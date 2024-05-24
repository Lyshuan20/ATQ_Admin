package com.example.atq.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.atq.Model.Choferes;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdapterChoferes extends FirebaseRecyclerAdapter<Choferes, AdapterChoferes.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterChoferes(@NonNull FirebaseRecyclerOptions<Choferes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Choferes choferes) {
        holder.NombreChofer.setText(choferes.getNombreChofer());
        holder.ApeChofer.setText(choferes.getApellidosChofer());
        holder.TelChofer.setText(choferes.getTelefono());

        Glide.with(holder.FotoChofer.getContext())
                .load(choferes.getFotoChofer())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.FotoChofer);
        //------------- VER CHOFER ------------------
        holder.itemChoferes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.NombreChofer.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_choferes);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarChofer);

                EditText nomChofer = dialog.findViewById(R.id.VerNombreChofer);
                EditText apeChofer = dialog.findViewById(R.id.VerApellidosChofer);
                EditText CURP = dialog.findViewById(R.id.VerCURP);
                EditText domchofer = dialog.findViewById(R.id.VerDomicilioChofer);
                EditText sangreChofer = dialog.findViewById(R.id.VerTipoSangreChofer);
                EditText telefono = dialog.findViewById(R.id.VerTelefonoChofer);
                EditText fechaNac = dialog.findViewById(R.id.VerFechaNac);
                EditText fechaing = dialog.findViewById(R.id.VerFechaIng);
                EditText fechaLic = dialog.findViewById(R.id.VerFechaLic);

                nomChofer.setText(choferes.getNombreChofer());
                apeChofer.setText(choferes.getApellidosChofer());
                CURP.setText(choferes.getCURP());
                domchofer.setText(choferes.getDomicilio());
                sangreChofer.setText(choferes.getTipoSangre());
                telefono.setText(choferes.getTelefono());
                fechaNac.setText(choferes.getFecha_nac());
                fechaing.setText(choferes.getFecha_Ing());
                fechaLic.setText(choferes.getFecha_vencimiento_Lic());

                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        //------------- ELIMINAR CHOFER ------------------
        holder.btnEliminarChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.NombreChofer.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Los datos eliminados no se pueden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Eliminar de Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(choferes.getFotoChofer()).delete();
                        FirebaseStorage.getInstance().getReferenceFromUrl(choferes.getFotoLic1()).delete();
                        FirebaseStorage.getInstance().getReferenceFromUrl(choferes.getFotoLic2()).delete();
                        //Eliminar de Realtime Database
                        FirebaseDatabase.getInstance().getReference().child("Choferes")
                                .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                        //ACTUALIZAR DATOS PARA ESCRITORIO
                        int ProcesoActualizar = 1;
                        FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                        Toast.makeText(holder.NombreChofer.getContext(), "Chofer borrado", Toast.LENGTH_SHORT).show();
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.NombreChofer.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR CHOFER ------------------
        holder.btnEditarChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.NombreChofer.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_chofer))
                        .setExpanded(true, 1300)
                        .create();
                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText nomChofer = view1.findViewById(R.id.EditNombreChofer);
                EditText apeChofer = view1.findViewById(R.id.EditApellidosChofer);
                EditText CURP = view1.findViewById(R.id.EditCURP);
                EditText domChofer = view1.findViewById(R.id.EditDomicilioChofer);
                EditText telChofer = view1.findViewById(R.id.EditTelefonoChofer);
                EditText sangreChofer = view1.findViewById(R.id.EditTipoSangreChofer);
                //Botones de las fechas
                TextView btnFechaNac = view1.findViewById(R.id.EditbtnFechaNac);
                TextView btnFechaIng = view1.findViewById(R.id.EditbtnFechaIng);
                TextView btnFechaLic = view1.findViewById(R.id.EditbtnFechaLic);
                //fechas
                TextView FechaNac = view1.findViewById(R.id.EdittxtFechaNac);
                TextView FechaIng = view1.findViewById(R.id.EdittxtFechaIng);
                TextView FechaLic = view1.findViewById(R.id.EdittxtFechaLic);

                Button btnActualizarChofer = view1.findViewById(R.id.btnActualizarChofer);

                //Ponemos los datos en los campos de texto
                nomChofer.setText(choferes.getNombreChofer());
                apeChofer.setText(choferes.getApellidosChofer());
                CURP.setText(choferes.getCURP());
                domChofer.setText(choferes.getDomicilio());
                telChofer.setText(choferes.getTelefono());
                sangreChofer.setText(choferes.getTipoSangre());
                //Fechas de nacimiento, ingreso y licencia
                FechaNac.setText(choferes.getFecha_nac());
                FechaIng.setText(choferes.getFecha_Ing());
                FechaLic.setText(choferes.getFecha_vencimiento_Lic());

                //------------- FECHA NACIMIENTO ------------------
                btnFechaNac.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        final int año = calendar.get(Calendar.YEAR);
                        final int mes = calendar.get(Calendar.MONTH);
                        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(holder.NombreChofer.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
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

                        DatePickerDialog dialog = new DatePickerDialog(holder.NombreChofer.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
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

                        DatePickerDialog dialog = new DatePickerDialog(holder.NombreChofer.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
                                FechaLic.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });

                dialogPlus.show();
                //------------- ACTUALIZAR CHOFER ------------------
                btnActualizarChofer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //String de los datos
                        String NomChofer = nomChofer.getText().toString();
                        String ApeChofer = apeChofer.getText().toString();
                        String CURPC = CURP.getText().toString();
                        String TEL = telChofer.getText().toString();
                        String DomChofer = domChofer.getText().toString();
                        String SangreChofer = sangreChofer.getText().toString();
                        String FechaNacimiento = FechaNac.getText().toString();
                        String FechaIngreso = FechaIng.getText().toString();
                        String FechaLicencia = FechaLic.getText().toString();

                        if (NomChofer.isEmpty() || ApeChofer.isEmpty() || CURPC.isEmpty() || DomChofer.isEmpty()
                                || SangreChofer.isEmpty() || TEL.isEmpty() || TEL.length() < 10) {
                            Toast.makeText(holder.NombreChofer.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("fecha_nac", FechaNacimiento);
                            map.put("fecha_Ing", FechaIngreso);
                            map.put("telefono", TEL);
                            map.put("nombreChofer", NomChofer);
                            map.put("apellidosChofer", ApeChofer);
                            map.put("domicilio", DomChofer);
                            map.put("curp", CURPC);
                            map.put("tipoSangre", SangreChofer);
                            map.put("fecha_vencimiento_Lic", FechaLicencia);

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Choferes")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.NombreChofer.getContext(), "Chofer Actualizado", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.NombreChofer.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chofer, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView NombreChofer, ApeChofer, TelChofer;
        Button btnEliminarChofer, btnEditarChofer;
        CardView itemChoferes;
        ImageView FotoChofer;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            NombreChofer = (TextView) itemView.findViewById(R.id.txtMostrarChofer);
            ApeChofer = (TextView) itemView.findViewById(R.id.txtMostrarApeChofer);
            TelChofer = (TextView) itemView.findViewById(R.id.txtMostrarTelChofer);

            FotoChofer = (ImageView) itemView.findViewById(R.id.MostrarFotoChofer);

            btnEditarChofer = (Button) itemView.findViewById(R.id.btnEditarChofer);
            btnEliminarChofer = (Button) itemView.findViewById(R.id.btnEliminarChofer);
            itemChoferes = (CardView) itemView.findViewById(R.id.itemChoferes);
        }
    }

}
