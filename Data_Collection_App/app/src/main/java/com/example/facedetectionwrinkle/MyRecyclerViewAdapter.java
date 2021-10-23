package com.example.facedetectionwrinkle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Subjects> mData;
    private LayoutInflater mInflater;
    private static ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Subjects> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String animal = mData.get(position).getName();
        int count = mData.get(position).getCount();
        int sc1;
        int sc2;
        if (count > 5) {
            sc1 = 5;
            sc2 = count - 5;
        } else {
            sc1 = count;
            sc2 = 0;
        }
        holder.subNameTV.setText(animal);
        String sess1Text = holder.sess1TV.getText().toString() + sc1;
        holder.sess1TV.setText(sess1Text);
        String sess2Text = holder.sess2TV.getText().toString() + sc2;
        holder.sess2TV.setText(sess2Text);
        String lastUpdate = mData.get(position).getLastUpdate();

//        LocalDateTime localDateTime = LocalDateTime.parse(lastUpdate);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
//        String output = formatter.format(localDateTime);


        ZonedDateTime dateTime = ZonedDateTime.parse(lastUpdate);

        String output = dateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("dd MMM hh:mm a"));
        String lastUpdateTxt = holder.lastUpdateTV.getText().toString() + output;
        holder.lastUpdateTV.setText(lastUpdateTxt);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subNameTV;
        TextView sess1TV;
        TextView sess2TV;
        TextView lastUpdateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            subNameTV = itemView.findViewById(R.id.SubNameTV);
            sess1TV = itemView.findViewById(R.id.sess1TV);
            sess2TV = itemView.findViewById(R.id.sess2TV);
            lastUpdateTV = itemView.findViewById(R.id.lastUpdateTV);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Subjects getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}