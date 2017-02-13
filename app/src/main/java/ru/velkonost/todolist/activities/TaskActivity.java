package ru.velkonost.todolist.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.managers.DBHelper;

import static ru.velkonost.todolist.managers.Initializatiors.initToolbar;

public class TaskActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_task;

    private Toolbar toolbar;

    private DBHelper dbHelper;

    private int taskId;

    private TextView viewDescription;

    private String name;
    private String description;
    private boolean isDone;

    private TextView isDoneText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewDescription = (TextView) findViewById(R.id.taskDescription);
        isDoneText = (TextView) findViewById(R.id.isDoneText);

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


    }
}
