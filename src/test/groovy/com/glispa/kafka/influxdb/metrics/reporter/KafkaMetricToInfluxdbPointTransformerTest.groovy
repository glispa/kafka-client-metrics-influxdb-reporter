package com.glispa.kafka.influxdb.metrics.reporter

import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class KafkaMetricToInfluxdbPointTransformerTest extends Specification {

    @Shared
    def NO_MATTER = 'no-matter'

    @Subject
    def transformer = new KafkaMetricToInfxdbPointTransformer('', [:])

    void "Metric name is passed as is"() {
        setup:
        def metricName = 'metric-name'
        def metric = Stub(Metric)
        metric.metricName() >> new MetricName(metricName, NO_MATTER, NO_MATTER, [:])

        when:
        def opt = transformer.transform(metric)
        then:
        opt.isPresent()
        def point = opt.get()
        point.lineProtocol().startsWith('metric-name')
    }

    void "Custom prefix can be added to measurment name"() {
        setup:
        def prefix = 'prefix'
        def metricName = 'metric-name'
        def metric = Stub(Metric)
        metric.metricName() >> new MetricName(metricName, NO_MATTER, NO_MATTER, [:])
        def transformerWithPrefix = new KafkaMetricToInfxdbPointTransformer(prefix, [:])

        when:
        def opt = transformerWithPrefix.transform(metric)
        then:
        opt.isPresent()
        def point = opt.get()
        point.lineProtocol().startsWith('prefix.metric-name')
    }

    void "Double values are passed as is"() {
        setup:
        def metric = Stub(Metric)
        metric.metricName() >> new MetricName(NO_MATTER, NO_MATTER, NO_MATTER, [:])
        metric.metricValue() >> 1.0

        when:
        def opt = transformer.transform(metric)
        then:
        opt.isPresent()
        def point = opt.get()
        point.lineProtocol().contains('value=1.0')
    }

    void "String values are passed as is"() {
        setup:
        def metric = Stub(Metric)
        metric.metricName() >> new MetricName(NO_MATTER, NO_MATTER, NO_MATTER, [:])
        metric.metricValue() >> 'abc'

        when:
        def opt = transformer.transform(metric)
        then:
        opt.isPresent()
        def point = opt.get()
        point.lineProtocol().contains('value="abc"')
    }

    @Unroll
    void "Infinity values are converted to empty Optional"(double value) {
        setup:
        def metric = Stub(Metric)
        metric.metricValue() >> value
        metric.metricName() >> new MetricName(NO_MATTER, NO_MATTER, NO_MATTER, [:])

        expect:
        !transformer.transform(metric).isPresent()

        where:
        value << [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN]
    }
}
