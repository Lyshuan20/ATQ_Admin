package com.example.atq.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterLiquidaciones;
import com.example.atq.Model.Liquidaciones;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentBitacora extends Fragment {

    //------------------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //------------------
    RecyclerView recyclerView;
    AdapterLiquidaciones adapterLiquidaciones;
    //------------------
    TextView FechaHoy;
    //------------------
    Button btnBuscarLiquidacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bitacora, container, false);

        FechaHoy = root.findViewById(R.id.txtFechaBitacora);
        btnBuscarLiquidacion = root.findViewById(R.id.btnBuscarLiquidacion);
        //PONER FECHA ACTUAL
        FechaHoy.setText(getDate());
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- MOSTRAR DATOS EN RECYCLERVIEW ------------------
        recyclerView = root.findViewById(R.id.listLiquidaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Liquidaciones> options =
                new FirebaseRecyclerOptions.Builder<Liquidaciones>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Liquidaciones").orderByChild("fechaLiquidacion").limitToLast(7), Liquidaciones.class)
                        .build();

        adapterLiquidaciones = new AdapterLiquidaciones(options);
        recyclerView.setAdapter(adapterLiquidaciones);
        //------------- BUSCAR FECHAS DE LIQUIDACIONES ------------------
        btnBuscarLiquidacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final int año = calendar.get(Calendar.YEAR);
                final int mes = calendar.get(Calendar.MONTH);
                final int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                        mes = mes + 1;
                        String mesFinal = String.valueOf(mes);
                        String diaFinal = String.valueOf(dia);
                        if (mes < 10) {
                            mesFinal = "0" + mes;
                        }
                        if (dia < 10) {
                            diaFinal = "0" + dia;
                        }
                        String date = año + "-" + mesFinal + "-" + diaFinal;
                        FechaHoy.setText(date);
                        ///----------------------------------------------------------------
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("¿Estas seguro?");
                        builder.setMessage("Se buscara la lista de asistencias de esta fecha " + FechaHoy.getText().toString());
                        //------------------------------------------------------------
                        builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String FechaABuscar = FechaHoy.getText().toString();
                                FirebaseRecyclerOptions<Liquidaciones> options =
                                        new FirebaseRecyclerOptions.Builder<Liquidaciones>()
                                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Liquidaciones").orderByChild("fechaLiquidacion").startAt(FechaABuscar).endAt(FechaABuscar), Liquidaciones.class)
                                                .build();

                                adapterLiquidaciones = new AdapterLiquidaciones(options);
                                adapterLiquidaciones.startListening();
                                recyclerView.setAdapter(adapterLiquidaciones);
                            }
                        });
                        //------------------------------------------------------------
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FechaHoy.setText(getDate());
                                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                }, año, mes, dia);
                dialog.show();
            }
        });
        return root;
    }

    //------------- MOSTRAR RECYCLERVIEW  ------------------
    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        adapterLiquidaciones.notifyDataSetChanged();
        adapterLiquidaciones.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterLiquidaciones.stopListening();
    }

    //------------- MOSTRAR FECHA DE HOY ------------------
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}