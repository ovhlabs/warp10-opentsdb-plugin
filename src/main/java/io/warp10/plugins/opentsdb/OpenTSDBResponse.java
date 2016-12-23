package io.warp10.plugins.opentsdb;

import java.util.Map;

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

    /*{
        "errors": [
        {
            "datapoint": {
            "metric": "sys.cpu.nice",
                    "timestamp": 1365465600,
                    "value": "NaN",
                    "tags": {
                "host": "web01"
            }
        },
            "error": "Unable to parse value to a number"
        }
    ],
    "failed": 1,
    "success": 0
    }*/
}
