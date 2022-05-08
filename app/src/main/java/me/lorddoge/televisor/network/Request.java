package me.lorddoge.televisor.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

    public static Response get(String url) {
        return request("GET", url);
    }

    public static Response post(String url) {
        return request("POST", url);
    }

    public static Response request(String method, String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(false);
            conn.setDoInput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            br.close();
            conn.disconnect();
            return new Response(conn.getResponseCode(), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
