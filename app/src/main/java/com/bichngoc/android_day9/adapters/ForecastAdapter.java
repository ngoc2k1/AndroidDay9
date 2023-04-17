package com.bichngoc.android_day9.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bichngoc.android_day9.R;
import com.bichngoc.android_day9.interfaces.IIClickItemRecycleView;
import com.bichngoc.android_day9.models.Day;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private ArrayList<Day> mDays;
    private IIClickItemRecycleView callback;

    public ForecastAdapter(ArrayList<Day> mDays) {
        this.mDays = mDays;
    }

    public void setCallback(IIClickItemRecycleView callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_forecast_day, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Day day = mDays.get(position);
        holder.tvDate.setText(day.getDate());
        HoursAdapter hoursAdapter = new HoursAdapter(day.getDataHours());
        holder.rvHours.setAdapter(hoursAdapter);
        hoursAdapter.notifyDataSetChanged();
        if (day.isExpand()) {
            holder.rvHours.setVisibility(View.VISIBLE);
            holder.imgArrow.setImageResource(R.drawable.ic_arrow_up);
        } else {
            holder.rvHours.setVisibility(View.GONE);
            holder.imgArrow.setImageResource(R.drawable.ic_arrow_down);
        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDate;
        ImageView imgArrow;
        RecyclerView rvHours;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            rvHours = itemView.findViewById(R.id.rvHours);
            imgArrow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.imgArrow) {
                if (callback != null) {
                    int posClick = getAdapterPosition();
//                    boolean isExpand = mDays.get(posClick).isExpand();
//                    if (isExpand) imgArrow.animate().setDuration(100).rotation(0f);
//                    else imgArrow.animate().setDuration(100).rotation(180f);
                    callback.onItemClickListener(view, posClick);
                }

            }
        }
    }
}
