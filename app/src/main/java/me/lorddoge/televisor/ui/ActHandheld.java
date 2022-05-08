package me.lorddoge.televisor.ui;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.lorddoge.televisor.FragPlayer;
import me.lorddoge.televisor.R;
import me.lorddoge.televisor.channel.Channel;
import me.lorddoge.televisor.channel.ChannelManager;
import me.lorddoge.televisor.channel.FluxusManager;
import me.lorddoge.televisor.utils.AssetUtils;

public class ActHandheld extends AppCompatActivity {

    private RecyclerView listChannels;

    private FrameLayout layoutPlayer;
    private FragPlayer fragPlayer;
    private ImageButton imgBtnFullscreen;

    private Dialog dialogFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);

        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.actionbar, null),
                new ActionBar.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        ChannelManager.getIns().load(AssetUtils.getTextFromAsset(this, "channels.json"));
        FluxusManager.getIns().load();
        //curChannel = ChannelManager.getIns().getAllChannels()[0];

        fragPlayer = (FragPlayer) getSupportFragmentManager().findFragmentById(R.id.FragPlayer);

        listChannels = findViewById(R.id.ListChannels);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listChannels.setLayoutManager(mLayoutManager);
        listChannels.setItemAnimator(new DefaultItemAnimator());
        listChannels.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            private int background;

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View row = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_channel, parent, false);

                TypedValue outValue = new TypedValue();
                parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                background = outValue.resourceId;

                return new RecyclerView.ViewHolder(row) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Channel channel = ChannelManager.getIns().getAllChannels()[position];

                ImageView imgViewBanner = holder.itemView.findViewById(R.id.ImgViewBanner);
                imgViewBanner.setImageDrawable(channel.getBanner(ActHandheld.this));

                TextView lblChannel = holder.itemView.findViewById(R.id.LblChannel);
                lblChannel.setText(channel.getName());

                TextView lblDesc = holder.itemView.findViewById(R.id.LblDesc);
                lblDesc.setText("未有節目資料");

                if (fragPlayer.getCurChannel() != null && fragPlayer.getCurChannel().equals(channel))
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.selected));
                else
                    holder.itemView.setBackgroundResource(background);

                holder.itemView.setOnClickListener(v -> {
                    if (fragPlayer.getCurChannel() != null && fragPlayer.getCurChannel().equals(channel))
                        return;

                    listChannels.getAdapter().notifyDataSetChanged();
                    fragPlayer.loadChannel(channel);
                });
            }

            @Override
            public int getItemCount() {
                return ChannelManager.getIns().getAllChannels().length;
            }
        });

        dialogFullscreen = new Dialog(this, R.style.AppTheme_Cinema) {
            @Override
            public void onBackPressed() {
                setFullscreen(false);
            }
        };

        layoutPlayer = findViewById(R.id.LayoutPlayer);
        imgBtnFullscreen = findViewById(R.id.ImgBtnFullscreen);
        imgBtnFullscreen.setOnClickListener(v -> setFullscreen(true));
    }

    void setFullscreen(boolean fullscreen) {
        ((ViewGroup) fragPlayer.getView().getParent()).removeView(fragPlayer.getView());
        if (fullscreen) {
            dialogFullscreen.addContentView(fragPlayer.getView(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            dialogFullscreen.show();
        } else {
            layoutPlayer.addView(fragPlayer.getView(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            dialogFullscreen.dismiss();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration conf) {
        super.onConfigurationChanged(conf);

        switch (conf.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setFullscreen(false);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setFullscreen(true);
                break;
        }
    }
}
