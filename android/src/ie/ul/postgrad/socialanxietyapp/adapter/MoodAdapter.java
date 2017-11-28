package ie.ul.postgrad.socialanxietyapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ie.ul.postgrad.socialanxietyapp.MoodEntry;
import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 20-Nov-17.
 */

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MyViewHolder> {

    private List<MoodEntry> moodList;

    public MoodAdapter(List<MoodEntry> list) {
        moodList = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView moodFace;
        public TextView title, date;

        public MyViewHolder(View itemView) {
            super(itemView);
            moodFace = (ImageView) itemView.findViewById(R.id.mood_image);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MoodEntry mood = moodList.get(position);
        switch (mood.getRating()) {
            case 0:
                holder.moodFace.setImageResource(R.drawable.mood_0);
                break;
            case 1:
                holder.moodFace.setImageResource(R.drawable.mood_1);
                break;
            case 2:
                holder.moodFace.setImageResource(R.drawable.mood_2);
                break;
            case 3:
                holder.moodFace.setImageResource(R.drawable.mood_3);
                break;
            case 4:
                holder.moodFace.setImageResource(R.drawable.mood_4);
                break;
        }
        holder.title.setText(mood.getDescription());
        holder.date.setText(mood.getDate());
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }


}
