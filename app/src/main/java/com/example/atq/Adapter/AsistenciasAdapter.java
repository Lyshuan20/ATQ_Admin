package com.example.atq.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Model.Asistencias;
import com.example.atq.R;

import java.util.List;

public class AsistenciasAdapter extends RecyclerView.Adapter<AsistenciasAdapter.AsistenciaViewHolder> {

    private List<Asistencias> asistenciasList;

    public AsistenciasAdapter(List<Asistencias> asistenciasList) {
        this.asistenciasList = asistenciasList;
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_asistencias, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencias asistencia = asistenciasList.get(position);

        // Configurar los TextView con los datos de la asistencia
        holder.txtMostrarNomAsis.setText(asistencia.getNombreEmpleado());
        holder.txtMostarFechaAsist.setText(asistencia.getFechaAsistencia());
        holder.txtMostrarHE.setText("E: " + asistencia.getHoraEntrada());
        holder.txtMostrarHS.setText("S: " + asistencia.getHoraSalida());
    }

    @Override
    public int getItemCount() {
        return asistenciasList.size();
    }

    public static class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        TextView txtMostrarNomAsis, txtMostarFechaAsist, txtMostrarHE, txtMostrarHS;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMostrarNomAsis = itemView.findViewById(R.id.txtMostrarNomAsis);
            txtMostarFechaAsist = itemView.findViewById(R.id.txtMostarFechaAsist);
            txtMostrarHE = itemView.findViewById(R.id.txtMostrarHE);
            txtMostrarHS = itemView.findViewById(R.id.txtMostrarHS);
        }
    }
}

