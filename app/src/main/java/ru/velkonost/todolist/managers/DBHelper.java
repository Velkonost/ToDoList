package ru.velkonost.todolist.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static ru.velkonost.todolist.managers.DBHelper.DBConstants.COLUMNS;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.TASKS;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("LOG_DB", "--- onCreate database ---");

        db.execSQL("create table " + COLUMNS + " ("
                + "id integer primary key autoincrement,"
                + "name text" + ");");

        db.execSQL("create table " + TASKS + " ("
                + "id integer primary key autoincrement,"
                + "columnId integer,"
                + "name text,"
                + "description text,"
                + "done integer" + ");");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class DBConstants {

        public static final String COLUMNS = "stodolist_columns";
        public static final String TASKS = "stodolist_task";


    }
}