package ru.velkonost.todolist.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.TimeNotification;
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

public class ColumnFragment extends BaseTabFragment {

    private int columnId;
    private String cardName;
    private String cardDescription;

    private List<Task> data;
    private ArrayList<String> cids;

    private DatePickerDialog datePicker;

    private int mYear, mMonth, mDay, mHour, mMinute;

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
        view = inflater.inflate(R.layout.fragment_column, container, false);

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

                final EditText inputDate = new EditText(context);
                inputDate.setLayoutParams(params);
                inputDate.setHint(getResources().getString(R.string.enter_task_description));

                final EditText inputTime = new EditText(context);
                inputTime.setLayoutParams(params);
                inputTime.setHint(getResources().getString(R.string.enter_task_description));


                /**
                 * Использует для получения даты.
                 */
                Calendar newCalendar = Calendar.getInstance();

                /**
                 * Требуется для дальнейшего преобразования даты в строку.
                 */
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat
                        = new SimpleDateFormat("dd-MM-yyyy");


                final String[] date = {""};

                /**
                 * Создает объект и инициализирует обработчиком события выбора даты и данными для даты по умолчанию.
                 */
                datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    // функция onDateSet обрабатывает шаг 2: отображает выбранные нами данные в элементе EditText
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newCal = Calendar.getInstance();
                        newCal.set(year, monthOfYear, dayOfMonth);

                        date[0] = "";
                        date[0] += dateFormat.format(newCal.getTime());
                        inputDate.setText(dateFormat.format(newCal.getTime()));
                    }
                },
                        newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));

                mHour = newCalendar.get(Calendar.HOUR_OF_DAY);
                mMinute = newCalendar.get(Calendar.MINUTE);

                final TimePickerDialog timePicker = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                date[0] = date[0].substring(0, 10);

                                if (minute < 10) {
                                    date[0] += " 0" + hourOfDay + " 0" + minute;
                                    inputTime.setText("0" + hourOfDay + ":0" + minute);
                                } else {
                                    date[0] += " " + hourOfDay + " " + minute;
                                    inputTime.setText(hourOfDay + ":" + minute);
                                }
                            }
                        }, mHour, mMinute, false);

                layout.addView(inputDate);
                layout.addView(inputTime);

                inputDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePicker.show();
                    }
                });

                datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        timePicker.show();
                    }
                });

                builder.setView(layout)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cardName = inputName.getText().toString();
                                cardDescription = inputDesc.getText().toString();

                                if (cardName.length() != 0) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH mm");
                                    long timeInMilliseconds = 0;

                                    try {
                                        Date mDate = sdf.parse(date[0]);
                                        timeInMilliseconds = mDate.getTime();
                                        Log.i("KEKE", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                                        Log.i("KEKE", String.valueOf(timeInMilliseconds));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                    Intent intentNotification = new Intent(context, TimeNotification.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                                            intentNotification, PendingIntent.FLAG_CANCEL_CURRENT);
                                    am.set(AlarmManager.RTC_WAKEUP, timeInMilliseconds, pendingIntent);



                                    dbHelper = new DBHelper(context);
                                    dbHelper.insertInTask(cardName, cardDescription, columnId);
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

        Cursor c = dbHelper.queryInTasks();
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
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, getResources().getString(R.string.zero_rows));

        c.close();
        dbHelper.close();

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerViewColumn);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new TaskListAdapter(data, getContext()));


        return view;
    }

    public void chooseDate(View w){
        datePicker.show();
    }


//    private void initDateBirthdayDatePicker(){
//        /**
//         * Использует для получения даты.
//         */
//        Calendar newCalendar = Calendar.getInstance();
//
//        /**
//         * Требуется для дальнейшего преобразования даты в строку.
//         */
//        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat
//                = new SimpleDateFormat("dd-MM-yyyy");
//
//        /**
//         * Создает объект и инициализирует обработчиком события выбора даты и данными для даты по умолчанию.
//         */
//        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            // функция onDateSet обрабатывает шаг 2: отображает выбранные нами данные в элементе EditText
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newCal = Calendar.getInstance();
//                newCal.set(year, monthOfYear, dayOfMonth);
//                editBirthday.setText(dateFormat.format(newCal.getTime()));
//            }
//        },
//                newCalendar.get(Calendar.YEAR),
//                newCalendar.get(Calendar.MONTH),
//                newCalendar.get(Calendar.DAY_OF_MONTH));
//    }


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
