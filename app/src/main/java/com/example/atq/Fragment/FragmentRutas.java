package com.example.atq.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterRutas;
import com.example.atq.Model.Rutas;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

public class FragmentRutas extends Fragment {

    FloatingActionButton fab;
    String Fecha;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    AdapterRutas adapterRutas;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_rutas, container, false);

        fab = root.findViewById(R.id.fabRutas);
        searchView = root.findViewById(R.id.txtbuscadorRutas);
        //------------- BUSCADOR DE RUTAS -----------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                txtBuscar(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                txtBuscar(s);
                return false;
            }
        });
        //------------- FECHA ACTUAL ------------------
        Calendar calendar = Calendar.getInstance();
        int dianow = calendar.get(Calendar.DATE);
        int mesnow = calendar.get(Calendar.MONTH);
        int yearnow = calendar.get(Calendar.YEAR);
        mesnow++;
        int finalMesnow = mesnow;
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- MOSTRAR DATOS EN RECYCLERVIEW ------------------
        recyclerView = root.findViewById(R.id.listRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Rutas> options =
                new FirebaseRecyclerOptions.Builder<Rutas>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Rutas"), Rutas.class)
                        .build();

        adapterRutas = new AdapterRutas(options);
        recyclerView.setAdapter(adapterRutas);
        //------------- FECHA ACTUAL ------------------
        //------------- BOTON DE AGREGAR ------------------
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_rutas);

                int gravity = Gravity.CENTER;
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowsAtributos = window.getAttributes();
                windowsAtributos.gravity = gravity;
                window.setAttributes(windowsAtributos);

                //Botones del dialog
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarRuta);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarRuta);

                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //------------- BOTON GUARDAR RUTA DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text del nombre de la ruta
                        EditText nomRuta = dialog.findViewById(R.id.AddNombreRuta);
                        String nombreRuta = nomRuta.getText().toString();
                        //Fecha de ingreso de la ruta

                        Fecha = (yearnow + "-" + finalMesnow + "-" + dianow);
                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (nombreRuta.isEmpty()) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            //Declarar los valores de la ruta
                            Rutas rutas = new Rutas();
                            rutas.setIDRuta(UUID.randomUUID().toString());
                            rutas.setNombreRuta(nombreRuta);
                            rutas.setFechaIngreso(Fecha);
                            //Guardamos datos a firebase
                            databaseReference.child("Rutas").child(rutas.getIDRuta()).setValue(rutas);
                            //ACTUALIZAR DATOS PARA ESCRITORIO
                            String ProcesoActualizar = "1";
                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                            //--------------------
                            Toast.makeText(getActivity(), "Agregado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
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
        adapterRutas.notifyDataSetChanged();
        adapterRutas.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRutas.stopListening();
    }

    //------------- BUSCADOR  ------------------
    private void txtBuscar(String str) {
        FirebaseRecyclerOptions<Rutas> options =
                new FirebaseRecyclerOptions.Builder<Rutas>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Rutas").orderByChild("nombreRuta").startAt(str).endAt(str + "~"), Rutas.class)
                        .build();

        adapterRutas = new AdapterRutas(options);
        adapterRutas.startListening();
        recyclerView.setAdapter(adapterRutas);
    }

}