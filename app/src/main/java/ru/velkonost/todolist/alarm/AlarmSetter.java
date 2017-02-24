package ru.velkonost.todolist.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.managers.DBHelper;

import static ru.velkonost.todolist.Constants.CONTENT_TEXT;
import static ru.velkonost.todolist.Constants.CONTENT_TITLE;
import static ru.velkonost.todolist.Constants.DATE;
import static ru.velkonost.todolist.Constants.ID;
import static ru.velkonost.todolist.Constants.LOG_TAG;
import static ru.velkonost.todolist.Constants.TICKER;

public class AlarmSetter extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LOG_TAG, "reboot alarm received");

        DBHelper dbHelper = new DBHelper(context);

        Cursor cr = dbHelper.queryInTasks();

        if (cr.moveToFirst()) {
            int dateTaskIndex = cr.getColumnIndex(DATE);

            do {

                if (cr.getLong(dateTaskIndex) >= Calendar.getInstance().getTimeInMillis()) {

                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intentNotification = new Intent(context, RebootService.class);

                    intentNotification.putExtra(TICKER, "To do list: " + "123");
                    intentNotification.putExtra(CONTENT_TITLE, "Задача: " + "123");
                    intentNotification.putExtra(CONTENT_TEXT, "123");
                    intentNotification.putExtra(ID, Integer.parseInt("12"));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                            intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC_WAKEUP, cr.getLong(dateTaskIndex), pendingIntent);

                }

            } while (cr.moveToNext());
        } else Log.d(LOG_TAG, context.getResources().getString(R.string.zero_rows));

        cr.close();
        dbHelper.close();

    }
}
