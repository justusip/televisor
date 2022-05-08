package me.lorddoge.televisor.channel;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import me.lorddoge.televisor.network.Request;
import me.lorddoge.televisor.network.Response;

public class ChannelManager {

    private static ChannelManager ins = new ChannelManager();

    public static ChannelManager getIns() {
        return ins;
    }

    private ArrayList<Category> categories = new ArrayList<>();

    public void load(String configData) {
        categories.clear();
        try {
            JSONObject json = new JSONObject(configData);
            JSONObject jsCategories = json.getJSONObject("categories");
            for (int i = 0; i < jsCategories.names().length(); i++) {
                String categoryId = jsCategories.names().getString(i);
                JSONObject jsCategory = jsCategories.getJSONObject(categoryId);

                JSONArray jsChannels = jsCategory.getJSONArray("channels");
                ArrayList<Channel> channels = new ArrayList<>();
                for (int j = 0; j < jsChannels.length(); j++) {
                    JSONObject jsChannel = jsChannels.getJSONObject(j);
                    Channel channel = null;

                    String name = jsChannel.getString("name");
                    String origin = jsChannel.getString("origin");

                    if (jsChannel.has("fluxusStream")) {
                        channel = new FluxusChannel(name, origin, jsChannel.getString("fluxusStream"));
                    } else if (jsChannel.has("nowStream")) {
                        channel = new NowChannel(name, origin, jsChannel.getString("nowStream"));
                    } else if (jsChannel.has("cableStream")) {
                        channel = new CableChannel(name, origin, jsChannel.getString("cableStream"));
                    } else {
                        channel = new StaticChannel(name, origin, Uri.parse(jsChannel.getString("urlStream")));
                    }

                    if (jsChannel.has("enabled"))
                        channel.setEnabled(jsChannel.getBoolean("enabled"));

                    if (jsChannel.has("nowId"))
                        channel.setNowId(jsChannel.getString("nowId"));

                    channels.add(channel);
                }
                categories.add(new Category(categoryId,
                        jsCategory.getString("name"),
                        channels.toArray(new Channel[0])));
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void addProgrammes(String nowURL) {
        JSONObject jsRes = Request.get(nowURL).getJson();

        for (Channel channel : getAllChannels()) {
            try {
                String nowId = channel.getNowId();
                if (nowId == null)
                    continue;

                ArrayList<Programme> programmes = new ArrayList<>();

                if (channel.getProgrammes() != null)
                    programmes.addAll(Arrays.asList(channel.getProgrammes()));

                JSONArray jsProgrammes = jsRes
                        .getJSONObject("data")
                        .getJSONObject("chProgram")
                        .getJSONArray(nowId);

                for (int i = 0; i < jsProgrammes.length(); i++) {
                    JSONObject jsProgramme = jsProgrammes.getJSONObject(i);
                    programmes.add(new Programme(
                            jsProgramme.getString("name"),
                            jsProgramme.getString("synopsis"),
                            new Date(jsProgramme.getLong("start")),
                            new Date(jsProgramme.getLong("end"))
                    ));
                }
                channel.setProgrammes(programmes.toArray(new Programme[0]));
            } catch (JSONException e) {
                e.printStackTrace();
                channel.setProgrammes(null);
            }
        }
    }

    public void updateProgrammes() {
        for (Channel channel : getAllChannels())
            channel.setProgrammes(null);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        addProgrammes("https://nowtv.now.com/gw-epg/epg/zh_tw/" + format.format(now) + "/prf0/resp-genre/ch_G03.json");
        addProgrammes("https://nowtv.now.com/gw-epg/epg/zh_tw/" + format.format(tomorrow) + "/prf0/resp-genre/ch_G03.json");
    }

    public final Category[] getCategories() {
        return categories.toArray(new Category[0]);
    }

    public final Channel[] getAllChannels() {
        ArrayList<Channel> channels = new ArrayList<>();
        for (Category category : categories)
            channels.addAll(Arrays.asList(category.getChannels()));
        return channels.toArray(new Channel[0]);
    }

    public final Channel getChannelOffset(Channel channel, int offset) {
        Channel[] channels = getAllChannels();
        for (int i = 0; i < channels.length; i++) {
            if (channels[i] != channel)
                continue;
            int index = i + offset;
            while (index < 0)
                index += channels.length;
            while (index >= channels.length)
                index -= channels.length;
            return channels[index];
        }
        return null;
    }

    public final Channel getChannel(String channelName) {
        for (Category category : categories)
            for (Channel channel : category.getChannels())
                if (channel.getName().equals(channelName))
                    return channel;
        return null;
    }

}
