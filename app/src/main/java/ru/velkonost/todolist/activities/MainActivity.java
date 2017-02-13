package ru.velkonost.todolist.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.fragments.ColumnsTabsFragmentAdapter;
import ru.velkonost.todolist.managers.DBHelper;

import static android.os.Build.ID;
import static ru.velkonost.todolist.fragments.ColumnsTabsFragmentAdapter.last;
import static ru.velkonost.todolist.managers.Initializatiors.initToolbar;
import static ru.velkonost.todolist.managers.PhoneDataStorage.loadText;
import static ru.velkonost.todolist.managers.PhoneDataStorage.saveText;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private ViewPager viewPager;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    private DBHelper dbHelper;

    final String LOG_TAG = "myLogs";

    private String columnName;

    private String currentColumnName;
    private String prevCurrentColumnName;

    private int currentColumnPosition;

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

            ContentValues cvColumn = new ContentValues();
            ContentValues cvTask = new ContentValues();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

//            db.delete("columns", null, null);
//            db.delete("task", null, null);

            db.execSQL("create table columns ("
                    + "id integer primary key autoincrement,"
                    + "name text," + ");");

            db.execSQL("create table task ("
                    + "id integer primary key autoincrement,"
                    + "columnId integer,"
                    + "name text,"
                    + "description text,"
                    + "done integer" + ");");

            // подготовим данные для вставки в виде пар: наименование столбца - значение

            cvColumn.put("name", "To do");
            cvColumn.put("id", 1);

            cvTask.put("id", 1);
            cvTask.put("name", "Ознакомиться с приложением");
            cvTask.put("columnId", 1);
            cvTask.put("description", "Потыкать. Немного там, немного тут");
            cvTask.put("done", 0);

            // вставляем запись и получаем ее ID
            db.insert("columns", null, cvColumn);
            db.insert("task", null, cvTask);

        }

        dbHelper.close();

        initTabs();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_column, menu);
        return true;
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerColumns);
        final ColumnsTabsFragmentAdapter adapter
                = new ColumnsTabsFragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        final boolean[] dialogOpen = {false};

        final LinearLayout[] tabStrip = {((LinearLayout) tabLayout.getChildAt(0))};

        tabStrip[0].getChildAt(last + 1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (!dialogOpen[0]) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Добавить колонку");

                    final EditText input = new EditText(MainActivity.this);
                    input.setHint("Введите название...");
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    columnName = input.getText().toString();

                                    if (columnName.length() != 0) {
                                        dbHelper = new DBHelper(MainActivity.this);

                                        ContentValues cvColumn = new ContentValues();

                                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                                        cvColumn.put("name", columnName);
                                        cvColumn.put("id", last);

                                        db.insert("columns", null, cvColumn);

                                        dbHelper.close();

                                        Intent intent = new Intent(MainActivity.this,
                                                MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        MainActivity.this.startActivity(intent);
                                        finish();
                                    } else dialog.cancel();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            dialogOpen[0] = false;
                        }
                    });
                    dialogOpen[0] = true;
                }

                return true;
            }
        });

        if (adapter.getCount() < 4) tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getGroupId();

        switch (id){
            case 0:

                currentColumnName = tabLayout.getTabAt(viewPager.getCurrentItem()).getText().toString();
                currentColumnPosition = viewPager.getCurrentItem() + 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Изменить название колонки");

                final EditText inputName = new EditText(MainActivity.this);

                inputName.setText(currentColumnName);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(inputName)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prevCurrentColumnName = currentColumnName;
                                currentColumnName = inputName.getText().toString();

                                if (currentColumnName.length() != 0) {

                                    dbHelper = new DBHelper(MainActivity.this);
                                    ContentValues cv = new ContentValues();
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    cv.put("name", currentColumnName);

                                    db.update("columns", cv, "name = ?", new String[] {prevCurrentColumnName});
                                    dbHelper.close();

                                    Intent intent = new Intent(MainActivity.this,
                                            MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MainActivity.this.startActivity(intent);
                                    finish();

                                } else dialog.cancel();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkCookieId() {
        return loadText(MainActivity.this, ID).length() != 0;
    }

}
