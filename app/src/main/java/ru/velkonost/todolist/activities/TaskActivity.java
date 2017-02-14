package ru.velkonost.todolist.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.managers.DBHelper;

import static ru.velkonost.todolist.Constants.DESCRIPTION;
import static ru.velkonost.todolist.Constants.DONE;
import static ru.velkonost.todolist.Constants.ID;
import static ru.velkonost.todolist.Constants.LOG_TAG;
import static ru.velkonost.todolist.Constants.NAME;
import static ru.velkonost.todolist.activities.MainActivity.initToolbar;

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
        Intent intent = getIntent();
        taskId = intent.getIntExtra(ID, 0);

        Cursor c = dbHelper.queryByIdInTasks(taskId);

        if (c.moveToFirst()) {

            int nameTaskIndex = c.getColumnIndex(NAME);
            int doneTaskIndex = c.getColumnIndex(DONE);
            int descriptionTaskIndex = c.getColumnIndex(DESCRIPTION);

            name = c.getString(nameTaskIndex);
            isDone = c.getInt(doneTaskIndex) == 1;
            description = c.getString(descriptionTaskIndex);

            if (description == null) description = " ";

        } else Log.d(LOG_TAG, "0 rows");

        initToolbar(TaskActivity.this, toolbar, name);
        viewDescription.setText(description);

        if (isDone) {
            isDoneText.setText(getResources().getString(R.string.task_completed));
            isDoneText.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        else {
            isDoneText.setText(getResources().getString(R.string.task_dont_completed));
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
                        dbHelper.updateInTasks(name, description, taskId);
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
                builder.setTitle(getResources().getString(R.string.delete_task))
                        .setMessage(getResources().getString(R.string.are_you_sure))
//                .setIcon(R.drawable.ic_android_cat) МОЖНО ДОБАВИТЬ ИКОНКУ!
                        .setCancelable(false)
                        .setNegativeButton(getResources().getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dbHelper = new DBHelper(TaskActivity.this);
                                dbHelper.deleteInTasks(taskId);
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
