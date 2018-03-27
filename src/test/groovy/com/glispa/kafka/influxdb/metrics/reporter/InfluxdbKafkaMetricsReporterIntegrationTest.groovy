package com.glispa.kafka.influxdb.metrics.reporter

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Query
import org.influxdb.dto.QueryResult
import org.influxdb.impl.InfluxDBResultMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class InfluxdbKafkaMetricsReporterIntegrationTest extends Specification {

    private Logger log = LoggerFactory.getLogger(InfluxdbKafkaMetricsReporterIntegrationTest.class)

    // the same values as in docker-compose.yml and .gitlab-ci.yml
    @Shared
    def INFLUXDB_SERVER = System.getProperty('influxdb.host', 'localhost') + ':8086'
    @Shared
    def INFLUXDB_USER = 'user'
    @Shared
    def INFLUXDB_USER_PASSWORD = 'upassword'
    @Shared
    def BOOTSTRAP_SERVERS = System.getProperty('kafka.host', '127.0.0.1') + ':9092'
    @Shared
    def INFLUXDB_DATABASE= 'dbname'
    @Shared
    def TOPIC = "topic-${UUID.randomUUID().toString()}".toString()
    @Shared
    def NUM_MESSAGES = 10
    @Shared
    def CONSUMER_CLIENT_ID = 'test-consumer-id'

    def "metrics work"() {
        setup:
        runProducer()
        runConsumer()

        when:
        def queryResult = queryInfluxdb()

        then:
        def resultMapper = new InfluxDBResultMapper()
        List<RecordsConsumedTotalDto> results = resultMapper.toPOJO(queryResult, RecordsConsumedTotalDto.class)
        def dto = results.get(0)
        dto.getTopic() == TOPIC
        dto.getGroup() == 'consumer-fetch-manager-metrics'
        dto.getClientId() == CONSUMER_CLIENT_ID
        dto.getCustomTag() == 'custom-value'
        dto.getValue() == NUM_MESSAGES
    }

    void runProducer() {
        def producer = createProducer()
        try {
            NUM_MESSAGES.times {
                def record = new ProducerRecord<>(TOPIC, it.longValue(), "Message ${it}".toString())
                producer.send(record).get()
            }
        } catch (Exception e) {
            log.error("Can not produce messages to Kafka", e)
        } finally {
            producer.flush()
            producer.close()
        }
    }

    void runConsumer() {
        def consumer = createConsumer()

        def consumedMessages = 0;

        while (consumedMessages < NUM_MESSAGES) {
            def consumerRecords = consumer.poll(1000)
            consumerRecords.forEach({ consumedMessages++ })
            consumer.commitAsync();
        }
        consumer.close()
    }

    Consumer<Long, String> createConsumer() {
        def props = new Properties()
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName())
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        props.put(ConsumerConfig.GROUP_ID_CONFIG, 'test-consumer')
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, CONSUMER_CLIENT_ID)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, 'earliest')
        props.put(ConsumerConfig.METRIC_REPORTER_CLASSES_CONFIG,
                'com.glispa.kafka.influxdb.metrics.reporter.InfluxdbKafkaMetricsReporter')
        props.put(InfluxdbConfig.INFLUXDB_SERVER_CONFIG, INFLUXDB_SERVER)
        props.put(InfluxdbConfig.INFLUXDB_USER_CONFIG, INFLUXDB_USER)
        props.put(InfluxdbConfig.INFLUXDB_PASSWORD_CONFIG, INFLUXDB_USER_PASSWORD)
        props.put(InfluxdbConfig.INFLUXDB_DATABASE_CONFIG, INFLUXDB_DATABASE)
        props.put(InfluxdbConfig.INFLUXDB_DATABASE_CONFIG, INFLUXDB_DATABASE)
        props.put(InfluxdbConfig.INFLUXDB_CUSTOM_TAGS_CONFIG, 'custom-tag=custom-value')

        def consumer = new KafkaConsumer<>(props)

        consumer.subscribe([TOPIC])
        return consumer
    }

    Producer<Long, String> createProducer() {
        def props = new Properties()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName())
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
        props.put(ProducerConfig.CLIENT_ID_CONFIG, 'test-producer');
        return new KafkaProducer<>(props);
    }

    QueryResult queryInfluxdb() {
        def influx = InfluxDBFactory.connect(
                "http://${INFLUXDB_SERVER}", INFLUXDB_USER, INFLUXDB_USER_PASSWORD)
        def query = new Query("select * from \"records-consumed-total\" where topic = '${TOPIC}'", INFLUXDB_DATABASE)
        return influx.query(query)
    }
}
