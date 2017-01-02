package io.warp10.plugins.opentsdb;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rcoligno on 12/23/16.
 * This is an adaptator for Gson engine, it can handle an array of OpenTsDb metrics or just a OpenTSDB object metric
 */
public class OpenTSDBMetricTypeAdaptater implements JsonDeserializer<List<OpenTSDBMetric>> {

    @Override
    public List<OpenTSDBMetric> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {

        List<OpenTSDBMetric> metrics = new ArrayList<>();

        if (json.isJsonArray()) {
            // It's a JSON Array !
            for (JsonElement e : json.getAsJsonArray()) {
                metrics.add((OpenTSDBMetric) ctx.deserialize(e, OpenTSDBMetric.class));
            }
        }
        else if (json.isJsonObject()) {
            // It's a JSON Object
            metrics.add((OpenTSDBMetric) ctx.deserialize(json, OpenTSDBMetric.class));
        }
        else {
            // It's something else
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return metrics;
    }
}