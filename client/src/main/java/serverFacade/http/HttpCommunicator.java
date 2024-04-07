package serverFacade.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpCommunicator {
    private final String serverURL;

    public HttpCommunicator(String serverURL) {
        this.serverURL = serverURL;
    }

    public Map handleHTTP(String method, String endpoint, String authToken, Map body) throws IOException, URISyntaxException {
        URI uri = new URI(String.format("http://%s/%s", serverURL, endpoint));
        var http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        http.addRequestProperty("Content-Type", "application/json");
        if (!authToken.isEmpty()) {
            http.addRequestProperty("authorization", authToken);
        }

        if (body != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
        }

        http.connect();

        try (InputStream responseBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
            return new Gson().fromJson(inputStreamReader, Map.class);
        }
    }
}
