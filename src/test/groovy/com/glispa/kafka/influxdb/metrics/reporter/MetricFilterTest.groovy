package com.glispa.kafka.influxdb.metrics.reporter

import spock.lang.Specification

class MetricFilterTest extends Specification {

    void "Metrics are allowed with default white/black lists"() {
        setup:
        def filter = new MetricFilter('', '');
        def metric = new TestMetric('records-lag-max')

        expect:
        filter.isAllowed(metric)
    }

    void "Invalid regexes does not throw exception, default values are used instead"() {
        setup:
        def filter = new MetricFilter('\\', '\\');
        def metric = new TestMetric('records-lag-max')

        expect:
        filter.isAllowed(metric)
    }

     void "Whitelist regex works"() {
        setup:
        def filter = new MetricFilter('records-lag-max', '');
        def allowed = new TestMetric('records-lag-max')
        def notAllowed = new TestMetric('partition-1.records-lag-max')

        expect:
        filter.isAllowed(allowed)
        !filter.isAllowed(notAllowed)
     }

    void "OR whitelist regex works"() {
        setup:
        def filter = new MetricFilter('.*records-lag-max|.*records-consumed-rate', '');
        def allowed1 = new TestMetric('partition-1.records-lag-max')
        def allowed2 = new TestMetric('partition-128.records-consumed-rate')
        def notAllowed = new TestMetric('outgoing-byte-total')

        expect:
        filter.isAllowed(allowed1)
        filter.isAllowed(allowed2)
        !filter.isAllowed(notAllowed)
    }

    void "Blacklist regex works"() {
        setup:
        def filter = new MetricFilter('', '.*records.*');
        def notAllowed1 = new TestMetric('records-lag-max')
        def notAllowed2 = new TestMetric('partition-1.records-lag-max')
        def allowed = new TestMetric('outgoing-byte-total')

        expect:
        !filter.isAllowed(notAllowed1)
        !filter.isAllowed(notAllowed2)
        filter.isAllowed(allowed)
    }
}
