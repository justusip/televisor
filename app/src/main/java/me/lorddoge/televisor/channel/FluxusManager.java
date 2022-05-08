package me.lorddoge.televisor.channel;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FluxusManager {

    private static FluxusManager ins = new FluxusManager();

    public static FluxusManager getIns() {
        return ins;
    }

    private ArrayList<FluxusSource> sources = new ArrayList<>();

    public void load() {
        new FetchFluxusSourcesTask().execute();
    }

    public class FetchFluxusSourcesTask extends AsyncTask<Void, Void, FluxusSource[]> {
        @Override
        protected FluxusSource[] doInBackground(Void... abysses) {
            ArrayList<FluxusSource> sources = new ArrayList<>();
            try {
                URL url = new URL("https://pastebin.com/raw/hfRRAcpy");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("#EXTINF"))
                        continue;

                    Pattern pattern = Pattern.compile("tvg-logo=\"(.*?)\" group-title=\"(.*?)\",(.+)");
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();

                    sources.add(new FluxusSource(matcher.group(3),
                            matcher.group(2),
                            Uri.parse(br.readLine()),
                            Uri.parse(matcher.group(1))));
                }
                br.close();
                conn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return sources.toArray(new FluxusSource[0]);
        }

        @Override
        protected void onPostExecute(FluxusSource[] fluxusSources) {
            sources.clear();
            sources.addAll(Arrays.asList(fluxusSources));
            /*for(FluxusSource s : fluxusSources) {
                Log.w("ass", "[" + s.getGroup() + "] " + s.getId());
            }*/
        }
    }

    public FluxusSource getFluxusSource(String id) {
        for (FluxusSource source : sources)
            if (source.getId().equals(id))
                return source;
        return null;
    }

    public static class FluxusSource {
        private String id;
        private String group;
        private ArrayList<Uri> streamUris;
        private Uri logo;

        public FluxusSource(String id, String group, Uri[] streamUris, Uri logo) {
            this.id = id;
            this.group = group;
            this.streamUris = new ArrayList<>(Arrays.asList(streamUris));
            this.logo = logo;
        }

        public FluxusSource(String id, String group, Uri url, Uri logo) {
            this(id, group, new Uri[]{url}, logo);
        }

        public String getId() {
            return id;
        }

        public String getGroup() {
            return group;
        }

        public Uri[] getStreamUris() {
            return streamUris.toArray(new Uri[0]);
        }

        public Uri getLogoUri() {
            return logo;
        }
    }
}


