package me.lorddoge.televisor.channel;

import android.net.Uri;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CableChannel extends Channel {
    private String cableChannelId;

    public CableChannel(String name, String origin, String cableChannelId) {
        super(name, origin);
        this.cableChannelId = cableChannelId;
    }

    @Override
    public Uri getStreamUri() {
        Uri uri = null;
        try {
            URL url = new URL("https://mobileapp.i-cable.com/iCableMobile/API/api.php?method=streamingGenerator");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes("type=live&channel_no=" + cableChannelId + "&quality=h");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            br.close();
            os.flush();
            os.close();
            conn.disconnect();

            JSONObject jsRes = new JSONObject(sb.toString());

            uri = Uri.parse(jsRes.getJSONObject("result")
                    .getString("stream"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return uri;
    }
}