package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.models.Reclamation;

import java.util.List;

public class ReclamationAdapter extends RecyclerView.Adapter<ReclamationAdapter.ReclamationViewHolder> {

    private List<Reclamation> reclamationList;

    public ReclamationAdapter(List<Reclamation> reclamationList) {
        this.reclamationList = reclamationList;
    }

    @NonNull
    @Override
    public ReclamationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reclamation, parent, false);
        return new ReclamationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReclamationViewHolder holder, int position) {
        Reclamation r = reclamationList.get(position);
        holder.tvObjet.setText("reclamations : " + r.getObjet());
        holder.tvMessage.setText("Message : " + r.getMessage());
        holder.tvDate.setText("Date : " + r.getDate());
    }

    @Override
    public int getItemCount() {
        return reclamationList.size();
    }

    static class ReclamationViewHolder extends RecyclerView.ViewHolder {
        TextView tvObjet, tvMessage, tvDate;

        public ReclamationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObjet = itemView.findViewById(R.id.tvObjet);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
