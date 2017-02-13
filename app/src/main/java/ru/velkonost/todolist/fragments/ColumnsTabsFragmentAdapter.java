package ru.velkonost.todolist.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ru.velkonost.todolist.managers.DBHelper;

public class ColumnsTabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    public static int last = 0;

    private DBHelper dbHelper;

    public ColumnsTabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("KEKE", String.valueOf(position));
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }


    private void initTabsMap(Context context) {
        tabs = new HashMap<>();

        dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("columns", null, null, null, null, null, null);

        int i = 0;

        if (c.moveToFirst()) {


            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");

            do {



                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("myLogs",
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex));

                tabs.put(i, ColumnFragment.getInstance(context, c.getInt(idColIndex),
                        c.getString(nameColIndex)));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла

                last = i;
                i ++;
            } while (c.moveToNext());
        } else
            Log.d("myLogs", "0 rows");
        c.close();
        dbHelper.close();


        tabs.put(i, AddColumnFragment.getInstance(context));
    }
}
