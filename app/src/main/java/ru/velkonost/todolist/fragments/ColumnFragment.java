package ru.velkonost.todolist.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.adapters.TaskListAdapter;
import ru.velkonost.todolist.managers.DBHelper;
import ru.velkonost.todolist.models.Task;

public class ColumnFragment extends AbstractTabFragment{

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        data = new ArrayList<>();
        cids = new ArrayList<>();

//        FloatingActionButton addCardButton = (FloatingActionButton) view.findViewById(R.id.btnAddCard);
//        addCardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LinearLayout layout = new LinearLayout(context);
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                LinearLayout.LayoutParams  params =
//                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, dp2px(20), 0, dp2px(20));
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Title");
//
//                final EditText inputName = new EditText(context);
//                inputName.setLayoutParams(params);
//
//                inputName.setHint("Enter card's name...");
//                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
//                layout.addView(inputName);
//
//                final EditText inputDesc = new EditText(context);
//                inputDesc.setLayoutParams(params);
//
//                inputDesc.setHint("Enter card's description...");
//                layout.addView(inputDesc);
//
//
//                builder.setView(layout)
//
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                cardName = inputName.getText().toString();
//                                cardDescription = inputDesc.getText().toString();
//
//                                if (cardName.length() != 0) {
//
//                                    AddCard addCard = new AddCard();
//                                    addCard.execute();
//
//                                    changeActivityCompat(getActivity());
//
//                                } else dialog.cancel();
//
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//
//
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });

        dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("task", null, null, null, null, null, null);

        int i = 0;

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idTaskIndex = c.getColumnIndex("id");
            int columnIdTaskIndex = c.getColumnIndex("columnId");
            int nameTaskIndex = c.getColumnIndex("name");
            int descriptionTaskIndex = c.getColumnIndex("description");
            int doneTaskIndex = c.getColumnIndex("done");

            do {

                if (columnId == c.getInt(columnIdTaskIndex))
                    data.add(new Task(
                            c.getInt(idTaskIndex), columnId, i, c.getString(nameTaskIndex),
                            c.getString(descriptionTaskIndex), c.getInt(doneTaskIndex) == 1
                    ));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else Log.d("myLogs", "0 rows");

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
