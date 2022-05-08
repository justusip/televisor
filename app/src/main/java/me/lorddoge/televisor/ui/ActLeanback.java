package me.lorddoge.televisor.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.lorddoge.televisor.FragPlayer;
import me.lorddoge.televisor.R;
import me.lorddoge.televisor.channel.Channel;
import me.lorddoge.televisor.channel.ChannelManager;
import me.lorddoge.televisor.channel.FluxusManager;
import me.lorddoge.televisor.channel.Programme;
import me.lorddoge.televisor.utils.AssetUtils;

public class ActLeanback extends FragmentActivity {

    private FragPlayer fragPlayer = new FragPlayer();

    private FrameLayout layoutPopup;
    private TextView lblChannelName;
    private ImageView imgViewChannelThumbnail;
    private TextView lblPgmPriDesc;
    private TextView lblPgmPriStart;
    private TextView lblPgmPriName;
    private TextView lblPgmPriEnd;
    private TextView lblPgmSecName;
    private TextView lblPgmSecTime;
    private TextView lblPgmTriName;
    private TextView lblPgmTriTime;
    private ProgressBar prgBarPgmPri;

    private FragBrowse fragBrowse = new FragBrowse();
    private FrameLayout layoutBrowse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leanback);

        layoutBrowse = findViewById(R.id.LayoutBrowse);
        layoutBrowse.setVisibility(View.GONE);

        fragBrowse = (FragBrowse) getSupportFragmentManager().findFragmentById(R.id.FragBrowse);
        fragPlayer = (FragPlayer) getSupportFragmentManager().findFragmentById(R.id.FragPlayer);

        layoutPopup = findViewById(R.id.LayoutPopup);
        layoutPopup.setAlpha(0);
        lblChannelName = findViewById(R.id.LblChannelName);
        imgViewChannelThumbnail = findViewById(R.id.ImgViewChannelThumbnail);
        lblPgmPriDesc = findViewById(R.id.LblPgmPriName);
        lblPgmPriStart = findViewById(R.id.LblPgmPriDesc);
        lblPgmPriName = findViewById(R.id.LblPgmPriStart);
        lblPgmPriEnd = findViewById(R.id.LblPgmPriEnd);
        lblPgmSecName = findViewById(R.id.LblPgmSecName);
        lblPgmSecTime = findViewById(R.id.LblPgmSecTime);
        lblPgmTriName = findViewById(R.id.LblPgmTriName);
        lblPgmTriTime = findViewById(R.id.LblPgmTriTime);
        prgBarPgmPri = findViewById(R.id.PrgBarPgmPri);

        ChannelManager.getIns().load(AssetUtils.getTextFromAsset(this, "channels.json"));
        FluxusManager.getIns().load();
        switchChannel(ChannelManager.getIns().getAllChannels()[0]);

        new LoadEPG().execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_W:
                switchChannel(ChannelManager.getIns().getChannelOffset(fragPlayer.getCurChannel(), 1));
                return true;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_S:
                switchChannel(ChannelManager.getIns().getChannelOffset(fragPlayer.getCurChannel(), -1));
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (layoutBrowse.getVisibility() == View.VISIBLE)
                    return true;
                layoutBrowse.setVisibility(View.VISIBLE);
                layoutPopup.animate().cancel();
                layoutPopup.setAlpha(0);
                fragBrowse.refresh();
                return true;
            case KeyEvent.KEYCODE_INFO:
                if (layoutBrowse.getVisibility() == View.VISIBLE)
                    return true;
                showPopup(fragPlayer.getCurChannel());
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (layoutBrowse.getVisibility() == View.VISIBLE) {
            layoutBrowse.setVisibility(View.GONE);
            showPopup(fragPlayer.getCurChannel());
            return;
        }
        if (layoutPopup.getAlpha() != 0) {
            closePopup();
            return;
        }

        super.onBackPressed();
    }

    private void showPopup(Channel channel) {
        lblChannelName.setText(channel.getName());
        imgViewChannelThumbnail.setImageDrawable(channel.getBanner(this));

        Date now = new Date();
        Programme[] programmes = fragPlayer.getCurChannel().getProgrammes();
        if (programmes == null) {
            findViewById(R.id.LayoutEmpty).setVisibility(View.VISIBLE);
            findViewById(R.id.LayoutProgrammes).setVisibility(View.GONE);
        } else {
            findViewById(R.id.LayoutEmpty).setVisibility(View.GONE);
            findViewById(R.id.LayoutProgrammes).setVisibility(View.VISIBLE);
            for (int i = 0; i < programmes.length; i++) {
                if (programmes[i].getStart().after(now))
                    continue;

                if (programmes[i].getEnd().before(now))
                    continue;

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                lblPgmPriDesc.setText(programmes[i].getName());
                lblPgmPriStart.setText(programmes[i].getDesc());
                lblPgmPriName.setText(format.format(programmes[i].getStart()));
                lblPgmPriEnd.setText(format.format(programmes[i].getEnd()));
                lblPgmSecName.setText(programmes[i + 1].getName());
                lblPgmSecTime.setText(format.format(programmes[i + 1].getStart()) + " - " + format.format(programmes[i + 1].getEnd()));
                lblPgmTriName.setText(programmes[i + 2].getName());
                lblPgmTriTime.setText(format.format(programmes[i + 2].getStart()) + " - " + format.format(programmes[i + 2].getEnd()));

                long millisElapsed = now.getTime() - programmes[i].getStart().getTime();
                long millisLeft = programmes[i].getEnd().getTime() - programmes[i].getStart().getTime();
                prgBarPgmPri.setProgress((int) (100L * millisElapsed / millisLeft));

                break;
            }
        }

        layoutPopup.animate().cancel();
        layoutPopup.setAlpha(1);
        layoutPopup.animate()
                .setStartDelay(4000)
                .setDuration(300)
                .alpha(0);
    }

    private void closePopup() {
        layoutPopup.animate().cancel();
        layoutPopup.setAlpha(0);
    }

    public void switchChannel(Channel channel) {
        if (channel == null)
            return;

        if (layoutBrowse.getVisibility() == View.VISIBLE)
            layoutBrowse.setVisibility(View.GONE);

        fragPlayer.loadChannel(channel);
        showPopup(channel);
    }

    private class LoadEPG extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... abysses) {
            ChannelManager.getIns().updateProgrammes();
            return null;
        }

        @Override
        protected void onPostExecute(Void abyss) {
            showPopup(fragPlayer.getCurChannel());
        }
    }

    public FragPlayer getFragPlayer() {
        return fragPlayer;
    }
}
