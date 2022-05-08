package me.lorddoge.televisor.channel;

import android.net.Uri;

public class StaticChannel extends Channel {
    private Uri uri;

    public StaticChannel(String name, String origin, Uri uri) {
        super(name, origin);
        this.uri = uri;
    }

    @Override
    public Uri getStreamUri() {
        return uri;
    }
}