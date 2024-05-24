package com.example.atq.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.atq.Adapter.AdapterImagenUnidades;
import com.example.atq.Dashboard_Personal;
import com.example.atq.Model.Asistencias;
import com.example.atq.Model.Choferes;
import com.example.atq.Model.GastosUnidades;
import com.example.atq.Model.Liquidaciones;
import com.example.atq.Model.Rutas;
import com.example.atq.Model.Unidades;
import com.example.atq.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FragmentInicio extends Fragment {

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Button btnLiquidacion, btnRoles;
    ImageView btnCambiarPagina;
    //Adapter de las imagenes de las unidades
    GridView gridViewUnidades;
    ArrayList<Unidades> unidadesList;
    AdapterImagenUnidades adapterImagenUnidades;
    //Datos de las unidades
    String NumUnidad, OperadorSeleccionado, RutaSeleccionada, A, B, C;
    //ROLES
    ArrayList<String> Nom_Unidades = new ArrayList<>();
    ArrayList<String> Nom_Choferes = new ArrayList<>();
    ArrayList<String> Nom_Rutas = new ArrayList<>();
    //GRAFICAS
    PieChart GraficUnidadGasto, GananciasRutas;
    BarChart GananciaUnidad;
    //------------------
    DatabaseReference databaseReferenceGasto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inicio, container, false);

        btnLiquidacion = root.findViewById(R.id.btnLiquidacion);
        btnRoles = root.findViewById(R.id.btnRoles);
        btnCambiarPagina = root.findViewById(R.id.btnCambiarPagina);

        GraficUnidadGasto = root.findViewById(R.id.GraficUnidadGasto);
        GananciaUnidad = root.findViewById(R.id.GraficGananciaUnidad);
        GananciasRutas = root.findViewById(R.id.GraficGananciaRuta);
        //-----
        // Cargar el fragmento
        verificarAsistencias();
        // Inicializar Firebase
        databaseReferenceGasto = FirebaseDatabase.getInstance().getReference().child("Gastos");
        // Obtener datos de Firebase
        obtenerDatosGastos();
        GraficoGananciasUnidad();
        calcularGananciaPorRuta();

        //------------- BOTÓN PAGINA ------------------
        btnCambiarPagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard_Personal.class);
                startActivity(intent);
            }
        });
        //------------- BOTÓN LIQUIDACIONES ------------------
        btnLiquidacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BotonLiquidacion();
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_liquidacion_unidades);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarLiquidacion);
                Button btnSiguiente = dialog.findViewById(R.id.btnSiguienteLiquidacion);

                //LISTA DE UNIDADES A ELEGIR
                gridViewUnidades = dialog.findViewById(R.id.GV_Unidades);

                unidadesList = new ArrayList<>();
                adapterImagenUnidades = new AdapterImagenUnidades(unidadesList, getActivity());
                gridViewUnidades.setAdapter(adapterImagenUnidades);
                TextView UnidadSelect = dialog.findViewById(R.id.txtNumUnidadSelect);

                databaseReference.child("Unidades").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Unidades unidades = dataSnapshot.getValue(Unidades.class);
                            unidadesList.add(unidades);
                        }
                        adapterImagenUnidades.notifyDataSetChanged();
                        gridViewUnidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String NumeroUnidad = unidadesList.get(i).getNumUnidad();
                                UnidadSelect.setText(NumeroUnidad);
                                NumUnidad = unidadesList.get(i).getNumUnidad();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumUnidad = null;
                        dialog.dismiss();
                    }
                });
                //------------- BOTON SIGUIENTE 1 LIQUIDACION DIALOG ------------------
                btnSiguiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //VERIFICAR QUE SI ELIGIO UNA UNIDAD
                        if (NumUnidad == null) {
                            Toast.makeText(getActivity(), "ERROR. Eliga una unidad", Toast.LENGTH_SHORT).show();
                        } else {
                            //ABRIR EL SEGUNDO FORMULARIO
                            dialog.dismiss();
                            //------------- DIALOG ------------------
                            final Dialog dialog2 = new Dialog(getActivity());
                            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog2.setContentView(R.layout.agregar_liquidacion);

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
                            Button btnCerrarL = (Button) dialog2.findViewById(R.id.btnCancelarLiquidacion);
                            Button btnSiguienteL = (Button) dialog2.findViewById(R.id.btnSiguienteLiquidacion);

                            //Botones de las fechas
                            TextView btnFechaLiqui = dialog2.findViewById(R.id.btnFechaLiquidacion);
                            //fechas
                            TextView FechaLiquid = dialog2.findViewById(R.id.txtFechaLiquidacion);
                            FechaLiquid.setText(getDate());

                            //------------- FECHA LIQUIDACION ------------------
                            btnFechaLiqui.setOnClickListener(new View.OnClickListener() {
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
                                            FechaLiquid.setText(date);
                                        }
                                    }, año, mes, dia);
                                    dialog.show();
                                }
                            });

                            //------------- CERRAR DIALOG ------------------
                            btnCerrarL.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog2.dismiss();
                                }
                            });
                            //------------- BOTON DE SIGUIENTE 2 LIQUIDACIÓN ------------------
                            btnSiguienteL.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //EDIT TEXT
                                    EditText CantLiquid = dialog2.findViewById(R.id.AddCantLiquidacion);
                                    EditText NumVueltas = dialog2.findViewById(R.id.AddNumVueltas);
                                    EditText CantCombus = dialog2.findViewById(R.id.AddCantCombustible);
                                    EditText Boletera1 = dialog2.findViewById(R.id.AddBoletera1);
                                    EditText Boletera2 = dialog2.findViewById(R.id.AddBoletera2);
                                    EditText Boletera3 = dialog2.findViewById(R.id.AddBoletera3);

                                    //STRINGS DE LOS DATOS
                                    String CantidadLiquidacion = CantLiquid.getText().toString();
                                    String NumeroVueltas = NumVueltas.getText().toString();
                                    String CantidadCombustible = CantCombus.getText().toString();
                                    String NumBolet1 = Boletera1.getText().toString();
                                    String NumBolet2 = Boletera2.getText().toString();
                                    String NumBolet3 = Boletera3.getText().toString();
                                    String FechaLiquidación = FechaLiquid.getText().toString();

                                    if (CantidadLiquidacion.isEmpty() || NumeroVueltas.isEmpty()
                                            || CantidadCombustible.isEmpty() || NumBolet1.isEmpty()
                                            || NumBolet2.isEmpty() || NumBolet3.isEmpty()) {
                                        Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //ABRIR EL TERCER FORMULARIO
                                        dialog2.dismiss();
                                        //------------- DIALOG ------------------
                                        final Dialog dialog3 = new Dialog(getActivity());
                                        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog3.setContentView(R.layout.agregar_liquidacion_2);

                                        int gravity = Gravity.CENTER;
                                        Window window = dialog3.getWindow();
                                        if (window == null) {
                                            return;
                                        }
                                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        WindowManager.LayoutParams windowsAtributos = window.getAttributes();
                                        windowsAtributos.gravity = gravity;
                                        window.setAttributes(windowsAtributos);

                                        //Botones del dialog
                                        Button btnCerrarL2 = (Button) dialog3.findViewById(R.id.btnCancelarLiquidacion);
                                        Button btnSiguienteL2 = (Button) dialog3.findViewById(R.id.btnGuardarLiquidacion);

                                        Spinner SpinnerOperadores = dialog3.findViewById(R.id.AddOperadorL);
                                        Spinner SpinnerRutas = dialog3.findViewById(R.id.AddRutaL);

                                        final List<Choferes> ListChoferes = new ArrayList<>();
                                        final List<Rutas> ListRutas = new ArrayList<>();
                                        //METODO DROPDOWN OPERADORES
                                        databaseReference.child("Choferes").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot item : snapshot.getChildren()) {
                                                        String nomChofer = item.child("nombreChofer").getValue().toString();
                                                        String apeChofer = item.child("apellidosChofer").getValue().toString();
                                                        ListChoferes.add(new Choferes(nomChofer, apeChofer));
                                                    }
                                                    ArrayAdapter<Choferes> AdapterChoferes = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListChoferes);
                                                    SpinnerOperadores.setAdapter(AdapterChoferes);
                                                    //PONER EL VALOR DEL OPERADOR SEGUN EL OPERADOR SELECCIONADO
                                                    SpinnerOperadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                            String Nombre = ListChoferes.get(i).getNombreChofer();
                                                            String Apellido = ListChoferes.get(i).getApellidosChofer();
                                                            OperadorSeleccionado = Nombre + " " + Apellido;
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
                                        //METODO DROPDOWN RUTAS
                                        databaseReference.child("Rutas").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot item : snapshot.getChildren()) {
                                                        String nomRuta = item.child("nombreRuta").getValue().toString();
                                                        ListRutas.add(new Rutas(nomRuta));
                                                    }
                                                    ArrayAdapter<Rutas> AdapterRutas = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListRutas);
                                                    SpinnerRutas.setAdapter(AdapterRutas);
                                                    //PONER EL VALOR DE LA RUTA SEGUN LA RUTA SELECCIONADA
                                                    SpinnerRutas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                            RutaSeleccionada = ListRutas.get(i).getNombreRuta();
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getActivity(), "ERROR. NO HAY RUTASa EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        //------------- CERRAR ------------------
                                        btnCerrarL2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog3.dismiss();
                                            }
                                        });
                                        //------------- GUARDAR LIQUIDACIÓN ------------------
                                        btnSiguienteL2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String OpeSelect = OperadorSeleccionado.toString();
                                                String RutaSelect = RutaSeleccionada.toString();

                                                //Convertir a int los valores
                                                int CantLiquidInt = Integer.valueOf(CantidadLiquidacion);
                                                int CantCombusInt = Integer.valueOf(CantidadCombustible);
                                                int NumVueltasInt = Integer.valueOf(NumeroVueltas);
                                                int NumB1Int = Integer.valueOf(NumBolet1);
                                                int NumB2Int = Integer.valueOf(NumBolet2);
                                                int NumB3Int = Integer.valueOf(NumBolet3);

                                                //declarar las variables con respecto a las liquidaciones
                                                Liquidaciones L = new Liquidaciones();
                                                L.setIDLiquidacion(UUID.randomUUID().toString());
                                                L.setFechaLiquidacion(FechaLiquidación);
                                                L.setCantidadLiquidacion(CantLiquidInt);
                                                L.setCantidadCombustible(CantCombusInt);
                                                L.setNumeroVueltas(NumVueltasInt);
                                                L.setNumeroBoletera1(NumB1Int);
                                                L.setNumeroBoletera2(NumB2Int);
                                                L.setNumeroBoletera3(NumB3Int);
                                                L.setNombreOperador(OpeSelect);
                                                L.setRutaLiquidacion(RutaSelect);
                                                L.setNumUnidad(NumUnidad);

                                                //Guardamos datos a firebase
                                                databaseReference.child("Liquidaciones").child(L.getIDLiquidacion()).setValue(L);
                                                //ACTUALIZAR DATOS PARA ESCRITORIO
                                                String ProcesoActualizar = "1";
                                                databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                                                //--------------------
                                                Toast.makeText(getActivity(), "Liquidación agregada", Toast.LENGTH_SHORT).show();
                                                dialog3.dismiss();
                                            }
                                        });

                                        dialog3.show();
                                    }
                                }
                            });
                            dialog2.show();
                        }
                    }
                });
                dialog.show();

            }
        });
        //------------- BOTÓN DE ROLES ------------------
        btnRoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BotonRoles();
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_rol_trabajo);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarRol);
                Button btnGuardar = dialog.findViewById(R.id.btnDescargarRoles);
                ListView RU = dialog.findViewById(R.id.listUnidadRol);
                ListView RC = dialog.findViewById(R.id.listChoferRol);
                ListView RR = dialog.findViewById(R.id.listRutaRol);

                ArrayList<Liquidaciones> Roles_Unidades = new ArrayList<>();
                ArrayList<Liquidaciones> Roles_Choferes = new ArrayList<>();
                ArrayList<Liquidaciones> Roles_Rutas = new ArrayList<>();
                //METODO GRAFICA GANANCIAS (CHOFER Y UNIDADES)
                databaseReference.child("Liquidaciones").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Roles_Unidades.clear();
                            Roles_Choferes.clear();
                            Roles_Rutas.clear();
                            Nom_Rutas.clear();
                            Nom_Choferes.clear();
                            Nom_Unidades.clear();
                            for (DataSnapshot item : snapshot.getChildren()) {
                                int Ganacia = Integer.parseInt(item.child("cantidadLiquidacion").getValue().toString());
                                String NumUnidad = item.child("numUnidad").getValue().toString();
                                String NombreChofer = item.child("nombreOperador").getValue().toString();
                                String NombreRuta = item.child("rutaLiquidacion").getValue().toString();
                                //Ganacia liquidacion en general
                                Roles_Unidades.add(new Liquidaciones(Ganacia, NombreChofer, NumUnidad, NombreRuta));
                                Roles_Choferes.add(new Liquidaciones(Ganacia, NombreChofer, NumUnidad, NombreRuta));
                                Roles_Rutas.add(new Liquidaciones(Ganacia, NombreChofer, NumUnidad, NombreRuta));
                            }
                            //-----------------(GANANCIA UNIDAD)----------------------
                            ArrayList<String> repetidos = new ArrayList<>();
                            for (int i = 0; i < 3; i++) {
                                for (int j = i + 1; j < 3 && !repetidos.contains(Roles_Unidades.get(i)); j++) {
                                    if (Roles_Unidades.get(i).getNumUnidad().equals(Roles_Unidades.get(j).getNumUnidad())) {
                                        int TotalGanancia = Roles_Unidades.get(i).getCantidadLiquidacion() + Roles_Unidades.get(j).getCantidadLiquidacion();
                                        Roles_Unidades.get(i).setCantidadLiquidacion(TotalGanancia);
                                        Roles_Unidades.remove(Roles_Unidades.get(j));
                                    }
                                }
                            }
                            //ORDENO DE MAYOR A MENOS
                            Collections.sort(Roles_Unidades, new Comparator<Liquidaciones>() {
                                @Override
                                public int compare(Liquidaciones l1, Liquidaciones l2) {
                                    return new Integer(l2.getCantidadLiquidacion()).compareTo(new Integer(l1.getCantidadLiquidacion()));
                                }
                            });
                            //AGREGO LOS NOMBRES A LIST
                            for (int i = 0; i < 3; i++) {
                                String u = Roles_Unidades.get(i).getNumUnidad();
                                Nom_Unidades.add(u);
                            }
                            ArrayAdapter UnidadesAdapter = new ArrayAdapter(getActivity(), R.layout.item_list_simple, Nom_Unidades);
                            RU.setAdapter(UnidadesAdapter);

                            //-----------------(GANANCIA CHOFER)----------------------
                            ArrayList<String> repetidosC = new ArrayList<>();
                            for (int i = 0; i < 3; i++) {
                                for (int j = i + 1; j < 3 && !repetidosC.contains(Roles_Choferes.get(i)); j++) {
                                    if (Roles_Choferes.get(i).getNombreOperador().equals(Roles_Choferes.get(j).getNombreOperador())) {
                                        int TotalGanancia = Roles_Choferes.get(i).getCantidadLiquidacion() + Roles_Choferes.get(j).getCantidadLiquidacion();
                                        Roles_Choferes.get(i).setCantidadLiquidacion(TotalGanancia);
                                        Roles_Choferes.remove(Roles_Choferes.get(j));
                                    }
                                }
                            }
                            //ORDENO DE MAYOR A MENOS
                            Collections.sort(Roles_Choferes, new Comparator<Liquidaciones>() {
                                @Override
                                public int compare(Liquidaciones l1, Liquidaciones l2) {
                                    return new Integer(l2.getCantidadLiquidacion()).compareTo(new Integer(l1.getCantidadLiquidacion()));
                                }
                            });
                            //AGREGO LOS NOMBRES A LIST
                            for (int i = 0; i < 3; i++) {
                                String c = Roles_Choferes.get(i).getNombreOperador();
                                Nom_Choferes.add(c);
                            }
                            ArrayAdapter AdapterChoferes = new ArrayAdapter(getActivity(), R.layout.item_list_simple, Nom_Choferes);
                            RC.setAdapter(AdapterChoferes);
                            //-----------------(GANANCIA RUTA)----------------------
                            ArrayList<String> repetidosR = new ArrayList<>();
                            for (int i = 0; i < 3; i++) {
                                for (int j = i + 1; j < 3 && !repetidosR.contains(Roles_Rutas.get(i)); j++) {
                                    if (Roles_Rutas.get(i).getRutaLiquidacion().equals(Roles_Rutas.get(j).getRutaLiquidacion())) {
                                        int TotalGanancia = Roles_Rutas.get(i).getCantidadLiquidacion() + Roles_Rutas.get(j).getCantidadLiquidacion();
                                        Roles_Rutas.get(i).setCantidadLiquidacion(TotalGanancia);
                                        Roles_Rutas.remove(Roles_Rutas.get(j));
                                    }
                                }
                            }
                            //ORDENO DE MAYOR A MENOS
                            Collections.sort(Roles_Rutas, new Comparator<Liquidaciones>() {
                                @Override
                                public int compare(Liquidaciones l1, Liquidaciones l2) {
                                    return new Integer(l2.getCantidadLiquidacion()).compareTo(new Integer(l1.getCantidadLiquidacion()));
                                }
                            });
                            //AGREGO LOS NOMBRES A LIST
                            for (int i = 0; i < 3; i++) {
                                String r = Roles_Rutas.get(i).getRutaLiquidacion();
                                String l = String.valueOf(Roles_Rutas.get(i).getCantidadLiquidacion());
                                Nom_Rutas.add(r);
                            }
                            ArrayAdapter AdapterRutas = new ArrayAdapter(getActivity(), R.layout.item_list_simple, Nom_Rutas);
                            RR.setAdapter(AdapterRutas);
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY LIQUIDACIONES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
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
                        dialog.dismiss();
                    }
                });
                //------------- BOTON GUARDAR RUTA DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                dialog.show();
            }
        });
        return root;
    }

    //----------- VERIFICAR ASISTENCIA -----------------
    private void verificarAsistencias() {
        DatabaseReference asistenciasRef = FirebaseDatabase.getInstance().getReference().child("Asistencias");
        DatabaseReference huellasRef = FirebaseDatabase.getInstance().getReference().child("Huellas");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        asistenciasRef.child(fechaActual).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> empleadosConAsistencia = new ArrayList<>();

                for (DataSnapshot empleadoSnapshot : dataSnapshot.getChildren()) {
                    Asistencias asistencia = empleadoSnapshot.getValue(Asistencias.class);
                    if (asistencia != null) {
                        empleadosConAsistencia.add(asistencia.getNombreEmpleado());
                    }
                }

                // Obtener la lista completa de empleados
                huellasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> todosLosEmpleados = new ArrayList<>();

                        for (DataSnapshot huellaSnapshot : dataSnapshot.getChildren()) {
                            String nombreEmpleado = huellaSnapshot.child("nomUsuario").getValue(String.class);
                            todosLosEmpleados.add(nombreEmpleado);
                        }

                        // Comparar y obtener los empleados sin asistencia
                        List<String> empleadosSinAsistencia = new ArrayList<>(todosLosEmpleados);
                        empleadosSinAsistencia.removeAll(empleadosConAsistencia);

                        // Mostrar mensaje Toast si hay empleados sin asistencia
                        if (!empleadosSinAsistencia.isEmpty()) {
                            StringBuilder mensaje = new StringBuilder("Hay empleados con retardo:\n");
                            for (String empleado : empleadosSinAsistencia) {
                                mensaje.append(empleado).append("\n");
                            }
                            mostrarNotificacion(mensaje.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores de lectura de la base de datos
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }

    //----------- NOTIFICACION -----------------
    private void mostrarNotificacion(String mensaje) {
        // Configurar la intención para abrir la actividad deseada al hacer clic en la notificación
        Intent intent = new Intent(getActivity(), FragmentAsistencias.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Usar FLAG_IMMUTABLE o FLAG_MUTABLE según sea necesario
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE  // Puedes usar FLAG_MUTABLE si es necesario
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                .setSmallIcon(R.drawable.ic_asistencias)
                .setContentTitle("Empleados con retardo")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Obtener el administrador de notificaciones
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // Mostrar la notificación
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // aquí para solicitar los permisos faltantes, y luego anular
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // para manejar el caso en que el usuario otorga el permiso. Consulta la documentación
            // de ActivityCompat#requestPermissions para obtener más detalles.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    //------------- OBTENER DATOS GASTOS ------------------
    private void obtenerDatosGastos() {
        // Escuchar cambios en la base de datos
        databaseReferenceGasto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Procesar datos al cambiar
                generarGrafico(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error de base de datos
                Toast.makeText(getActivity(), "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-------------------------------
    private void generarGrafico(DataSnapshot dataSnapshot) {
        // Crear un mapa para almacenar los gastos por unidad
        Map<String, Float> gastosPorUnidad = new HashMap<>();

        // Iterar sobre los datos de Firebase
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            GastosUnidades gasto = snapshot.getValue(GastosUnidades.class);

            if (gasto != null) {
                String numUnidad = gasto.getNumUnidad();
                float monto = (float) gasto.getMonto();

                // Agregar el monto al total por unidad
                if (gastosPorUnidad.containsKey(numUnidad)) {
                    gastosPorUnidad.put(numUnidad, gastosPorUnidad.get(numUnidad) + monto);
                } else {
                    gastosPorUnidad.put(numUnidad, monto);
                }
            }
        }

        // Crear una lista de entradas para la gráfica
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : gastosPorUnidad.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        // Configurar la apariencia de la gráfica
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        //Configurar leyendas
        Legend legend = GraficUnidadGasto.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Centra las leyendas

        // Configurar los datos y mostrar la gráfica
        PieData data = new PieData(dataSet);
        GraficUnidadGasto.setData(data);
        GraficUnidadGasto.getDescription().setEnabled(false);
        GraficUnidadGasto.invalidate(); // refresh
    }

    //------------- GRAFICA  GANACIA UNIDAD ------------------
    private void GraficoGananciasUnidad() {
        // Obtener datos de la base de datos
        Map<String, Double> gananciaPorUnidad = calcularGananciaPorUnidad();

        // Configurar y mostrar el gráfico
        configurarGrafico(gananciaPorUnidad);
    }

    //-------------------------------
    private Map<String, Double> calcularGananciaPorUnidad() {
        final Map<String, Double> gananciaPorUnidad = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Liquidaciones");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener el valor de "numUnidad" y "cantidadLiquidacion" de cada nodo
                    String numUnidad = snapshot.child("numUnidad").getValue(String.class);
                    double cantidadLiquidacion = snapshot.child("cantidadLiquidacion").getValue(Double.class);

                    // Sumar la cantidadLiquidacion a la ganancia correspondiente en gananciaPorUnidad
                    if (gananciaPorUnidad.containsKey(numUnidad)) {
                        double gananciaActual = gananciaPorUnidad.get(numUnidad);
                        gananciaPorUnidad.put(numUnidad, gananciaActual + cantidadLiquidacion);
                    } else {
                        gananciaPorUnidad.put(numUnidad, cantidadLiquidacion);
                    }
                }

                // Una vez que se hayan procesado todos los datos, actualiza el gráfico
                configurarGrafico(gananciaPorUnidad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });

        // Devolver un mapa vacío (los datos se llenarán en onDataChange)
        return gananciaPorUnidad;
    }

    //-------------------------------
    private void configurarGrafico(Map<String, Double> gananciaPorUnidad) {
        List<BarEntry> entries = convertirDatosParaGrafico(gananciaPorUnidad);

        BarDataSet dataSet = new BarDataSet(entries, "");
        BarData barData = new BarData(dataSet);

        int[] colores = ColorTemplate.MATERIAL_COLORS;
        dataSet.setColors(colores);

        // Configurar etiquetas para el eje X
        XAxis xAxis = GananciaUnidad.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Coloca el eje X en la parte inferior
        xAxis.setGranularity(1f); // Asegura que solo se muestre una etiqueta por cada unidad
        xAxis.setValueFormatter(new IndexAxisValueFormatter(obtenerEtiquetasUnidad(gananciaPorUnidad)));

        // Configuraciones adicionales para las etiquetas
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(entries.size()); // Establece la cantidad de etiquetas que se mostrarán

        // Configurar el gráfico
        GananciaUnidad.setData(barData);
        GananciaUnidad.setFitBars(true); // Ajusta el ancho de las barras
        GananciaUnidad.getDescription().setEnabled(false); // Deshabilita la descripción del gráfico
        GananciaUnidad.getLegend().setEnabled(false); // Deshabilita la leyenda

        // Actualiza el gráfico
        GananciaUnidad.invalidate();
    }

    private List<String> obtenerEtiquetasUnidad(Map<String, Double> gananciaPorUnidad) {
        List<String> etiquetas = new ArrayList<>();

        for (String numUnidad : gananciaPorUnidad.keySet()) {
            etiquetas.add(numUnidad);
        }

        return etiquetas;
    }

    //-------------------------------
    private List<BarEntry> convertirDatosParaGrafico(Map<String, Double> gananciaPorUnidad) {
        List<BarEntry> entries = new ArrayList<>();
        int i = 0;

        for (Map.Entry<String, Double> entry : gananciaPorUnidad.entrySet()) {
            entries.add(new BarEntry(i++, entry.getValue().floatValue()));
        }

        return entries;
    }

    //------------- GRAFICA  GANACIA RUTA ------------------
    private Map<String, Double> calcularGananciaPorRuta() {
        final Map<String, Double> gananciaPorRuta = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Liquidaciones");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener el valor de "rutaLiquidacion" y "cantidadLiquidacion" de cada nodo
                    String rutaLiquidacion = snapshot.child("rutaLiquidacion").getValue(String.class);
                    double cantidadLiquidacion = snapshot.child("cantidadLiquidacion").getValue(Double.class);

                    // Sumar la cantidadLiquidacion a la ganancia correspondiente en gananciaPorRuta
                    if (gananciaPorRuta.containsKey(rutaLiquidacion)) {
                        double gananciaActual = gananciaPorRuta.get(rutaLiquidacion);
                        gananciaPorRuta.put(rutaLiquidacion, gananciaActual + cantidadLiquidacion);
                    } else {
                        gananciaPorRuta.put(rutaLiquidacion, cantidadLiquidacion);
                    }
                }

                // Una vez que se hayan procesado todos los datos, actualiza el gráfico
                configurarGraficoPie(gananciaPorRuta);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });

        // Devolver un mapa vacío (los datos se llenarán en onDataChange)
        return gananciaPorRuta;
    }

    //-------------------------------
    private void configurarGraficoPie(Map<String, Double> gananciaPorRuta) {
        List<PieEntry> entries = convertirDatosParaGraficoPie(gananciaPorRuta);

        PieDataSet dataSet = new PieDataSet(entries, "");
        PieData pieData = new PieData(dataSet);

        // Configurar colores de Material Design
        int[] colores = ColorTemplate.MATERIAL_COLORS;
        dataSet.setColors(colores);

        //Configurar leyendas
        Legend legend = GananciasRutas.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setEnabled(true);// Centra las leyendas

        // Configurar el gráfico de pie
        GananciasRutas.setData(pieData);
        GananciasRutas.getDescription().setEnabled(false); // Deshabilita la descripción del gráfico

        // Actualiza el gráfico
        GananciasRutas.invalidate();
    }

    //-------------------------------
    private List<PieEntry> convertirDatosParaGraficoPie(Map<String, Double> gananciaPorRuta) {
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : gananciaPorRuta.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        return entries;
    }

    //------------- DISEÑO DE LOS BOTONES ------------------
    private void BotonLiquidacion() {
        //DISEÑO BOTONES
        btnLiquidacion.setEnabled(true);
        btnLiquidacion.setTextColor(Color.WHITE);
        btnLiquidacion.setBackground(getResources().getDrawable(R.drawable.bg_black_corner_10));

        btnRoles.setEnabled(true);
        btnRoles.setTextColor(Color.BLACK);
        btnRoles.setBackground(getResources().getDrawable(R.drawable.button_border));
    }

    private void BotonRoles() {
        //DISEÑO BOTONES
        btnRoles.setEnabled(true);
        btnRoles.setTextColor(Color.WHITE);
        btnRoles.setBackground(getResources().getDrawable(R.drawable.bg_black_corner_10));

        btnLiquidacion.setEnabled(true);
        btnLiquidacion.setTextColor(Color.BLACK);
        btnLiquidacion.setBackground(getResources().getDrawable(R.drawable.button_border));
    }

    //------------- MOSTRAR FECHA DE AYER ------------------
    private String getDate() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.DATE, -1);
        Date DiaAyer = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(DiaAyer);
    }

}