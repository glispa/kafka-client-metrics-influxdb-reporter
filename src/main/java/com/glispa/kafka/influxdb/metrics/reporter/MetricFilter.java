package com.glispa.kafka.influxdb.metrics.reporter;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.Metric;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
class MetricFilter {

    private final Pattern whitelist;
    private final Pattern blacklist;

    MetricFilter(String whitelist, String blacklist) {
        Objects.requireNonNull(whitelist, "Whitelist pattern is null");
        Objects.requireNonNull(blacklist, "Blacklist pattern is null");
        this.whitelist = compile(whitelist);
        this.blacklist = compile(blacklist);
    }

    private Pattern compile(String regex) {
        if (regex.isEmpty()) {
            return null;
        }
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            log.error("Can not compile regex pattern " + regex + ". Ignore this value!");
            return null;
        }

    }

    boolean isAllowed(Metric metric) {
        String name = metric.metricName().name();
        return isAllowedByWhitelist(name) && isAllowedByBlacklist(name);
    }

    private boolean isAllowedByWhitelist(String name) {
        return whitelist == null || whitelist.matcher(name).matches();
    }

    private boolean isAllowedByBlacklist(String name) {
        return blacklist == null || !blacklist.matcher(name).matches();
    }
}
