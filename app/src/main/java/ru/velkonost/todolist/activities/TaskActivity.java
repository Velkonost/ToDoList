package ru.velkonost.todolist.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.managers.DBHelper;

import static ru.velkonost.todolist.managers.Initializatiors.initToolbar;

public class TaskActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_task;

    private Toolbar toolbar;

    private DBHelper dbHelper;

    private int taskId;

    private TextView viewDescription;

    private String name = "";
    private String description = "";
    private boolean isDone;

    private TextView isDoneText;

    private Menu menu;

    private EditText editCardName;

    private RecyclerView recyclerViewColumns;
    private View popupViewColumns;
    public static PopupWindow popupWindowColumns;


    private ViewSwitcher switcher;
    private String text;

    private EditText mEditText;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewDescription = (TextView) findViewById(R.id.taskDescription);
        isDoneText = (TextView) findViewById(R.id.isDoneText);
        editCardName = (EditText) findViewById(R.id.editCardName);

        dbHelper = new DBHelper(this);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        taskId = intent.getIntExtra("id", 0);

        Cursor c = db.query("task",
                null,
                "id = ?",
                new String[] {String.valueOf(taskId)},
                null, null, null);

        if (c.moveToFirst()) {

            int nameTaskIndex = c.getColumnIndex("name");
            int doneTaskIndex = c.getColumnIndex("done");
            int descriptionTaskIndex = c.getColumnIndex("description");

            name = c.getString(nameTaskIndex);
            isDone = c.getInt(doneTaskIndex) == 1;
            description = c.getString(descriptionTaskIndex);

            if (description == null) description = " ";

        } else
            Log.d("myLogs", "0 rows");

        initToolbar(TaskActivity.this, toolbar, name);
        viewDescription.setText(description);

        if (isDone) {
            isDoneText.setText("Завершено");
            isDoneText.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        else {
            isDoneText.setText("Не завершено");
            isDoneText.setTextColor(getResources().getColor(R.color.colorRed));
        }



        toolbar.setNavigationIcon(R.mipmap.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        switcher = (ViewSwitcher) findViewById(R.id.switcherBoardDescription);
        mEditText = (EditText) findViewById(R.id.editTaskDescription);

        ((TextView) findViewById(R.id.taskDescription))
                .setText(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                ? Html.fromHtml(description,
                                Html.FROM_HTML_MODE_LEGACY)
                                : Html.fromHtml(description)
                );

        ((EditText) findViewById(R.id.editTaskDescription))
                .setText(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                ? Html.fromHtml(description,
                                Html.FROM_HTML_MODE_LEGACY)
                                : Html.fromHtml(description)
                );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                toolbar.setTitle("");
                editCardName.setVisibility(View.VISIBLE);
                editCardName.setText(name);

                showNext();


                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);

                menu.findItem(R.id.action_agree).setVisible(true);

                menu.findItem(R.id.action_agree).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        name = editCardName.getText().toString();
                        description = getText();

                        toolbar.setTitle(name);
                        changeText();
                        showNext();

                        editCardName.setVisibility(View.INVISIBLE);

                        menu.findItem(R.id.action_settings).setVisible(true);
                        menu.findItem(R.id.action_delete).setVisible(true);

                        menu.findItem(R.id.action_agree).setVisible(false);

                        dbHelper = new DBHelper(TaskActivity.this);
                        ContentValues cv = new ContentValues();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        cv.put("name", name);
                        cv.put("description", name);

                        db.update("task", cv, "id = ?", new String[] {String.valueOf(taskId)});
                        dbHelper.close();

                        InputMethodManager inputMethodManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        getCurrentFocus().clearFocus();

                        return false;
                    }
                });


                break;
            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Удаление задачи")
                        .setMessage("Вы уверены?")
//                .setIcon(R.drawable.ic_android_cat) МОЖНО ДОБАВИТЬ ИКОНКУ!
                        .setCancelable(false)
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dbHelper = new DBHelper(TaskActivity.this);
                                ContentValues cv = new ContentValues();
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                db.delete("task",
                                        "id = ?",
                                        new String[] {String.valueOf(taskId)});
                                dbHelper.close();

                                TaskActivity.this.startActivity(new Intent(TaskActivity.this,
                                        MainActivity.class));

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNext(){ switcher.showNext(); }
    public void setText(String text) { this.text = text; }
    public void changeText() { viewDescription.setText(mEditText.getText().toString()); }
    public String getText() { return mEditText.getText().toString(); }

}
