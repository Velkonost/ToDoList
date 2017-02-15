package ru.velkonost.todolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static ru.velkonost.todolist.Constants.CONTENT_TEXT;
import static ru.velkonost.todolist.Constants.CONTENT_TITLE;
import static ru.velkonost.todolist.Constants.TICKER;

public class RebootService extends BroadcastReceiver {

    Notification myNotication;

    @Override
    public void onReceive(Context context, Intent intent) {
//
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentNotification = new Intent(context, TimeNotification.class);

        intentNotification.putExtra(TICKER, intent.getStringExtra(TICKER));
        intentNotification.putExtra(CONTENT_TITLE, intent.getStringExtra(CONTENT_TITLE));
        intentNotification.putExtra(CONTENT_TEXT, intent.getStringExtra(CONTENT_TEXT));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 1000, pendingIntent);

//        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //Интент для активити, которую мы хотим запускать при нажатии на уведомление
//        Intent intentTL = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intentTL, 0);
//
//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setAutoCancel(false);
//        builder.setTicker("this is ticker text");
//        builder.setContentTitle("TOdO Notification");
//        builder.setContentText("time out!");
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentIntent(pendingIntent);
//        builder.setOngoing(false);
//        builder.setSubText("This is subtext...");
//        builder.setNumber(100);
//        builder.build();
//        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(500);
//
//        myNotication = builder.getNotification();
//        nm.notify(11, myNotication);

    }
}
