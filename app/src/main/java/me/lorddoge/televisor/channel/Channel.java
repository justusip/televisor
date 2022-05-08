package me.lorddoge.televisor.channel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.lorddoge.televisor.R;
import me.lorddoge.televisor.network.Request;
import me.lorddoge.televisor.utils.AssetUtils;

public abstract class Channel {

    private String name;
    private String origin;
    private String nowId;

    private Programme[] programmes;

    private boolean enabled = false;

    public Channel(String name, String origin) {
        this.name = name;
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public String getNowId() {
        return nowId;
    }

    public void setNowId(String nowId) {
        this.nowId = nowId;
    }

    public Programme[] getProgrammes() {
        return programmes;
    }

    public void setProgrammes(Programme[] programmes) {
        this.programmes = programmes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract Uri getStreamUri();

    public Drawable getBanner(Context context) {
        if (!AssetUtils.assetExists(context, "thumbnails", name + ".png"))
            return ContextCompat.getDrawable(context, R.drawable.thumbnail_def);

        return AssetUtils.getDrawableFromAssets(context, "thumbnails/" + name + ".png");
    }

}
