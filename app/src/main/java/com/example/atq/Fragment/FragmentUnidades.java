package com.example.atq.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterGastosUnidades;
import com.example.atq.Adapter.AdapterPolizasUnidades;
import com.example.atq.Adapter.AdapterUnidades;
import com.example.atq.Model.GastosUnidades;
import com.example.atq.Model.PolizasUnidades;
import com.example.atq.Model.TipoGasto;
import com.example.atq.Model.Unidades;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;

public class FragmentUnidades extends Fragment {

    private static final int PICK_IMAGE_UNIDAD = 1;
    FloatingActionButton fabUnidades, fabPolizas, fabGastos;
    Button btnUnidades, btnPolizas, btnGasto;
    SearchView searchView;
    String UnidadSeleccionada, UnidadSeleccionadaGasto, GastoSeleccionado;
    //------------------
    RecyclerView RV_Unidades, RV_Polizas, RV_Gastos;
    AdapterUnidades adapterUnidades;
    AdapterPolizasUnidades adapterPolizas;
    AdapterGastosUnidades adapterGastos;
    //------------------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //-----------------
    ImageView imgUnidad;
    Uri ImageUriU;
    String URLFotoUnidad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_unidades, container, false);

        fabUnidades = root.findViewById(R.id.fabUnidades);
        fabPolizas = root.findViewById(R.id.fabPolizas);
        fabGastos = root.findViewById(R.id.fabGastos);

        btnUnidades = root.findViewById(R.id.btnUnidades);
        btnGasto = root.findViewById(R.id.btnGastos);
        btnPolizas = root.findViewById(R.id.btnPolizas);

        RV_Unidades = root.findViewById(R.id.listUnidades);
        RV_Polizas = root.findViewById(R.id.listPolizas);
        RV_Gastos = root.findViewById(R.id.listGastos);

        searchView = root.findViewById(R.id.txtbuscadorUnidades);
        //------------- BUSCADOR DE UNIDADES -----------------
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
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (UNIDADES) ------------------
        RV_Unidades.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Unidades> options =
                new FirebaseRecyclerOptions.Builder<Unidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Unidades"), Unidades.class)
                        .build();

        adapterUnidades = new AdapterUnidades(options);
        RV_Unidades.setAdapter(adapterUnidades);
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (POLIZAS) ------------------
        RV_Polizas.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<PolizasUnidades> options2 =
                new FirebaseRecyclerOptions.Builder<PolizasUnidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Polizas"), PolizasUnidades.class)
                        .build();

        adapterPolizas = new AdapterPolizasUnidades(options2);
        RV_Polizas.setAdapter(adapterPolizas);
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (GASTOS) ------------------
        RV_Gastos.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<GastosUnidades> options3 =
                new FirebaseRecyclerOptions.Builder<GastosUnidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Gastos"), GastosUnidades.class)
                        .build();

        adapterGastos = new AdapterGastosUnidades(options3);
        RV_Gastos.setAdapter(adapterGastos);
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- BOTON UNIDADES ------------------
        btnUnidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DISEÑO DE LOS BOTONES
                BotonesUnidades();
            }
        });
        //------------- BOTON POLIZAS ------------------
        btnPolizas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DISEÑO DE LOS BOTONES
                BotonesPolizas();
            }
        });
        //------------- BOTON GASTOS ------------------
        btnGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DISEÑO DE LOS BOTONES
                BotonesGastos();
            }
        });
        //------------- FAB UNIDADES ------------------
        fabUnidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_unidad);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarUnidad);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarUnidad);

                //Imagen de la unidad
                ButterKnife.bind(getActivity());
                imgUnidad = dialog.findViewById(R.id.imgUnidad);
                //------------- ELEGIR IMAGEN DE LA UNIDAD ------------------
                imgUnidad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OpenFile(PICK_IMAGE_UNIDAD);
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //------------- BOTON GUARDAR UNIDAD DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text de los datos de la unidad
                        EditText numUnidad = dialog.findViewById(R.id.AddNumUnidad);
                        EditText numSerie = dialog.findViewById(R.id.AddNumSerie);
                        EditText placasUnidad = dialog.findViewById(R.id.AddPlacasUnidad);


                        //String de los datos de la unidad
                        String NumUnidad = numUnidad.getText().toString();
                        String NumSerie = numSerie.getText().toString();
                        String PlacasUnidad = placasUnidad.getText().toString();


                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (NumUnidad.isEmpty() || NumSerie.isEmpty() || PlacasUnidad.isEmpty()) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            //Declarar los valores de la unidad
                            Unidades unidades = new Unidades();
                            unidades.setIDUnidad(UUID.randomUUID().toString());
                            unidades.setNumUnidad(NumUnidad);
                            unidades.setNumSerie(NumSerie);
                            unidades.setPlacas(PlacasUnidad);

                            if (ImageUriU == null) {
                                Toast.makeText(getActivity(), "ERROR. Seleccione una foto", Toast.LENGTH_SHORT).show();
                            } else {
                                //FOTO DE LA UNIDAD
                                StorageReference Folder = FirebaseStorage.getInstance().getReference().child("Unidades").child(unidades.getIDUnidad());
                                final StorageReference file_unidad = Folder.child("file" + ImageUriU.getLastPathSegment());

                                //Valor String a la foto de la unidad
                                file_unidad.putFile(ImageUriU).addOnSuccessListener(taskSnapshot -> file_unidad.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            URLFotoUnidad = String.valueOf(uri);
                                            //AGREGAR FOTOS DEL CHOFER
                                            unidades.setFotoUnidad(URLFotoUnidad);
                                            //SUBIR A FIREBASE TODA LA INFORMACIÓN
                                            databaseReference.child("Unidades").child(unidades.getIDUnidad()).setValue(unidades);
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                                        }));
                                //--------------------------------------------------------------
                                Toast.makeText(getActivity(), "Unidad agregada", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
        //------------- FAB POLIZAS ------------------
        fabPolizas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_poliza);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarPoliza);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarPoliza);

                //Boton de la fecha
                TextView btnFechaPoliza = dialog.findViewById(R.id.btnFechaPoliza);
                //fecha
                TextView FechaPoliza = dialog.findViewById(R.id.txtFechaPoliza);

                //MOSTRAR SPINNER DE LAS UNIDADES
                Spinner unidadPoliza = dialog.findViewById(R.id.AddUnidadPoliza);

                //Mostrar placas de la unidad
                final EditText PlacasUnidad = (EditText) dialog.findViewById(R.id.AddPlacasUnidadPoliza);

                final List<Unidades> ListUnidades = new ArrayList<>();
                //METODO DROPDOWN UNIDADES
                databaseReference.child("Unidades").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String numUnidad = item.child("numUnidad").getValue().toString();
                                String placasUnidad = item.child("placas").getValue().toString();
                                ListUnidades.add(new Unidades(numUnidad, placasUnidad));
                            }
                            ArrayAdapter<Unidades> AdapterUnidades = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListUnidades);
                            unidadPoliza.setAdapter(AdapterUnidades);
                            //SEGUN LA UNIDAD QUE ELIGA PONER LAS PLACAS
                            unidadPoliza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    String placas = ListUnidades.get(i).getPlacas();
                                    PlacasUnidad.setText(placas);
                                    UnidadSeleccionada = ListUnidades.get(i).getNumUnidad();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY UNIDADES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //------------- FECHA DE LA POLIZA ------------------
                btnFechaPoliza.setOnClickListener(new View.OnClickListener() {
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
                                FechaPoliza.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //------------- BOTON GUARDAR POLIZA DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text de los datos de la poliza
                        EditText empresaPoliza = dialog.findViewById(R.id.AddEmpresaPoliza);
                        EditText numPoliza = dialog.findViewById(R.id.AddNumPoliza);


                        //String de los datos de la poliza
                        String NumUnidad = UnidadSeleccionada.toString();
                        String PlacasUnidadSelect = PlacasUnidad.getText().toString();
                        String EmpresaPoliza = empresaPoliza.getText().toString();
                        String NumPoliza = numPoliza.getText().toString();
                        String FechaVencimientoPoliza = FechaPoliza.getText().toString();


                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (NumUnidad.isEmpty() || EmpresaPoliza.isEmpty() || NumPoliza.isEmpty()
                                || FechaVencimientoPoliza.equals("0000-00-00")) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            //Declarar los valores de la poliza
                            PolizasUnidades poliza = new PolizasUnidades();
                            poliza.setIDPoliza(UUID.randomUUID().toString());
                            poliza.setNumUnidad(NumUnidad);
                            poliza.setPlacas(PlacasUnidadSelect);
                            poliza.setEmpresaPoliza(EmpresaPoliza);
                            poliza.setNumeroPoliza(NumPoliza);
                            poliza.setFechaVencimiento(FechaVencimientoPoliza);

                            //Guardamos datos a firebase
                            databaseReference.child("Polizas").child(poliza.getIDPoliza()).setValue(poliza);
                            //ACTUALIZAR DATOS PARA ESCRITORIO
                            String ProcesoActualizar = "1";
                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                            //--------------------
                            Toast.makeText(getActivity(), "Poliza agregada", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        //------------- FAB GASTOS ------------------
        fabGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_gasto_unidad);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarGasto);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarGasto);

                //Boton de la fecha
                TextView btnFechaGasto = dialog.findViewById(R.id.btnFechaGasto);
                //fecha
                TextView FechaGasto = dialog.findViewById(R.id.txtFechaGasto);

                //MOSTRAR SPINNER DE LAS UNIDADES
                Spinner unidadGasto = dialog.findViewById(R.id.AddUnidadGasto);
                //MOSTRAR SPINNER DE LOS TIPOS DE GASTOS
                Spinner TipoDeGasto = dialog.findViewById(R.id.AddTipoGasto);

                final List<Unidades> ListUnidades = new ArrayList<>();
                final List<TipoGasto> ListGasto = new ArrayList<>();

                //METODO DROPDOWN UNIDADES
                databaseReference.child("Unidades").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String numUnidad = item.child("numUnidad").getValue().toString();
                                String placasUnidad = item.child("placas").getValue().toString();
                                ListUnidades.add(new Unidades(numUnidad, placasUnidad));
                            }
                            ArrayAdapter<Unidades> AdapterUnidades = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListUnidades);
                            unidadGasto.setAdapter(AdapterUnidades);
                            //PONER EL VALOR DE LA UNIDAD SEGUN LA UNIDAD SELECCIONADA
                            unidadGasto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    UnidadSeleccionadaGasto = ListUnidades.get(i).getNumUnidad();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY UNIDADES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //METODO DROPDOWN GASTOS
                databaseReference.child("TiposGastos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String idGasto = item.getKey();
                                String NombreGasto = item.child("NombreGasto").getValue().toString();
                                ListGasto.add(new TipoGasto(idGasto, NombreGasto));
                            }
                            ArrayAdapter<TipoGasto> AdapterGastos = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListGasto);
                            TipoDeGasto.setAdapter(AdapterGastos);
                            //PONER EL VALOR DE LA UNIDAD SEGUN LA UNIDAD SELECCIONADA
                            TipoDeGasto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    GastoSeleccionado = ListGasto.get(i).getNombreGasto();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY TIPOS DE GASTOS EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //------------- FECHA DEL GASTO ------------------
                btnFechaGasto.setOnClickListener(new View.OnClickListener() {
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
                                FechaGasto.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //------------- BOTON GUARDAR POLIZA DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text de los datos de la poliza
                        EditText monto = dialog.findViewById(R.id.AddMontoGasto);
                        EditText descripcion = dialog.findViewById(R.id.AddDescGasto);


                        //String de los datos de la poliza
                        String NumUnidad = UnidadSeleccionadaGasto.toString();
                        String TipoGasto = GastoSeleccionado.toString();
                        String Monto = monto.getText().toString();
                        String Descripcion = descripcion.getText().toString();
                        String FechaDelGasto = FechaGasto.getText().toString();


                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (NumUnidad.isEmpty() || TipoGasto.isEmpty() || Monto.isEmpty()
                                || Descripcion.isEmpty() || FechaDelGasto.equals("0000-00-00")) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            int MontoInt = Integer.valueOf(Monto);
                            //Declarar los valores de la poliza
                            GastosUnidades gastos = new GastosUnidades();
                            gastos.setIDGasto(UUID.randomUUID().toString());
                            gastos.setNumUnidad(NumUnidad);
                            gastos.setTipoGasto(TipoGasto);
                            gastos.setMonto(MontoInt);
                            gastos.setDescripción(Descripcion);
                            gastos.setFechaGasto(FechaDelGasto);

                            //Guardamos datos a firebase
                            databaseReference.child("Gastos").child(gastos.getIDGasto()).setValue(gastos);
                            //ACTUALIZAR DATOS PARA ESCRITORIO
                            String ProcesoActualizar = "1";
                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                            //--------------------
                            Toast.makeText(getActivity(), "gasto agregada", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        return root;
    }

    //------------- DISEÑO DE LOS BOTONES ------------------
    private void BotonesUnidades() {
        //FAB
        fabUnidades.setVisibility(View.VISIBLE);
        fabPolizas.setVisibility(View.GONE);
        fabGastos.setVisibility(View.GONE);
        //DISEÑO BOTONES
        btnUnidades.setEnabled(true);
        btnUnidades.setTextColor(Color.WHITE);
        btnUnidades.setBackground(getResources().getDrawable(R.drawable.bg_black_corner_10));

        btnGasto.setEnabled(true);
        btnGasto.setTextColor(Color.BLACK);
        btnGasto.setBackground(getResources().getDrawable(R.drawable.button_border));

        btnPolizas.setEnabled(true);
        btnPolizas.setTextColor(Color.BLACK);
        btnPolizas.setBackground(getResources().getDrawable(R.drawable.button_border));
        //LISTAS
        RV_Unidades.setVisibility(View.VISIBLE);
        RV_Polizas.setVisibility(View.GONE);
        RV_Gastos.setVisibility(View.GONE);
    }

    private void BotonesPolizas() {
        //FAB
        fabPolizas.setVisibility(View.VISIBLE);
        fabUnidades.setVisibility(View.GONE);
        fabGastos.setVisibility(View.GONE);
        //DISEÑO BOTONES
        btnPolizas.setEnabled(true);
        btnPolizas.setTextColor(Color.WHITE);
        btnPolizas.setBackground(getResources().getDrawable(R.drawable.bg_black_corner_10));

        btnGasto.setEnabled(true);
        btnGasto.setTextColor(Color.BLACK);
        btnGasto.setBackground(getResources().getDrawable(R.drawable.button_border));

        btnUnidades.setEnabled(true);
        btnUnidades.setTextColor(Color.BLACK);
        btnUnidades.setBackground(getResources().getDrawable(R.drawable.button_border));
        //LISTAS
        RV_Polizas.setVisibility(View.VISIBLE);
        RV_Unidades.setVisibility(View.GONE);
        RV_Gastos.setVisibility(View.GONE);
    }

    private void BotonesGastos() {
        //FAB
        fabGastos.setVisibility(View.VISIBLE);
        fabUnidades.setVisibility(View.GONE);
        fabPolizas.setVisibility(View.GONE);
        //DISEÑO BOTONES
        btnGasto.setEnabled(true);
        btnGasto.setTextColor(Color.WHITE);
        btnGasto.setBackground(getResources().getDrawable(R.drawable.bg_black_corner_10));

        btnPolizas.setEnabled(true);
        btnPolizas.setTextColor(Color.BLACK);
        btnPolizas.setBackground(getResources().getDrawable(R.drawable.button_border));

        btnUnidades.setEnabled(true);
        btnUnidades.setTextColor(Color.BLACK);
        btnUnidades.setBackground(getResources().getDrawable(R.drawable.button_border));
        //LISTAS
        RV_Gastos.setVisibility(View.VISIBLE);
        RV_Polizas.setVisibility(View.GONE);
        RV_Unidades.setVisibility(View.GONE);
    }

    //------------- MOSTRAR ADAPTERS ------------------
    @Override
    public void onStart() {
        super.onStart();
        //------------------------------
        RV_Gastos.getRecycledViewPool().clear();
        adapterGastos.notifyDataSetChanged();

        RV_Polizas.getRecycledViewPool().clear();
        adapterPolizas.notifyDataSetChanged();

        RV_Unidades.getRecycledViewPool().clear();
        adapterUnidades.notifyDataSetChanged();
        //------------------------------
        adapterUnidades.startListening();
        adapterPolizas.startListening();
        adapterGastos.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterUnidades.stopListening();
        adapterPolizas.stopListening();
        adapterGastos.stopListening();

    }

    //------------- SUBIR FOTO CHOFER  ------------------
    public void OpenFile(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_UNIDAD) {
                ImageUriU = data.getData();
                Picasso.with(getActivity()).load(ImageUriU).into(imgUnidad);
            }
        }
    }

    //------------- BUSCADOR  ------------------
    private void txtBuscar(String str) {
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (UNIDADES) ------------------
        RV_Unidades.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Unidades> options =
                new FirebaseRecyclerOptions.Builder<Unidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Unidades").orderByChild("numUnidad").startAt(str).endAt(str + "~"), Unidades.class)
                        .build();

        adapterUnidades = new AdapterUnidades(options);
        adapterUnidades.startListening();
        RV_Unidades.setAdapter(adapterUnidades);
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (POLIZAS) ------------------
        RV_Polizas.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<PolizasUnidades> options2 =
                new FirebaseRecyclerOptions.Builder<PolizasUnidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Polizas").orderByChild("numUnidad").startAt(str).endAt(str + "~"), PolizasUnidades.class)
                        .build();

        adapterPolizas = new AdapterPolizasUnidades(options2);
        adapterPolizas.startListening();
        RV_Polizas.setAdapter(adapterPolizas);
        //------------- MOSTRAR DATOS EN RECYCLERVIEW (GASTOS) ------------------
        RV_Gastos.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<GastosUnidades> options3 =
                new FirebaseRecyclerOptions.Builder<GastosUnidades>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Gastos").orderByChild("numUnidad").startAt(str).endAt(str + "~"), GastosUnidades.class)
                        .build();

        adapterGastos = new AdapterGastosUnidades(options3);
        adapterGastos.startListening();
        RV_Gastos.setAdapter(adapterGastos);
    }

}
