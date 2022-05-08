package me.lorddoge.televisor.channel;

import android.net.Uri;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NowChannel extends Channel {
    private String nowChannelId;

    public NowChannel(String name, String origin, String nowChannelId) {
        super(name, origin);
        this.nowChannelId = nowChannelId;
    }

    @Override
    public Uri getStreamUri() {
        Uri uri = null;
        try {
            URL url = null;

            if (nowChannelId.equals("099")) {
                url = new URL("https://api.viu.now.com/p8/2/getLiveURL");
            } else {
                url = new URL("https://hkt-mobile-api.nowtv.now.com/09/1/getLiveURL");
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("callerReferenceNo", "20140702122500");
            jsonParams.put("channelno", nowChannelId);
            jsonParams.put("mode", "prod");
            jsonParams.put("format", "HLS");

            if (nowChannelId.equals("099")) {
                jsonParams.put("deviceId", "8ff4f1db9007328090");
                jsonParams.put("deviceType", 5);
            } else {
                jsonParams.put("audioCode", "");
            }

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParams.toString());

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

            uri = Uri.parse(jsRes.getJSONObject("asset")
                    .getJSONObject("hls")
                    .getJSONArray("adaptive")
                    .getString(0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return uri;
    }

}