package me.lorddoge.televisor.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

public class WakeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("ass", "a");

        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("settings put secure enabled_accessibility_services " +
                    "me.lorddoge.televisor/me.lorddoge.televisor.background.ShortcutService\n");
            os.writeBytes("settings put secure accessibility_enabled 1\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        Bundle extras = new Bundle();
        extras.putString(Notification.EXTRA_BACKGROUND_IMAGE_URI,
                Uri.parse("").toString());

        NotificationManager ntManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int notificationId = 0;

        for (Category category : ChannelManager.getIns().getCategories()) {
            for (Channel channel : category.getChannels()) {
                if (!channel.isRecommended())
                    continue;

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                stackBuilder.addParentStack(ActLeanback.class);

                Intent detailsIntent = new Intent(context, ActLeanback.class);
                detailsIntent.putExtra("channelName", channel.getName());
                stackBuilder.addNextIntent(detailsIntent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap bitmap = null;
                if (AssetUtils.assetExists(context, "thumbnails", channel.getName() + ".png"))
                    bitmap = AssetUtils.getBitmapFromAsset(context, "thumbnails/" + channel.getName() + ".png");
                Notification notification = new NotificationCompat.BigPictureStyle(
                        new NotificationCompat.Builder(context)
                                .setAutoCancel(true)
                                .setContentTitle(channel.getName())
                                .setContentText(channel.getDesc())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setLocalOnly(true)
                                .setOngoing(true)
                                .setGroup("Channels")
                                .setSortKey("1.0")
                                .setColor(Color.RED)
                                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(bitmap)
                                .setContentIntent(pendingIntent)
                                .setExtras(extras))
                        .build();
                ntManager.notify(++notificationId, notification);
            }
        }*/
    }
}