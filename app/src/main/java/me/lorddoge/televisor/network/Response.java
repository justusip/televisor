package me.lorddoge.televisor.network;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {

    private int status;
    private String content;

    public Response(int status, String content) {
        this.status = status;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public JSONObject getJson() {
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
