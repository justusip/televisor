package me.lorddoge.televisor;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import me.lorddoge.televisor.channel.Channel;


public class FragPlayer extends Fragment {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private String userAgent;

    private Channel curChannel = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        playerView = view.findViewById(R.id.PlayerView);
        initPlayer();
    }

    public Channel getCurChannel() {
        return curChannel;
    }

    public void loadChannel(Channel channel) {
        if (channel == null)
            return;
        this.curChannel = channel;

        new LoadChannelTask().execute(channel);
    }

    private class LoadChannelTask extends AsyncTask<Channel, Void, Uri> {
        @Override
        protected void onPreExecute() {
            player.stop(true);
        }

        @Override
        protected Uri doInBackground(Channel... channels) {
            return channels[0].getStreamUri();
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (player == null)
                return;

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            player.prepare(hlsMediaSource, true, false);
        }
    }

    private void initPlayer() {
        if (player != null)
            return;
        player = ExoPlayerFactory.newSimpleInstance(getActivity());
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getActivity(), "來源無效，請轉睇其它臺。", Toast.LENGTH_LONG).show();
            }
        });
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        playerView.setPlayer(player);
        playerView.setUseController(false);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER);
        playerView.setBackgroundColor(Color.BLACK);

        userAgent = Util.getUserAgent(getActivity(), "televisor");
    }

    private void releasePlayer() {
        if (player == null)
            return;

        player.release();
        player = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayer();
        loadChannel(curChannel);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }
}
