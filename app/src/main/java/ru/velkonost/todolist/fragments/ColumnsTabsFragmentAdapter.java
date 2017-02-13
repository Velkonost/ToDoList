package ru.velkonost.todolist.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

import ru.velkonost.todolist.managers.DBHelper;

public class ColumnsTabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;
    public static int last = 0;

    private DBHelper dbHelper;

    public ColumnsTabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();

        dbHelper = new DBHelper(context);

        ContentValues cv = new ContentValues();

        tabs.put(0, ColumnFragment.getInstance(context, 0, "Name"));
        tabs.put(1, AddColumnFragment.getInstance(context));
    }

}
