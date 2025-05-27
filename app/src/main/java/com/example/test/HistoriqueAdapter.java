package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Historique;

import java.util.List;

public class HistoriqueAdapter extends RecyclerView.Adapter<HistoriqueAdapter.HistoriqueViewHolder> {

    private List<Historique> historiqueList;

    public HistoriqueAdapter(List<Historique> historiqueList) {
        this.historiqueList = historiqueList;
    }

    @NonNull
    @Override
    public HistoriqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historique, parent, false);
        return new HistoriqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriqueViewHolder holder, int position) {
        Historique h = historiqueList.get(position);
        holder.txtAction.setText("• " + h.getAction());
        holder.txtDate.setText(h.getDate());
    }

    @Override
    public int getItemCount() {
        return historiqueList.size();
    }

    static class HistoriqueViewHolder extends RecyclerView.ViewHolder {
        TextView txtAction, txtDate;

        public HistoriqueViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAction = itemView.findViewById(R.id.txtAction);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
