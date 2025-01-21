package com.example.salud_total_v2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citas;

    public CitasAdapter(List<Cita> citas) {
        this.citas = citas;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citas.get(position);
        Log.d("CitasAdapter", "Enlazando cita en la posición " + position + ": " + cita.getFechaCita());
        holder.tvFecha.setText(cita.getFechaCita());
        holder.tvHora.setText(cita.getHoraCita());
        holder.tvUbicacion.setText(cita.getUbicacion());
        holder.tvNota.setText(cita.getNota());
    }


    @Override
    public int getItemCount() {
        return citas.size();
    }

    public void updateData(List<Cita> citas) {
        Log.d("CitasAdapter", "Actualizando datos del adapter con " + citas.size() + " citas");
        this.citas = citas;
        notifyDataSetChanged();
    }



    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHora, tvUbicacion, tvNota;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvNota = itemView.findViewById(R.id.tvNota);
        }
    }

}
