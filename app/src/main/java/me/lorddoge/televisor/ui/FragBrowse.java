package me.lorddoge.televisor.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;

import java.util.Date;

import me.lorddoge.televisor.R;
import me.lorddoge.televisor.channel.Category;
import me.lorddoge.televisor.channel.Channel;
import me.lorddoge.televisor.channel.ChannelManager;
import me.lorddoge.televisor.channel.Programme;
import me.lorddoge.televisor.utils.AssetUtils;

public class FragBrowse extends BrowseSupportFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle("頻道");
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.primary));
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.primary));

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        Presenter cardPresenter = new Presenter() {
            private int colourDefault;
            private int colourSelected;

            private void updateCardBackgroundColor(ImageCardView view, boolean selected) {
                int color = selected ? colourSelected : colourDefault;
                view.setInfoAreaBackgroundColor(color);
                view.setBackgroundColor(color);
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent) {
                colourDefault = ContextCompat.getColor(parent.getContext(), R.color.card_default);
                colourSelected = ContextCompat.getColor(parent.getContext(), R.color.card_selected);

                ImageCardView cardView = new ImageCardView(parent.getContext()) {
                    @Override
                    public void setSelected(boolean selected) {
                        updateCardBackgroundColor(this, selected);
                        super.setSelected(selected);
                    }
                };

                updateCardBackgroundColor(cardView, false);
                return new ViewHolder(cardView);
            }

            @Override
            public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
                Channel channel = (Channel) item;
                ImageCardView cardView = (ImageCardView) viewHolder.view;

                cardView.setTitleText(channel.getName());

                Date now = new Date();
                Programme[] programmes = channel.getProgrammes();
                if (programmes == null) {
                    cardView.setContentText("未有節目資料");
                } else {
                    for (Programme programme : programmes) {
                        if (programme.getStart().after(now))
                            continue;

                        if (programme.getEnd().before(now))
                            continue;

                        cardView.setContentText(programme.getName());
                        break;
                    }
                }

                cardView.setBadgeImage(AssetUtils.getDrawableFromAssets(viewHolder.view.getContext(),
                        "flags/" + channel.getOrigin().toUpperCase() + ".png"));
                cardView.setMainImageDimensions(300, 169);

                Channel curChannel = ((ActLeanback)getActivity()).getFragPlayer().getCurChannel();
                boolean active = curChannel.equals(channel);

                cardView.setEnabled(!active);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1)
                    cardView.setFocusable(!active);

                cardView.setAlpha(active ? 0.2f : 1f);

                cardView.setMainImage(channel.getBanner(cardView.getContext()));
            }

            @Override
            public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
                ImageCardView cardView = (ImageCardView) viewHolder.view;
                cardView.setBadgeImage(null);
                cardView.setMainImage(null);
            }
        };

        Category[] categories = ChannelManager.getIns().getCategories();
        for (Category category : categories) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (Channel channel : category.getChannels()) {
                listRowAdapter.add(channel);
            }

            HeaderItem header = new HeaderItem(rowsAdapter.size() - 1, category.getName());
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        setAdapter(rowsAdapter);

        getView().setBackgroundColor(Color.BLACK);
        getView().getBackground().setAlpha(200);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if (item instanceof Channel) {
                ((ActLeanback) getActivity()).switchChannel((Channel) item);
            } else {

            }
        });

        setOnItemViewSelectedListener((itemViewHolder, item, rowViewHolder, row) -> {

        });

        setHeadersTransitionOnBackEnabled(false);
    }

    public void refresh() {
        getAdapter().notifyItemRangeChanged(0, getAdapter().size());
    }
}