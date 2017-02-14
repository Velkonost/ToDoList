package ru.velkonost.todolist.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.activities.MainActivity;
import ru.velkonost.todolist.adapters.TaskListAdapter;
import ru.velkonost.todolist.managers.DBHelper;
import ru.velkonost.todolist.models.Task;

import static ru.velkonost.todolist.Constants.COLUMN_ID;
import static ru.velkonost.todolist.Constants.DESCRIPTION;
import static ru.velkonost.todolist.Constants.DONE;
import static ru.velkonost.todolist.Constants.ID;
import static ru.velkonost.todolist.Constants.LOG_TAG;
import static ru.velkonost.todolist.Constants.NAME;
import static ru.velkonost.todolist.managers.DBHelper.DBConstants.TASKS;

public class ColumnFragment extends BaseTabFragment {

    private static final int LAYOUT = R.layout.fragment_column;

    private int columnId;

    private String cardName;
    private String cardDescription;


    private List<Task> data;
    private ArrayList<String> cids;

    private DBHelper dbHelper;

    public static ColumnFragment getInstance(Context context, int columnId, String columnName) {
        Bundle args = new Bundle();
        ColumnFragment fragment = new ColumnFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setColumnId(columnId);

        fragment.setTitle(columnName);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        data = new ArrayList<>();
        cids = new ArrayList<>();

        FloatingActionButton addCardButton = (FloatingActionButton) view.findViewById(R.id.btnAddCard);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams  params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, dp2px(20), 0, dp2px(20));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.add_task));

                final EditText inputName = new EditText(context);
                inputName.setLayoutParams(params);

                inputName.setHint(getResources().getString(R.string.enter_task_name));
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(inputName);

                final EditText inputDesc = new EditText(context);
                inputDesc.setLayoutParams(params);

                inputDesc.setHint(getResources().getString(R.string.enter_task_description));
                layout.addView(inputDesc);


                builder.setView(layout)

                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cardName = inputName.getText().toString();
                                cardDescription = inputDesc.getText().toString();

                                if (cardName.length() != 0) {

                                    dbHelper = new DBHelper(context);

                                    ContentValues cvTask = new ContentValues();

                                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                                    cvTask.put(NAME, cardName);
                                    cvTask.put(COLUMN_ID, columnId);
                                    cvTask.put(DONE, 0);
                                    cvTask.put(DESCRIPTION, cardDescription);

                                    db.insert(TASKS, null, cvTask);

                                    dbHelper.close();

                                    Intent intent = new Intent(context,
                                            MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    context.startActivity(intent);
                                    getActivity().finish();

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
            }
        });

        dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TASKS, null, null, null, null, null, null);

        int i = 0;

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idTaskIndex = c.getColumnIndex(ID);
            int columnIdTaskIndex = c.getColumnIndex(COLUMN_ID);
            int nameTaskIndex = c.getColumnIndex(NAME);
            int descriptionTaskIndex = c.getColumnIndex(DESCRIPTION);
            int doneTaskIndex = c.getColumnIndex(DONE);

            do {

                if (columnId == c.getInt(columnIdTaskIndex))
                    data.add(new Task(
                            c.getInt(idTaskIndex), columnId, i, c.getString(nameTaskIndex),
                            c.getString(descriptionTaskIndex), c.getInt(doneTaskIndex) == 1
                    ));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "0 rows");

        c.close();
        dbHelper.close();

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerViewColumn);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new TaskListAdapter(data, getContext()));


        return view;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

}
