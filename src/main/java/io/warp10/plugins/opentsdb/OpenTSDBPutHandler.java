package io.warp10.plugins.opentsdb;
/**
 * Created by rcoligno on 12/22/16.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;

public class OpenTSDBPutHandler extends AbstractHandler {

    private final URL url;
    private static final Type OpenTSDBMetricListType = new TypeToken<List<OpenTSDBMetric>>(){}.getType();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(OpenTSDBMetricListType, new OpenTSDBMetricTypeAdaptater())
            .create();
    // Base64 regexp
    private static final Pattern authPattern = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");

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
        System.out.println(path);
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
        System.out.println(token);

        BufferedReader reader   = new BufferedReader(new InputStreamReader(request.getInputStream()));
        try {
            List<OpenTSDBMetric> metrics = gson.fromJson(reader, new TypeToken<List<OpenTSDBMetric>>(){}.getType());
            System.out.println(metrics.toString());
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        System.out.println("end");
    }

    private boolean sendToIngres (String token){

        HttpURLConnection conn = null;

        try {
            // url = plugin.opentsdb.warp10.endpoint
            conn = (HttpURLConnection) url.openConnection();

            /*conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty(Constants.getHeader(Configuration.HTTP_HEADER_UPDATE_TOKENX), token);
            //conn.setRequestProperty("Content-Type", "application/gzip");
            conn.setChunkedStreamingMode(16384);
            conn.connect();

            OutputStream os = conn.getOutputStream();
            //GZIPOutputStream out = new GZIPOutputStream(os);
            PrintWriter pw = new PrintWriter(os);

            BufferedReader br = request.getReader();

            while(true) {
                String line = br.readLine();
                if (null == line) {
                    break;
                }
                parse(pw,line,labels);
            }

            br.close();

            pw.flush();

            if (HttpServletResponse.SC_OK != conn.getResponseCode()) {
                throw new IOException(conn.getResponseMessage());
            }*/throw new IOException(conn.getResponseMessage());
        }
        catch (IOException e) {
            return false;
        }
        finally {
            if (null != conn) {
                conn.disconnect();
            }
            return false;
        }
    }
}
