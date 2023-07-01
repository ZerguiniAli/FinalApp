package com.example.finalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.TableItem;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private List<TableItem> items;
    private OnItemClickListener itemClickListener;

    public TableAdapter(List<TableItem> items, OnItemClickListener itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableItem item = items.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView coordinatesTextView;
        private TextView keyFieldTextView;
        private TextView fieldnametextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldnametextview = itemView.findViewById(R.id.keynameTextVieW);
        }

        public void bind(TableItem item) {
            fieldnametextview.setText(item.getFieldname());

        }
    }

    public interface OnItemClickListener {
        void onItemClick(TableItem item);
    }
}
