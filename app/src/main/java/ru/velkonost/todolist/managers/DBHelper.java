package ru.velkonost.todolist.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.velkonost.todolist.R;

import static ru.velkonost.todolist.Constants.COLUMN_ID;
import static ru.velkonost.todolist.Constants.DESCRIPTION;
import static ru.velkonost.todolist.Constants.DONE;
import static ru.velkonost.todolist.Constants.ID;
import static ru.velkonost.todolist.Constants.NAME;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.COLUMNS;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.TASKS;

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);

        this.mContext = context;
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

    public void insertInColumns(String columnName) {

        ContentValues cvColumn = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();

        cvColumn.put(NAME, columnName);

        db.insert(COLUMNS, null, cvColumn);

    }

    public void insertInTask(String name, String description, int columnId) {
        ContentValues cvTask = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        cvTask.put(NAME, name);
        cvTask.put(COLUMN_ID, columnId);
        cvTask.put(DONE, 0);
        cvTask.put(DESCRIPTION, description);
        db.insert(TASKS, null, cvTask);
    }

    public void updateNameInColumns(String newColumnName, String prevColumnName) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        cv.put(NAME, newColumnName);

        db.update(COLUMNS, cv, "name = ?", new String[] {prevColumnName});
    }

    public void updateInTasks(String name, String description, int taskId) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(NAME, name);
        cv.put(DESCRIPTION, description);

        db.update(TASKS, cv, "id = ?", new String[] {String.valueOf(taskId)});
    }

    public void updateDoneInTasks(int done, int id) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        cv.put(DONE, done);
        db.update(TASKS, cv, "id = ?", new String[] {String.valueOf(id)});
    }

    public void deleteInTasks(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TASKS,
                "id = ?",
                new String[] {String.valueOf(taskId)});
    }

    public Cursor queryByIdInTasks(int taskId) {
        return this.getWritableDatabase().query(TASKS,
                null,
                "id = ?",
                new String[] {String.valueOf(taskId)},
                null, null, null);
    }

    public Cursor queryInTasks() {
        return this.getWritableDatabase().query(TASKS, null, null, null, null, null, null);
    }

    public Cursor queryInColumns() {
        return this.getWritableDatabase().query(COLUMNS, null, null, null, null, null, null);
    }

    public void addBase () {
        ContentValues cvColumn = new ContentValues();
        ContentValues cvTask = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();

        cvColumn.put(NAME, mContext.getResources().getString(R.string.column_example_name));
        cvColumn.put(ID, 1);

        cvTask.put(ID, 1);
        cvTask.put(NAME,
                mContext.getResources().getString(R.string.task_example_name));
        cvTask.put(COLUMN_ID, 1);
        cvTask.put(DESCRIPTION,
                mContext.getResources().getString(R.string.task_example_description));
        cvTask.put(DONE, 0);

        db.insert(COLUMNS, null, cvColumn);
        db.insert(TASKS, null, cvTask);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class DBConstants {

        public static final String COLUMNS = "todolist_columns";
        public static final String TASKS = "todolist_task";


    }
}