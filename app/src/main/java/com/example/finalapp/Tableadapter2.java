package com.example.finalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Tableadapter2 extends RecyclerView.Adapter<Tableadapter2.ViewHolder> {
    private List<Tableitem2> items;
    private TableAdapter.OnItemClickListener itemClickListener;

    public Tableadapter2(List<Tableitem2> items) {
        this.items = items;

    }

    @NonNull
    @Override
    public Tableadapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row2, parent, false);
        return new Tableadapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView keyPartitionTextView;
        private TextView ColorTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            keyPartitionTextView = itemView.findViewById(R.id.keyParition);
            ColorTextView = itemView.findViewById(R.id.COLOR);
        }

        public void bind(Tableitem2 items) {
            keyPartitionTextView.setText(items.getKeyPartition());
            ColorTextView.setText(items.getColor());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TableItem item);
    }
}
