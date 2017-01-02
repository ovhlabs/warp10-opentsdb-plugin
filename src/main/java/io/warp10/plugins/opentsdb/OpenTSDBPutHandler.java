package io.warp10.plugins.opentsdb;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.warp10.continuum.Configuration;
import io.warp10.continuum.store.Constants;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;

/**
 * Created by rcoligno on 12/22/16.
 */
public class OpenTSDBPutHandler extends AbstractHandler {

    private URL             url;
    private Gson            gson = new GsonBuilder()
      .registerTypeAdapter(OpenTSDBMetricListType, new OpenTSDBMetricTypeAdaptater())
      .create();
    private static final Type       OpenTSDBMetricListType = new TypeToken<List<OpenTSDBMetric>>(){}.getType();
    // Base64 regexp
    private static final Pattern    authPattern = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");

    /**
     * Constructor with default Warp10 endpoint
     */
    public OpenTSDBPutHandler() {
        try {
            this.url = new URL("http://127.0.0.1:8080/api/v0/update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor with custom Warp10 endpoint
     * @param url
     */
    public OpenTSDBPutHandler(URL url) {
        this.url = url;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        baseRequest.setHandled(true);

        if (!request.getMethod().equals("POST")){
            response.sendError(500, "Wrong HTTP method used for opentsdb protocol");
            return;
        }

        String path = request.getRequestURI();
        if (!path.equals("/api/put")) {
            response.sendError(404, "Bad url");
            return;
        }

        String requestAuth = request.getHeader("Authorization");

        if (requestAuth == null) {
            response.sendError(500, "Missing basic auth authorization");
            return;
        }

        String token;

        if (requestAuth.toLowerCase().startsWith("basic ")) {
            Matcher m = authPattern.matcher(requestAuth);

            // Check if Authorization header have good format
            if (!m.find()) {
                response.sendError(500, "Bad basic auth Authorization format");
                return;
            }
            // Un base64
            byte[] tokenByte = Base64.getDecoder().decode(m.group(0));
            // concat user and password
            token = new String(tokenByte).replace(":", "");
        } else {
            token = requestAuth;
        }

        BufferedReader  requestBodyReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        List<OpenTSDBMetric> metrics;
        OpenTSDBResponse resBody = new OpenTSDBResponse();
        try {
            metrics= gson.fromJson(requestBodyReader, new TypeToken<List<OpenTSDBMetric>>(){}.getType());
        }
        catch (Exception e) {
            System.out.println(e.toString());
            response.sendError(400, "malformed JSON body : <a href=\"http://jsonlint.com\">http://jsonlint.com</a>");
            return;
        }

        byte[] body = OpenTSDBMetric.toBodyRequest(metrics);
        IOException err = sendToIngres(token, body);

        resBody.setFailed(0);
        resBody.setSuccess(metrics.size());

        if (null != err) {
            resBody.setFailed(metrics.size());
            resBody.setSuccess(0);
        }

        response.addHeader("Content-Type", "application/json");
        response.getWriter().print(gson.toJson(resBody));
    }

    /**
     * Send GTS to Warp10 Ingres
     * @param token
     * @return
     */
    private IOException sendToIngres (String token, byte[] body){
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty(Constants.getHeader(Configuration.HTTP_HEADER_UPDATE_TOKENX), token);
            //conn.setRequestProperty("Content-Type", "application/gzip");
            conn.setChunkedStreamingMode(16384);
            conn.connect();

            OutputStream os = conn.getOutputStream();
            //GZIPOutputStream out = new GZIPOutputStream(os);
            PrintWriter pw = new PrintWriter(os);

            os.write(body);
            os.flush();

            if (HttpServletResponse.SC_OK != conn.getResponseCode()) {
                throw new IOException(conn.getResponseMessage());
            }
            System.out.println(conn.getResponseCode());
        }
        catch (IOException e) {
            return e;
        }
        finally {
            if (null != conn) {
                conn.disconnect();
            }
            return null;
        }
    }
}
