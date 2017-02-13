package ru.velkonost.todolist.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.fragments.ColumnsTabsFragmentAdapter;
import ru.velkonost.todolist.managers.DBHelper;

import static android.os.Build.ID;
import static ru.velkonost.todolist.managers.PhoneDataStorage.loadText;
import static ru.velkonost.todolist.managers.PhoneDataStorage.saveText;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private ViewPager viewPager;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    private DBHelper dbHelper;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        dbHelper = new DBHelper(this);

        initToolbar(MainActivity.this, toolbar, "To Do List");

        if (checkCookieId()) {


            Log.i("KEKE", String.valueOf(123));

        } else {
            saveText(MainActivity.this, ID, "ok");

            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            db.execSQL("create table columns ("
                    + "id integer primary key autoincrement,"
                    + "name text," + ");");

            db.execSQL("create table tasks ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "description text"
                    + "done integer" + ");");

            Log.d(LOG_TAG, "--- Insert in mytable: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение

            cv.put("name", "To do");
            cv.put("id", 1);
            // вставляем запись и получаем ее ID
            long rowID = db.insert("columns", null, cv);
            Log.i(LOG_TAG, "row inserted, ID = " + rowID);

        }

        dbHelper.close();

        initTabs();

    }

    public static void initToolbar(AppCompatActivity activity, Toolbar toolbar, String title) {

        toolbar.setTitle(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        activity.setSupportActionBar(toolbar);
    }


    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerColumns);
        final ColumnsTabsFragmentAdapter adapter
                = new ColumnsTabsFragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        if (adapter.getCount() < 4)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private boolean checkCookieId() {
        return loadText(MainActivity.this, ID).length() != 0;
    }

}
