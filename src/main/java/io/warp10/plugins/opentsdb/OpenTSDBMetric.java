package io.warp10.plugins.opentsdb;

import java.util.Map;

/**
 * Created by rcoligno on 12/23/16.
 */
public class OpenTSDBMetric {

    private String              metric;
    private String              timestamp;
    private String              value;
    private Map<String, Object> tags;


    /**
     * Metric Name assignation
     * @param metric
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * Metric Timestamp assignation
     * @param timestamp
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = Integer.toString(timestamp);
    }

    /**
     * Metric Value assignation
     * @param value
     */
    public void setValue(int value) {
        this.value = Integer.toString(value);
    }

    /**
     * Metric value assignation
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Metric value assignation
     * @param value
     */
    public void setValue(float value) {
        this.value = Float.toString(value);
    }

    /**
     * Metric list of Tags assignation
     * @param tags
     */
    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }


    /**
     * Friendly console output
     * @return
     */
    public String toString() {
        return "Metric: " + metric + " ts: " + timestamp + " tags: " + tags.toString() + " value: " + value;
    }
}
