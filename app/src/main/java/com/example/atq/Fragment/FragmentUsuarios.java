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
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterUsuarios;
import com.example.atq.Model.Usuarios;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentUsuarios extends Fragment {

    FloatingActionButton fab;
    //------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //-----
    RecyclerView recyclerView;
    SearchView searchView;
    AdapterUsuarios adapterUsuarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_usuarios, container, false);

        fab = root.findViewById(R.id.fabUsuarios);
        searchView = root.findViewById(R.id.txtbuscador);
        //------------- BUSCADOR DE USUARIOS -----------------
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
        //------------- MOSTRAR DATOS EN RECYCLERVIEW ------------------
        recyclerView = root.findViewById(R.id.listUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Usuarios> options =
                new FirebaseRecyclerOptions.Builder<Usuarios>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Usuarios"), Usuarios.class)
                        .build();

        adapterUsuarios = new AdapterUsuarios(options);
        recyclerView.setAdapter(adapterUsuarios);
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- BOTON DE AGREGAR ------------------
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_usuarios);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarUsuario);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarUsuario);

                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //------------- BOTON GUARDAR USUARIO DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text de los datos usuario
                        EditText nomUsuario = dialog.findViewById(R.id.AddNombreUsuario);
                        EditText nomCompleto = dialog.findViewById(R.id.AddNombreCompleto);
                        EditText correoUsuario = dialog.findViewById(R.id.AddCorreoUsuario);
                        EditText contraUsuario = dialog.findViewById(R.id.AddContraseñaUsuario);
                        RadioButton UsuarioAdmin = dialog.findViewById(R.id.RBAdmin);
                        RadioButton UsuarioNormal = dialog.findViewById(R.id.RBNormal);

                        //String de los datos de usuario
                        String nombreUsuario = nomUsuario.getText().toString();
                        String nombreComple = nomCompleto.getText().toString();
                        String email = correoUsuario.getText().toString();
                        String contraseñaUsuario = contraUsuario.getText().toString();
                        String UserAdmin = UsuarioAdmin.getText().toString();
                        String UserNormal = UsuarioNormal.getText().toString();

                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (nombreUsuario.isEmpty() || email.isEmpty() || nombreComple.isEmpty()
                                || contraseñaUsuario.isEmpty()) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else if (UsuarioAdmin.isChecked() || UsuarioNormal.isChecked()) {
                            //Declarar los valores del usuario
                            Usuarios usuarios = new Usuarios();
                            usuarios.setNombreUsuario(nombreUsuario);
                            usuarios.setNombreCompleto(nombreComple);
                            usuarios.setEmail(email);
                            usuarios.setContra(contraseñaUsuario);

                            //Le ponemos el tipo de usuario
                            if (UsuarioAdmin.isChecked()) {
                                usuarios.setTipoUsuario(UserAdmin);
                            } else {
                                usuarios.setTipoUsuario(UserNormal);
                            }
                            //Guardamos datos a firebase
                            databaseReference.child("Usuarios").child(usuarios.getNombreUsuario()).setValue(usuarios);
                            //ACTUALIZAR DATOS PARA ESCRITORIO
                            String ProcesoActualizar = "1";
                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                            Toast.makeText(getActivity(), "Agregado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        adapterUsuarios.notifyDataSetChanged();
        adapterUsuarios.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterUsuarios.stopListening();
    }

    //------------- BUSCADOR  ------------------
    private void txtBuscar(String str) {
        FirebaseRecyclerOptions<Usuarios> options =
                new FirebaseRecyclerOptions.Builder<Usuarios>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("nombreUsuario").startAt(str).endAt(str + "~"), Usuarios.class)
                        .build();
        adapterUsuarios = new AdapterUsuarios(options);
        adapterUsuarios.startListening();
        recyclerView.setAdapter(adapterUsuarios);
    }
}