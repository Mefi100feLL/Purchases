package com.PopCorp.Purchases.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.PopCorp.Purchases.R;
import com.PopCorp.Purchases.model.Shop;
import com.PopCorp.Purchases.services.LoadingSalesService;

public class NotificationManager {

    public static final int NOTIFY_ID_LOADING_SALES = 1;

    private final Context context;
    private final android.app.NotificationManager notificationManager;

    public NotificationManager(Context context) {
        this.context = context;
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void loadingSalesForShop(Shop shop) {
        showNotifyLoadingSales(shop, 0, 0, true);
    }

    public void setProgressOfLoadingSales(Shop shop, int progress, int max){
        showNotifyLoadingSales(shop, progress, max, false);
    }

    public void showNotifyLoadingSales(Shop shop, int progress, int max, boolean indeterminate){
        Intent notificationIntent = new Intent(context, LoadingSalesService.class);
        notificationIntent.putExtra(LoadingSalesService.EXTRA_STOP, true);
        notificationIntent.setAction("stopService");
        PendingIntent stopIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(context.getResources().getString(R.string.notification_loading_sales));
        builder.setProgress(max, progress, indeterminate);
        builder.setContentText(shop.getName());
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= 16) {
            builder.addAction(R.mipmap.ic_stop_white_24dp, context.getString(R.string.notification_action_stop), stopIntent);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            builder.setContentIntent(stopIntent);
        }
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(NOTIFY_ID_LOADING_SALES, notification);
    }

    public void cancelLoading() {
        notificationManager.cancel(NOTIFY_ID_LOADING_SALES);
    }
}
