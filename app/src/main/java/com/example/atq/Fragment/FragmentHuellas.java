package com.example.atq.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterHuellas;
import com.example.atq.Model.Choferes;
import com.example.atq.Model.Huellas;
import com.example.atq.Model.Usuarios;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class FragmentHuellas extends Fragment {
    final int Encendido = 1; //Ultimo ID insertado y estado encendido huella
    //------------- BOTONES ------------------
    FloatingActionButton fabHuellas;
    //------------- FIREBASE ------------------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //------------- AGREGAR HUELLAS ------------------
    String UltimoIdInsertado;
    String ChoferSeleccionado, UsuarioSeleccionado; //si selecciono un usuario o chofer
    String NombreChofer, ApellidosChofer; //Datos del chofer
    String NombreUsuario, NombreCompleto, ContraseñaUsuario; //Datos del usuario
    String ContraChoferFinal, NombreChoferFinal, ApeChoferFinal, IDStringChofer; //Datos finales Choferes
    String ContraUsuarioFinal, NombreUsuarioFinal, NomComletoFinal, IDStringUsuario; //Datos finales user
    //------------- RECYCLERVIEW Y ADAPTER ------------------
    RecyclerView recyclerView, RV_Asist;
    AdapterHuellas adapterHuellas;
    TextView txtTituloHuellas;
    SharedPreferences settings;
    private ProgressDialog progressDialog; //Progress dialog
    //------------- NOTIFICACION DE ASISTENCIAS ------------------
    private TextView notificationsTime;
    private Button btnHoraEntrada;
    private int alarmID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_huellas, container, false);

        fabHuellas = root.findViewById(R.id.fabHuellas);
        txtTituloHuellas = root.findViewById(R.id.txtTituloHuellas);
        progressDialog = new ProgressDialog(getActivity());
        recyclerView = root.findViewById(R.id.listHuellas);

        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- PROGRESS DIALOG ------------------
        databaseReference.child("EstadoHuella").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String EstadoHuellaProgress = (snapshot.getValue().toString());
                    if (EstadoHuellaProgress.equals("1")) {
                        progressDialog.setMessage("Enrolando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    } else if (EstadoHuellaProgress.equals("2")) {
                        progressDialog.setMessage("Eliminando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    } else if (EstadoHuellaProgress.equals("0")) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (HUELLAS) ------------------
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Huellas> options =
                new FirebaseRecyclerOptions.Builder<Huellas>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Huellas"), Huellas.class)
                        .build();

        adapterHuellas = new AdapterHuellas(options);
        recyclerView.setAdapter(adapterHuellas);
        //------------- AGREGAR DE HUELLAS -----------------
        fabHuellas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_huella_1);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarHuella);
                Button btnSiguiente1 = dialog.findViewById(R.id.btnSiguienteHuella1);

                //SPINNER
                Spinner TipoE = dialog.findViewById(R.id.AddTipoEmpleadoHuella);

                String[] TipoDeEmpleados = {"Operador", "Administrativo"};
                ArrayAdapter<String> adapterTipoE = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, TipoDeEmpleados);
                TipoE.setAdapter(adapterTipoE);

                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //------------- BOTON GUARDAR HUELLA DIALOG (PARTE 1) ------------------
                btnSiguiente1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TIPO DE EMPLEADO
                        String TipoSeleccionado = TipoE.getSelectedItem().toString();
                        String TipoDeEmpleadoSeleccionado;
                        if (TipoSeleccionado.equals("Operador")) {
                            TipoDeEmpleadoSeleccionado = "Operador";
                        } else {
                            TipoDeEmpleadoSeleccionado = "Administrativo";
                        }
                        dialog.dismiss();
                        //------------- BOTON GUARDAR HUELLA DIALOG (PARTE 2) ------------------
                        final Dialog dialog2 = new Dialog(getActivity());
                        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog2.setContentView(R.layout.agregar_huella_2);

                        int gravity = Gravity.CENTER;
                        Window window = dialog2.getWindow();
                        if (window == null) {
                            return;
                        }
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams windowsAtributos = window.getAttributes();
                        windowsAtributos.gravity = gravity;
                        window.setAttributes(windowsAtributos);

                        //Botones del dialog
                        Button btnCerrar = dialog2.findViewById(R.id.btnCancelarHuella);
                        Button btnSiguiente2 = dialog2.findViewById(R.id.btnSiguienteHuella2);
                        //SPINNER
                        Spinner spinnerEmpleado = dialog2.findViewById(R.id.AddEmpleadoHuella);

                        final List<Choferes> ListChoferes = new ArrayList<>();
                        final List<Usuarios> ListUsuarios = new ArrayList<>();
                        final List<Huellas> ListHuellas = new ArrayList<>();

                        //LISTA DE LAS HUELLAS EXISTENTE
                        databaseReference.child("Huellas").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot item : snapshot.getChildren()) {
                                        String nomUser = item.child("nomUsuario").getValue().toString();
                                        ListHuellas.add(new Huellas(nomUser));
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "ERROR. NO HAY HUELLAS EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //SI EL EMPLEADO ES OPERADOR
                        if (TipoDeEmpleadoSeleccionado.equals("Operador")) {
                            databaseReference.child("Choferes").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot item : snapshot.getChildren()) {
                                            String nomChofer = item.child("nombreChofer").getValue().toString();
                                            String apeChofer = item.child("apellidosChofer").getValue().toString();
                                            ListChoferes.add(new Choferes(nomChofer, apeChofer));
                                        }
                                        //VERIFICAR SI EL USUARIO YA EXISTE
                                        ArrayList<String> repetidos = new ArrayList<>();
                                        for (int i = 0; i < ListChoferes.size(); i++) {
                                            for (int j = i + 1; j < ListHuellas.size() && !repetidos.contains(ListChoferes.get(i)); j++) {
                                                if (ListChoferes.get(i).getNombreChofer().equals(ListHuellas.get(j).getNomUsuario())) {
                                                    ListChoferes.remove(ListChoferes.get(i));
                                                }
                                            }
                                        }
                                        ArrayAdapter<Choferes> AdapterChoferes = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListChoferes);
                                        spinnerEmpleado.setAdapter(AdapterChoferes);
                                        //PONER EL VALOR DEL CHOFER SEGUN EL CHOFER SELECCIONADO
                                        spinnerEmpleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                NombreChofer = ListChoferes.get(i).getNombreChofer();
                                                ApellidosChofer = ListChoferes.get(i).getApellidosChofer();
                                                ChoferSeleccionado = ListChoferes.get(i).getNombreChofer();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "ERROR. NO HAY CHOFERES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        } else {
                            databaseReference.child("Usuarios").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot item : snapshot.getChildren()) {
                                            String nomUsuario = item.child("nombreUsuario").getValue().toString();
                                            String nomCompleto = item.child("nombreCompleto").getValue().toString();
                                            String Contraseña = item.child("contra").getValue().toString();
                                            ListUsuarios.add(new Usuarios(nomUsuario, nomCompleto, Contraseña));
                                        }
                                        //VERIFICAR SI EL USUARIO YA EXISTE
                                        ArrayList<String> repetidos = new ArrayList<>();
                                        for (int i = 0; i < ListHuellas.size(); i++) {
                                            for (int j = 0; j < ListUsuarios.size() && !repetidos.contains(ListHuellas.get(i)); j++) {
                                                if (ListUsuarios.get(j).getNombreUsuario().equals(ListHuellas.get(i).getNomUsuario())) {
                                                    ListUsuarios.remove(ListUsuarios.get(j));
                                                }
                                            }
                                        }
                                        ArrayAdapter<Usuarios> AdapterUsuarios = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListUsuarios);
                                        spinnerEmpleado.setAdapter(AdapterUsuarios);
                                        //PONER EL VALOR DE LA UNIDAD SEGUN LA UNIDAD SELECCIONADA
                                        spinnerEmpleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                String nomUsuario = ListUsuarios.get(i).getNombreUsuario();
                                                String nomComple = ListUsuarios.get(i).getNombreCompleto();
                                                String Contra = ListUsuarios.get(i).getContra();
                                                NombreUsuario = nomUsuario;
                                                NombreCompleto = nomComple;
                                                ContraseñaUsuario = Contra;
                                                UsuarioSeleccionado = ListUsuarios.get(i).getNombreUsuario();

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "ERROR. NO HAY USUARIOS EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                        //------------- CERRAR DIALOG ------------------
                        btnCerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog2.dismiss();
                            }
                        });
                        //------------- BOTON GUARDAR HUELLA DIALOG (PARTE 2) ------------------
                        btnSiguiente2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //------------- BOTON GUARDAR HUELLA DIALOG (PARTE 3) ------------------
                                //-------------------------- OPERADOR -------------------------------
                                if (TipoDeEmpleadoSeleccionado.equals("Operador")) {
                                    dialog2.dismiss();
                                    //------------- DIALOG ------------------
                                    final Dialog dialog3O = new Dialog(getActivity());
                                    dialog3O.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog3O.setContentView(R.layout.agregar_huella_3_operador);

                                    int gravity = Gravity.CENTER;
                                    Window window = dialog3O.getWindow();
                                    if (window == null) {
                                        return;
                                    }
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    WindowManager.LayoutParams windowsAtributos = window.getAttributes();
                                    windowsAtributos.gravity = gravity;
                                    window.setAttributes(windowsAtributos);

                                    //Botones del dialog
                                    Button btnCerrar = dialog3O.findViewById(R.id.btnCancelarHuella);
                                    Button btnSiguiente3 = dialog3O.findViewById(R.id.btnSiguiente3O);

                                    EditText nomChofer = dialog3O.findViewById(R.id.AddNomChoferHuella);
                                    EditText apeChofer = dialog3O.findViewById(R.id.AddApeChoferHuella);
                                    nomChofer.setText(NombreChofer);
                                    apeChofer.setText(ApellidosChofer);

                                    //ID Y ESTADO DE LA HUELLA
                                    TextView txtId = dialog3O.findViewById(R.id.IDHuellaFinal);

                                    //-------------------------------------------
                                    //BUSCAR EL VALOR DEL ULTIMO ID
                                    databaseReference.child("UltimoIdInsertado").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                UltimoIdInsertado = snapshot.getValue().toString();
                                                txtId.setText(UltimoIdInsertado);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    //------------- CERRAR DIALOG ------------------
                                    btnCerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog3O.dismiss();
                                        }
                                    });
                                    //------------- BOTON SIGUIENTE OPERADOR DIALOG ------------------
                                    btnSiguiente3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //ID DE HUELLA
                                            UltimoIdInsertado = txtId.getText().toString();
                                            int IDINT = Integer.parseInt(UltimoIdInsertado) + 1;
                                            IDStringChofer = String.valueOf(IDINT);
                                            FirebaseDatabase.getInstance().getReference().child("UltimoIdInsertado").setValue(IDINT);
                                            //-------------------------
                                            EditText contraChofer = dialog3O.findViewById(R.id.AddContraChoferHuella);
                                            ContraChoferFinal = contraChofer.getText().toString();
                                            NombreChoferFinal = nomChofer.getText().toString();
                                            ApeChoferFinal = apeChofer.getText().toString();


                                            if (ContraChoferFinal.isEmpty()) {
                                                Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                                            } else {

                                                FirebaseDatabase.getInstance().getReference().child("EstadoHuella").setValue(Encendido);
                                                //-------------------------------------------
                                                //BUSCAR EL VALOR DEL ESTADO HUELLA
                                                Huellas HC = new Huellas();
                                                HC.setIDHuella(IDStringChofer);
                                                HC.setNomUsuario(NombreChoferFinal);
                                                HC.setContra(ContraChoferFinal);
                                                HC.setTipoEmpleado(TipoDeEmpleadoSeleccionado);
                                                HC.setNomCompleto(NombreChoferFinal + " " + ApeChoferFinal);

                                                String NuevoID = "H" + HC.getIDHuella();
                                                //Guardamos datos a firebase
                                                databaseReference.child("Huellas").child(NuevoID).setValue(HC);
                                                Toast.makeText(getActivity(), "Agregado", Toast.LENGTH_SHORT).show();
                                                dialog3O.dismiss();
                                            }

                                        }
                                    });

                                    dialog3O.show();
                                }
                                //------------- BOTON GUARDAR HUELLA DIALOG (PARTE 3) ------------------
                                //-------------------- ADMINISTRATIVO -------------------------
                                else {
                                    dialog2.dismiss();
                                    //------------- DIALOG ------------------
                                    final Dialog dialog3A = new Dialog(getActivity());
                                    dialog3A.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog3A.setContentView(R.layout.agregar_huella_3_administrativo);

                                    int gravity = Gravity.CENTER;
                                    Window window = dialog3A.getWindow();
                                    if (window == null) {
                                        return;
                                    }
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    WindowManager.LayoutParams windowsAtributos = window.getAttributes();
                                    windowsAtributos.gravity = gravity;
                                    window.setAttributes(windowsAtributos);

                                    //Botones del dialog
                                    Button btnCerrar = dialog3A.findViewById(R.id.btnCancelarHuella);
                                    Button btnSiguiente3 = dialog3A.findViewById(R.id.btnSiguiente3A);

                                    EditText nomUsuario = dialog3A.findViewById(R.id.AddNomUserHuella);
                                    EditText nomComUsuario = dialog3A.findViewById(R.id.AddNomCompleHuella);
                                    EditText ContraUsuario = dialog3A.findViewById(R.id.AddContraUserHuella);
                                    nomUsuario.setText(NombreUsuario);
                                    nomComUsuario.setText(NombreCompleto);
                                    ContraUsuario.setText(ContraseñaUsuario);

                                    //ID Y ESTADO DE LA HUELLA
                                    TextView txtId = dialog3A.findViewById(R.id.IDHuellaFinal);
                                    TextView txtEstadoHuella = dialog3A.findViewById(R.id.EstadoHuellaFinal);

                                    //-------------------------------------------
                                    //BUSCAR EL VALOR DEL ULTIMO ID
                                    databaseReference.child("UltimoIdInsertado").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                UltimoIdInsertado = snapshot.getValue().toString();
                                                txtId.setText(UltimoIdInsertado);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    //------------- CERRAR DIALOG ------------------
                                    btnCerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog3A.dismiss();
                                        }
                                    });
                                    //------------- BOTON SIGUIENTE ADMINISTRATIVO DIALOG ------------------
                                    btnSiguiente3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //ID DE HUELLA
                                            UltimoIdInsertado = txtId.getText().toString();
                                            int IDINT = Integer.parseInt(UltimoIdInsertado) + 1;
                                            IDStringUsuario = String.valueOf(IDINT);
                                            FirebaseDatabase.getInstance().getReference().child("UltimoIdInsertado").setValue(IDINT);
                                            //-------------------------
                                            ContraUsuarioFinal = ContraUsuario.getText().toString();
                                            NombreUsuarioFinal = nomUsuario.getText().toString();
                                            NomComletoFinal = nomComUsuario.getText().toString();

                                            FirebaseDatabase.getInstance().getReference().child("EstadoHuella").setValue(Encendido);
                                            //-------------------------------------------
                                            //BUSCAR EL VALOR DEL ESTADO HUELLA
                                            Huellas HC = new Huellas();
                                            HC.setIDHuella(IDStringUsuario);
                                            HC.setNomUsuario(NombreUsuarioFinal);
                                            HC.setContra(ContraUsuarioFinal);
                                            HC.setTipoEmpleado(TipoDeEmpleadoSeleccionado);
                                            HC.setNomCompleto(NomComletoFinal);

                                            String NuevoID = "H" + HC.getIDHuella();
                                            //Guardamos datos a firebase
                                            databaseReference.child("Huellas").child(NuevoID).setValue(HC);
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                                            //--------------------
                                            Toast.makeText(getActivity(), "Agregado", Toast.LENGTH_SHORT).show();
                                            dialog3A.dismiss();

                                        }
                                    });

                                    dialog3A.show();
                                }
                            }
                        });
                        dialog2.show();
                    }
                });
                dialog.show();
            }
        });
        return root;
    }

    //------------- MOSTRAR FECHA DE HOY ------------------
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //------------- MOSTRAR LAS HUELLAS ------------------
    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        adapterHuellas.notifyDataSetChanged();

        adapterHuellas.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        adapterHuellas.stopListening();
    }
}