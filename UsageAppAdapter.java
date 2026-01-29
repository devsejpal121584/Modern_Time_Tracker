package com.example.timetrackpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsageAppAdapter extends RecyclerView.Adapter<UsageAppAdapter.AppViewHolder> {

    List<UsageAppModel> appList;

    public UsageAppAdapter(List<UsageAppModel> appList) {
        this.appList = appList;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app_usage, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        UsageAppModel model = appList.get(position);

        holder.tvAppName.setText(model.getAppName());

        long mins = model.getTimeInForeground() / (1000 * 60);
        holder.tvAppTime.setText("Time Used: " + mins + " min");
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {

        TextView tvAppName, tvAppTime;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppTime = itemView.findViewById(R.id.tvAppTime);
        }
    }
}
