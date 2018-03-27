package com.glispa.kafka.influxdb.metrics.reporter

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CustomTagsPropertyParserTest extends Specification {

    @Subject
    def parser = new CustomTagsPropertyParser()

    @Unroll
    def "Correct property string is parsed successfully"(String property, Map<String, String> expectedMap) {
        expect:
        parser.parseMapProperty(property) == expectedMap

        where:
        property                   | expectedMap
        ''                         | [:]
        'key=value'                | ['key': 'value']
        'key1=value1,key2=value2' | ['key1': 'value1', 'key2': 'value2']
    }
}
