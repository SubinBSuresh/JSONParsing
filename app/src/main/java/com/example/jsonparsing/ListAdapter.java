package com.example.jsonparsing;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    ArrayList<Device> deviceArrayList;
    Context context;

    private int selectedPosition = -1; // Track the selected item's position
    private boolean isFirstClick = true; // In the adapter class

    public ListAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.context = context;
        this.deviceArrayList = deviceArrayList;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        holder.deviceID.setText(String.valueOf(deviceArrayList.get(position).getId()));
        holder.deviceName.setText(deviceArrayList.get(position).getName());

        // Set background color based on selection
        if (position == selectedPosition) {
            if (isFirstClick) {
                isFirstClick = false;
                holder.itemView.setBackgroundColor(Color.BLUE); // Change color as needed

            } else {
                System.out.println("Clicked"); // Or use a Toast for a visual message
                Log.e("logg", "double click");
            }
        } else {
            isFirstClick = true;
            holder.itemView.setBackgroundColor(Color.WHITE); // Default color
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = getSelectedPosition();
            if (previousSelectedPosition != -1) {
                notifyItemChanged(previousSelectedPosition); // Revert previous item's color
            }
            setSelectedPosition(position);
            notifyItemChanged(position); // Update current item's color
        });
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceID, deviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceID = itemView.findViewById(R.id.tv_id);
            deviceName = itemView.findViewById(R.id.tv_device);

        }
    }
}
