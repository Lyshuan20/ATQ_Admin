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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Adapter.AdapterChoferes;
import com.example.atq.Model.Choferes;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.UUID;

import butterknife.ButterKnife;

public class FragmentOperadores extends Fragment {

    //----DATOS DEL CHOFER
    private static final int PICK_IMAGE_CHOFER = 1;
    private static final int PICK_IMAGE_LIC1 = 2;
    private static final int PICK_IMAGE_LIC2 = 3;
    FloatingActionButton fab;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    AdapterChoferes adapterChoferes;
    SearchView searchView;
    Uri ImageUric, ImageUriL1, ImageUriL2;
    ImageView imgChofer, imgLic1, imgLic2;
    String ApellidosChofer, NombreChofer, CURPChofer, Domicilio, FechaNacimiento, FechaIngreso, Telefono,
            Sangre, FechaLicencia, URLChofer, URLLic1, URLLic2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_operadores, container, false);

        fab = root.findViewById(R.id.fabChoferes);
        searchView = root.findViewById(R.id.txtbuscadorChoferes);
        //------------- BUSCADOR DE CHOFERES -----------------
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
        recyclerView = root.findViewById(R.id.listChoferes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<Choferes> options =
                new FirebaseRecyclerOptions.Builder<Choferes>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Choferes"), Choferes.class)
                        .build();

        adapterChoferes = new AdapterChoferes(options);
        recyclerView.setAdapter(adapterChoferes);
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
                dialog.setContentView(R.layout.agregar_chofer);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarChofer);
                Button btnGuardar = dialog.findViewById(R.id.btnGuardarChofer);

                //Botones de las fechas
                TextView btnFechaNac = dialog.findViewById(R.id.btnFechaNac);
                TextView btnFechaIng = dialog.findViewById(R.id.btnFechaIng);
                TextView btnFechaLic = dialog.findViewById(R.id.btnFechaLic);

                //fechas
                TextView FechaNac = dialog.findViewById(R.id.txtFechaNac);
                TextView FechaIng = dialog.findViewById(R.id.txtFechaIng);
                TextView FechaLic = dialog.findViewById(R.id.txtFechaLic);

                //------------- FECHA NACIMIENTO ------------------
                btnFechaNac.setOnClickListener(new View.OnClickListener() {
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
                                FechaNac.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });
                //------------- FECHA INGRESO ------------------
                btnFechaIng.setOnClickListener(new View.OnClickListener() {
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
                                FechaIng.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });
                //------------- FECHA LICENCIA ------------------
                btnFechaLic.setOnClickListener(new View.OnClickListener() {
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
                                FechaLic.setText(date);
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
                //------------- BOTON GUARDAR CHOFER DIALOG ------------------
                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit text de los datos
                        EditText nomChofer = dialog.findViewById(R.id.AddNombreChofer);
                        EditText apeChofer = dialog.findViewById(R.id.AddApellidosChofer);
                        EditText CURP = dialog.findViewById(R.id.AddCURP);
                        EditText domChofer = dialog.findViewById(R.id.AddDomicilioChofer);
                        EditText telChofer = dialog.findViewById(R.id.AddTelefonoChofer);
                        EditText sangreChofer = dialog.findViewById(R.id.AddTipoSangreChofer);

                        //String de los datos
                        NombreChofer = nomChofer.getText().toString();
                        ApellidosChofer = apeChofer.getText().toString();
                        CURPChofer = CURP.getText().toString();
                        Telefono = telChofer.getText().toString();
                        Domicilio = domChofer.getText().toString();
                        Sangre = sangreChofer.getText().toString();
                        FechaNacimiento = FechaNac.getText().toString();
                        FechaIngreso = FechaIng.getText().toString();
                        FechaLicencia = FechaLic.getText().toString();

                        //VERIFICAR QUE LOS CAMPOS NO ESTEN VACIOS
                        if (NombreChofer.isEmpty() || ApellidosChofer.isEmpty() || CURPChofer.isEmpty() || Domicilio.isEmpty()
                                || Sangre.isEmpty() || Telefono.isEmpty() || Telefono.length() < 10) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else if (FechaNacimiento.equals("0000-00-00") || FechaIngreso.equals("0000-00-00")
                                || FechaLicencia.equals("0000-00-00")) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            //------------- DIALOG ------------------
                            final Dialog dialog2 = new Dialog(getActivity());
                            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog2.setContentView(R.layout.agregar_choger_img);

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
                            Button btnCerrar = dialog2.findViewById(R.id.btnCancelarChoferI);
                            Button btnGuardar = dialog2.findViewById(R.id.btnGuardarChoferI);

                            ButterKnife.bind(getActivity());
                            imgChofer = dialog2.findViewById(R.id.imgChofer);
                            imgLic1 = dialog2.findViewById(R.id.imgLic1);
                            imgLic2 = dialog2.findViewById(R.id.imgLic2);

                            //------------- ELEGIR FOTO LICENCIA 1 ------------------
                            imgLic1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    OpenFile(PICK_IMAGE_LIC1);
                                }
                            });
                            //------------- ELEGIR FOTO LICENCIA 2 ------------------
                            imgLic2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    OpenFile(PICK_IMAGE_LIC2);
                                }
                            });
                            //------------- ELEGIR FOTO CHOFER ------------------
                            imgChofer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    OpenFile(PICK_IMAGE_CHOFER);
                                }
                            });
                            //------------- CERRAR DIALOG ------------------
                            btnCerrar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog2.dismiss();
                                }
                            });
                            //------------- BOTON IMAGENES DE CHOFERES ------------------
                            btnGuardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //DECLARAR LAS VARIABLES DEL CHOFER
                                    Choferes choferes = new Choferes();
                                    choferes.setIDChofer(UUID.randomUUID().toString());
                                    choferes.setApellidosChofer(ApellidosChofer);
                                    choferes.setNombreChofer(NombreChofer);
                                    choferes.setCURP(CURPChofer);
                                    choferes.setDomicilio(Domicilio);
                                    choferes.setFecha_nac(FechaNacimiento);
                                    choferes.setFecha_Ing(FechaIngreso);
                                    choferes.setTelefono(Telefono);
                                    choferes.setTipoSangre(Sangre);
                                    //LICENCIA DEL CHOFER
                                    choferes.setFecha_vencimiento_Lic(FechaLicencia);
                                    //FOTOS DEL CHOFER
                                    if (ImageUric == null || ImageUriL2 == null || ImageUriL1 == null) {
                                        Toast.makeText(getActivity(), "ERROR. Seleccione las fotos", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //FOTOS DEL CHOFER
                                        StorageReference Folder = FirebaseStorage.getInstance().getReference().child("Choferes").child(choferes.getIDChofer());
                                        final StorageReference file_chofer = Folder.child("file" + ImageUric.getLastPathSegment());
                                        final StorageReference file_Lic1 = Folder.child("file" + ImageUriL1.getLastPathSegment());
                                        final StorageReference file_Lic2 = Folder.child("file" + ImageUriL2.getLastPathSegment());
                                        //Valor String a la foto del chofer
                                        file_chofer.putFile(ImageUric).addOnSuccessListener(taskSnapshot -> file_chofer.getDownloadUrl()
                                                .addOnSuccessListener(uri -> {
                                                    URLChofer = String.valueOf(uri);
                                                    //Valor String a la foto de la licencia 1
                                                    file_Lic1.putFile(ImageUriL1).addOnSuccessListener(taskSnapshot2 -> file_Lic1.getDownloadUrl()
                                                            .addOnSuccessListener(uri2 -> {
                                                                URLLic1 = String.valueOf(uri2);
                                                                //Valor String a la foto de la licencia 1
                                                                file_Lic2.putFile(ImageUriL2).addOnSuccessListener(taskSnapshot3 -> file_Lic2.getDownloadUrl()
                                                                        .addOnSuccessListener(uri3 -> {
                                                                            URLLic2 = String.valueOf(uri3);
                                                                            //AGREGAR FOTOS DEL CHOFER
                                                                            choferes.setFotoChofer(URLChofer);
                                                                            choferes.setFotoLic1(URLLic1);
                                                                            choferes.setFotoLic2(URLLic2);
                                                                            //SUBIR A FIREBASE TODA LA INFORMACIÓN
                                                                            databaseReference.child("Choferes").child(choferes.getIDChofer()).setValue(choferes);
                                                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                                                            String ProcesoActualizar = "1";
                                                                            databaseReference.child("DatoActualizado").setValue(ProcesoActualizar);
                                                                            //--------------------
                                                                        }));
                                                            }));
                                                }));
                                        //--------------------------------------------------------------
                                        Toast.makeText(getActivity(), "Chofer agregado", Toast.LENGTH_SHORT).show();
                                        dialog2.dismiss();
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
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        adapterChoferes.notifyDataSetChanged();
        adapterChoferes.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterChoferes.stopListening();
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
            if (requestCode == PICK_IMAGE_CHOFER) {
                ImageUric = data.getData();
                Picasso.with(getActivity()).load(ImageUric).into(imgChofer);
            } else if (requestCode == PICK_IMAGE_LIC1) {
                ImageUriL1 = data.getData();
                Picasso.with(getActivity()).load(ImageUriL1).into(imgLic1);
            } else if (requestCode == PICK_IMAGE_LIC2) {
                ImageUriL2 = data.getData();
                Picasso.with(getActivity()).load(ImageUriL2).into(imgLic2);
            }
        }
    }

    //------------- BUSCADOR  ------------------
    private void txtBuscar(String str) {
        FirebaseRecyclerOptions<Choferes> options =
                new FirebaseRecyclerOptions.Builder<Choferes>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Choferes").orderByChild("nombreChofer").startAt(str).endAt(str + "~"), Choferes.class)
                        .build();

        adapterChoferes = new AdapterChoferes(options);
        adapterChoferes.startListening();
        recyclerView.setAdapter(adapterChoferes);
    }
}