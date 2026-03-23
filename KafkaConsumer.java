package karate.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.internal.Utils.isEmpty;
import static java.util.Objects.isNull;

public class KarateKafkaConsumer implements Runnable {

  private static final Logger logger =
      LoggerFactory.getLogger(KarateKafkaConsumer.class.getName());

  private final ObjectMapper objectMapper = new ObjectMapper();

  private KafkaConsumer<Object, Object> kafka;
  private final CountDownLatch startupLatch = new CountDownLatch(1);
  private final CountDownLatch shutdownLatch = new CountDownLatch(1);

  private boolean partitionsAssigned = false;

  private Pattern keyFilter;                 // Java regex
  private String valueFilter;               // JsonPath expression
  private Map<String, String> headerFilters; // exact header key/value match

  private final BlockingQueue<String> outputList = new LinkedBlockingQueue<>();

  public KarateKafkaConsumer(String kafkaTopic, Map<String, String> consumerProperties) {
    this(kafkaTopic, consumerProperties, null, null, null);
  }

  public KarateKafkaConsumer(String kafkaTopic) {
    this(kafkaTopic, getDefaultPropertiesAsMap(), null, null, null);
  }

  public KarateKafkaConsumer(
      String kafkaTopic,
      String keyFilterExpression,
      String valueFilterExpression) {
    this(kafkaTopic, getDefaultPropertiesAsMap(), keyFilterExpression, valueFilterExpression, null);
  }

  public KarateKafkaConsumer(
      String kafkaTopic,
      Map<String, String> consumerProperties,
      String keyFilterExpression,
      String valueFilterExpression) {
    this(kafkaTopic, consumerProperties, keyFilterExpression, valueFilterExpression, null);
  }

  // New constructor with header filters
  public KarateKafkaConsumer(
      String kafkaTopic,
      Map<String, String> consumerProperties,
      String keyFilterExpression,
      String valueFilterExpression,
      Map<String, String> headerFilters) {

    setKeyValueFilters(keyFilterExpression, valueFilterExpression);
    setHeaderFilters(headerFilters);

    Properties cp = new Properties();
    if (consumerProperties != null) {
      for (String key : consumerProperties.keySet()) {
        cp.setProperty(key, consumerProperties.get(key));
      }
    }
    create(kafkaTopic, cp);
  }

  private static Map<String, String> getDefaultPropertiesAsMap() {
    Properties cp = getDefaultProperties();
    Map<String, String> map = new HashMap<>();
    for (String name : cp.stringPropertyNames()) {
      map.put(name, cp.getProperty(name));
    }
    return map;
  }

  // All constructors eventually call this
  private void create(String kafkaTopic, Properties cp) {
    kafka = new KafkaConsumer<>(cp);
    kafka.subscribe(Collections.singleton(kafkaTopic));

    Thread t = new Thread(this);
    t.start();

    logger.debug("Waiting for consumer to be ready.");
    try {
      startupLatch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while waiting for consumer startup", e);
    }
    logger.debug("consumer is ready");
  }

  /**
   * Sets the predicate to filter kafka records based on key or/and value
   *
   * @param keyFilterExpression Java regular expression pattern
   * @param valueFilterExpression JsonPath expression
   */
  private void setKeyValueFilters(String keyFilterExpression, String valueFilterExpression) {
    if (!isEmpty(keyFilterExpression)) {
      this.keyFilter = Pattern.compile(keyFilterExpression);
    }
    if (!isEmpty(valueFilterExpression)) {
      this.valueFilter = valueFilterExpression;
    }
  }

  private void setHeaderFilters(Map<String, String> headerFilters) {
    if (headerFilters != null && !headerFilters.isEmpty()) {
      this.headerFilters = new HashMap<>(headerFilters);
    }
  }

