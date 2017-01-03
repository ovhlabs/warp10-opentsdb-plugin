package io.runabove.warp.plugins.opentsdb;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rcoligno on 12/23/16.
 */
public class OpenTSDBMetric {

    private String              metric;
    private Long                timestamp;
    private String              timestampWarp;
    private String              value;
    private Map<String, Object> tags;

    /**
     * Metric Timestamp assignation
     */
    public void formatTimestamp() {
	    String str = Long.toString(this.timestamp);
        // If less than 2^32, assume it's in seconds
        // (in millis that would be Thu Feb 19 18:02:47 CET 1970)
        if (this.timestamp < 0xFFFFFFFF) {
            str += "000000";
        } else {
            str += "000";
        }
        this.timestampWarp = str += "000";
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

        this.formatTimestamp();
        String gts = this.timestampWarp;
        // No Geo support
        gts += "// " + metric + "{";
        if (null != tags && tags.size() > 0) {

            List<String> warpLabels = new ArrayList<>();
            try {
                for (Map.Entry<String, Object> tag : tags.entrySet()) {
                    warpLabels.add(URLEncoder.encode(tag.getKey(), "UTF-8")
                           + "=" + URLEncoder.encode((String) tag.getValue(), "UTF-8"));
                }
                gts += String.join(",", warpLabels).replace("+", "%20");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
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