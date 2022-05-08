package me.lorddoge.televisor.channel;

import android.net.Uri;

public class FluxusChannel extends Channel {
    private String fluxusId;

    public FluxusChannel(String name, String origin, String fluxusId) {
        super(name, origin);
        this.fluxusId = fluxusId;
    }

    @Override
    public Uri getStreamUri() {
        FluxusManager.FluxusSource source = FluxusManager.getIns().getFluxusSource(fluxusId);
        if (source == null)
            return null;
        return source.getStreamUris()[0];
    }
}