  public static Properties getDefaultProperties() {
    Properties cp = new Properties();
    cp.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    cp.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    cp.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    cp.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "karate-kafka-default-consumer-group");
    return cp;
  }

  public void close() {
    logger.debug("asking consumer to shutdown.");
    kafka.wakeup();
    try {
      shutdownLatch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while waiting for consumer shutdown", e);
    }
  }

  public void signalWhenReady() {
    if (!partitionsAssigned) {
      logger.debug("checking partition assignment");
      Set<TopicPartition> partitions = kafka.assignment();
      if (partitions.size() > 0) {
        partitionsAssigned = true;
        logger.debug("partitions assigned to consumer.");
        startupLatch.countDown();
      }
    }
  }

  @Override
  public void run() {
    try {
      while (true) {
        ConsumerRecords<Object, Object> records = kafka.poll(Duration.ofMillis(500));
        signalWhenReady();

        if (records != null) {
          for (ConsumerRecord<Object, Object> record : records) {
            logger.debug("*** Consumer got data ****");

            Object key = record.key();
            Object value = record.value();
            Headers recordHeaders = record.headers();

            logger.debug("Partition : {} Offset : {}", record.partition(), record.offset());
            if (key == null) {
              logger.debug("Key : null");
            } else {
              logger.debug("Key : {} Type: {}", key, key.getClass().getName());
            }
            logger.debug("Value : {} Type: {}", value, value == null ? "null" : value.getClass().getName());

            if (!isNull(keyFilter) && !filterByKey(key)) {
              continue;
            }
            if (!isNull(valueFilter) && !filterByValue(value)) {
              continue;
            }
            if (!filterByHeaders(recordHeaders)) {
              continue;
            }

            String str = convertToJsonString(key, value, recordHeaders);

            logger.debug("Consuming record. key: {}, value: {}", key, value);
            outputList.put(str);
          }
        }
      }
    } catch (WakeupException e) {
      logger.debug("Got WakeupException");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      logger.debug("consumer is shutting down...");
      kafka.close();
      logger.debug("consumer is now shut down.");
      shutdownLatch.countDown();
    }
  }

  private boolean filterByHeaders(Headers headers) {
    if (headerFilters == null || headerFilters.isEmpty()) {
      return true;
    }

    for (Map.Entry<String, String> entry : headerFilters.entrySet()) {
      Header h = headers.lastHeader(entry.getKey());
      if (h == null) {
        return false;
      }

      String actualValue = new String(h.value(), StandardCharsets.UTF_8);
      if (!Objects.equals(actualValue, entry.getValue())) {
        return false;
      }
    }
    return true;
  }

  private String convertToJsonString(Object key, Object value, Headers recordHeaders) {
    Map<String, Object> result = new LinkedHashMap<>();
    Map<String, String> headersMap = new LinkedHashMap<>();

    for (Header h : recordHeaders) {
      headersMap.put(h.key(), new String(h.value(), StandardCharsets.UTF_8));
    }

    result.put("key", key);

    if (value == null) {
      result.put("value", null);
    } else {
      try {
        Object jsonValue = objectMapper.readValue(value.toString(), Object.class);
        result.put("value", jsonValue);
      } catch (Exception e) {
        result.put("value", value);
      }
    }

    if (!headersMap.isEmpty()) {
      result.put("headers", headersMap);
    }

    try {
      return objectMapper.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize Kafka record to JSON", e);
    }
  }

  /**
   * @param value The kafka record value
   */
  private boolean filterByValue(Object value) {
    try {
      return !isNull(value)
          && !JsonPath.parse(value.toString()).read(valueFilter, List.class).isEmpty();
    } catch (JsonPathException e) {
      logger.error("Exception while trying to filter value", e);
    }
    return false;
  }

  /**
   * Checks whether the given string matches the keyFilter regular expression
   */
  private boolean filterByKey(Object key) {
    return !isNull(key) && keyFilter.matcher(key.toString()).find();
  }

  /**
   * @return The next available kafka record in the Queue. Blocks if necessary.
   */
  public synchronized String take() throws InterruptedException {
    logger.debug("take() called");
    return outputList.take();
  }

  /**
   * @param n The number of records to read
   * @return A JSON array string of the next n available kafka records.
   */
  public synchronized String take(int n) throws InterruptedException {
    logger.debug("take(n) called");
    List<String> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      list.add(outputList.take());
    }
    return list.toString();
  }

  /**
   * @param timeout maximum time in milliseconds to wait for a record
   * @return The next available kafka record in the Queue, or null if timeout expires
   */
  public synchronized String poll(long timeout) throws InterruptedException {
    logger.debug("poll() called");
    return outputList.poll(timeout, TimeUnit.MILLISECONDS);
  }
}