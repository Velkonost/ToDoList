package ru.velkonost.todolist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.models.Task;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<Task> data;
    private Context mContext;

    public TaskListAdapter(List<Task> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public TaskListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_column_task, parent, false);

        return new TaskListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskListViewHolder holder, int position) {
        final Task item = data.get(position);

        holder.title.setText(item.getName());
//
//        final int id = item.getId();
//
//        holder.title.setText(item.getName());
//        holder.title.setSelected(true);
//        holder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        holder.title.setHorizontallyScrolling(true);
//        holder.title.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);
//
//        holder.amount.setText(String.valueOf(item.getAmountParticipants()));
//        holder.amount.setVisibility(View.VISIBLE);
//
//        if (item.isBelong()){
//            holder.isBelong.setVisibility(View.VISIBLE);
//        }
//
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mContext, BoardCardActivity.class);
//                intent.putExtra(CARD_ID, id);
//                intent.putExtra(CARD_NAME, item.getName());
//
//                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {


        TextView title;

        public TaskListViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);

        }
    }
}
