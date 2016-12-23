package io.warp10.plugins.opentsdb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

    /**
     * Serialise object into Warp10 input format
     * @return
     */
    public String toGTS() {

        String gts = this.timestamp;
        // No Geo support
        gts += "// " + metric + "{";
        if (null != tags && tags.size() > 0) {

            List<String> warpLabels = new ArrayList<>();
            for (Map.Entry<String, Object> tag : tags.entrySet())
            {
                warpLabels.add(tag.getKey() + "=" + tag.getValue());
            }
            gts += String.join(",", warpLabels);
        }

        return gts + "} " + value;
    }

    /**
     * Join a List of Metrics Object into HTTP request reader
     * @param metrics
     * @return
     */
    public static byte[] toBodyRequest(List<OpenTSDBMetric> metrics) {

        List<String> bodyEntries = new ArrayList<>();
        for( OpenTSDBMetric metric : metrics) {
            bodyEntries.add(metric.toGTS());
        }
        return String.join("\n", bodyEntries).getBytes();
    }
}