package io.runabove.warp.plugins.opentsdb;

/**
 * Created by rcoligno on 12/23/16.
 * Only implement Summary response not Detailed
 */
public class OpenTSDBResponse {

    private int                 failed;
    private int                 success;

    /**
     * Number of store failed points
     * @param failed
     */
    public void setFailed(int failed) {
        this.failed = failed;
    }

    /**
     * Number of store Success points
     * @param success
     */
    public void setSuccess(int success) {
        this.success = success;
    }
}
