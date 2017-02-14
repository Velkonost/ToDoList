package ru.velkonost.todolist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.todolist.R;
import ru.velkonost.todolist.activities.TaskActivity;
import ru.velkonost.todolist.managers.DBHelper;
import ru.velkonost.todolist.models.Task;

import static ru.velkonost.todolist.Constants.ID;

public class TaskListAdapter
        extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<Task> data;
    private Context mContext;

    private DBHelper dbHelper;


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
    public void onBindViewHolder(final TaskListViewHolder holder, int position) {
        final Task item = data.get(position);

        holder.title.setText(item.getName());

        holder.isDone = item.isDone();

        if (holder.isDone) holder.imageDone.setImageResource(R.mipmap.ic_checkbox_marked);
        else holder.imageDone.setImageResource(R.mipmap.ic_checkbox_blank_outline);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, TaskActivity.class);
                intent.putExtra(ID, item.getId());

                mContext.startActivity(intent);

            }
        });

        holder.imageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.isDone = !holder.isDone;

                dbHelper = new DBHelper(mContext);
                int done;

                if (holder.isDone){
                    holder.imageDone.setImageResource(R.mipmap.ic_checkbox_marked);
                    done = 1;
                }
                else{
                    holder.imageDone.setImageResource(R.mipmap.ic_checkbox_blank_outline);
                    done = 0;
                }

                dbHelper.updateDoneInTasks(done, item.getId());
                dbHelper.close();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        ImageView imageDone;
        TextView title;

        boolean isDone;

        public TaskListViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            imageDone = (ImageView) itemView.findViewById(R.id.checkbox_done);

        }
    }
}
