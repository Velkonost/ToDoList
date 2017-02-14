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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.fragments.ColumnsTabsFragmentAdapter;
import ru.velkonost.todolist.managers.DBHelper;

import static ru.velkonost.todolist.Constants.COLUMN_ID;
import static ru.velkonost.todolist.Constants.DESCRIPTION;
import static ru.velkonost.todolist.Constants.DONE;
import static ru.velkonost.todolist.Constants.EXIST;
import static ru.velkonost.todolist.Constants.ID;
import static ru.velkonost.todolist.Constants.NAME;
import static ru.velkonost.todolist.fragments.ColumnsTabsFragmentAdapter.last;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.COLUMNS;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.TASKS;
import static ru.velkonost.todolist.managers.PhoneDataStorage.loadText;
import static ru.velkonost.todolist.managers.PhoneDataStorage.saveText;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private ViewPager viewPager;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    private DBHelper dbHelper;

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

        initToolbar(MainActivity.this, toolbar, getResources().getString(R.string.app_name));

        if (!checkCookieId()) {

            saveText(MainActivity.this, EXIST, getResources().getString(R.string.ok));

            ContentValues cvColumn = new ContentValues();
            ContentValues cvTask = new ContentValues();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            cvColumn.put(NAME, getResources().getString(R.string.column_example_name));
            cvColumn.put(ID, 1);

            cvTask.put(ID, 1);
            cvTask.put(NAME, getResources().getString(R.string.task_example_name));
            cvTask.put(COLUMN_ID, 1);
            cvTask.put(DESCRIPTION, getResources().getString(R.string.task_example_description));
            cvTask.put(DONE, 0);

            db.insert(COLUMNS, null, cvColumn);
            db.insert(TASKS, null, cvTask);

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
                    builder.setTitle(getResources().getString(R.string.add_column));

                    final EditText input = new EditText(MainActivity.this);
                    input.setHint(getResources().getString(R.string.enter_column_name));
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input)
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    columnName = input.getText().toString();

                                    if (columnName.length() != 0) {
                                        dbHelper = new DBHelper(MainActivity.this);

                                        ContentValues cvColumn = new ContentValues();

                                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                                        cvColumn.put(NAME, columnName);

                                        db.insert(COLUMNS, null, cvColumn);

                                        dbHelper.close();

                                        Intent intent = new Intent(MainActivity.this,
                                                MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        MainActivity.this.startActivity(intent);
                                        finish();
                                    } else dialog.cancel();

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                builder.setTitle(getResources().getString(R.string.change_column_name));

                final EditText inputName = new EditText(MainActivity.this);

                inputName.setText(currentColumnName);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(inputName)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prevCurrentColumnName = currentColumnName;
                                currentColumnName = inputName.getText().toString();

                                if (currentColumnName.length() != 0) {

                                    dbHelper = new DBHelper(MainActivity.this);
                                    ContentValues cv = new ContentValues();
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    cv.put(NAME, currentColumnName);

                                    db.update(COLUMNS, cv, "name = ?", new String[] {prevCurrentColumnName});
                                    dbHelper.close();

                                    Intent intent = new Intent(MainActivity.this,
                                            MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MainActivity.this.startActivity(intent);
                                    finish();

                                } else dialog.cancel();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        return loadText(MainActivity.this, EXIST).length() != 0;
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

}
