package com.example.atq.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AsistenciasAdapter;
import com.example.atq.Model.Asistencias;
import com.example.atq.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentAsistencias extends Fragment {
    private String busqueda = "";
    private RecyclerView recyclerView;
    private List<Asistencias> asistenciasList;
    private AsistenciasAdapter asistenciasAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_asistencias, container, false);

        recyclerView = root.findViewById(R.id.listasistencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        asistenciasList = new ArrayList<>();
        asistenciasAdapter = new AsistenciasAdapter(asistenciasList);
        recyclerView.setAdapter(asistenciasAdapter);
        //------
        cargarDatosFirebase();

        //------
        SearchView txtbuscadorAsistencias = root.findViewById(R.id.txtbuscadorAsistencias);
        txtbuscadorAsistencias.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Actualizar la cadena de búsqueda y recargar los datos
                busqueda = newText;
                cargarDatosFirebase();
                return true;
            }
        });
        //--------
        return root;
    }

    //----------- CARGAR DATOS FIREBASE -----------------
    private void cargarDatosFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Asistencias");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                asistenciasList.clear();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String fechaActual = dateFormat.format(new Date());

                for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot empleadoSnapshot : fechaSnapshot.getChildren()) {
                        Asistencias asistencia = empleadoSnapshot.getValue(Asistencias.class);

                        // Si hay una cadena de búsqueda, mostrar todas las asistencias que coincidan
                        if (!busqueda.isEmpty() && (asistencia != null &&
                                (asistencia.getNombreEmpleado().toLowerCase().contains(busqueda.toLowerCase()) ||
                                        asistencia.getFechaAsistencia().contains(busqueda)))) {
                            asistenciasList.add(asistencia);
                        }
                        // Si no hay cadena de búsqueda, mostrar solo las asistencias del día actual
                        if (asistencia != null && asistencia.getFechaAsistencia() != null &&
                                asistencia.getFechaAsistencia().equals(fechaActual)) {
                            asistenciasList.add(asistencia);
                        }
                    }
                }
                asistenciasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }

}