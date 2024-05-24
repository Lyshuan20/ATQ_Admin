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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Model.PolizasUnidades;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdapterPolizasUnidades extends FirebaseRecyclerAdapter<PolizasUnidades, AdapterPolizasUnidades.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterPolizasUnidades(@NonNull FirebaseRecyclerOptions<PolizasUnidades> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PolizasUnidades polizas) {
        holder.NumUnidad.setText("UNIDAD: " + polizas.getNumUnidad());
        holder.NumPoliza.setText(polizas.getNumeroPoliza());
        //------------- VER POLIZA------------------
        holder.itemPoliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.NumUnidad.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_polizas);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarPoliza);

                EditText NumUnidad = dialog.findViewById(R.id.VerUnidadPoliza);
                EditText PlacasUni = dialog.findViewById(R.id.VerPlacasUnidadPoliza);
                EditText Empresa = dialog.findViewById(R.id.VerEmpresaPoliza);
                EditText NumPoliza = dialog.findViewById(R.id.VerNumPoliza);
                EditText FechaVencimiento = dialog.findViewById(R.id.VerFechaPoliza);

                NumUnidad.setText(polizas.getNumUnidad());
                PlacasUni.setText(polizas.getPlacas());
                Empresa.setText(polizas.getEmpresaPoliza());
                NumPoliza.setText(polizas.getNumeroPoliza());
                FechaVencimiento.setText(polizas.getFechaVencimiento());

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
        //------------- ELIMINAR POLIZA------------------
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.NumPoliza.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Los datos eliminados no se pueden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Polizas")
                                .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                        //ACTUALIZAR DATOS PARA ESCRITORIO
                        String ProcesoActualizar = "1";
                        FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.NumPoliza.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR POLIZA------------------
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.NumPoliza.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_poliza))
                        .setExpanded(true, 1250)
                        .create();
                //dialogPlus.show();
                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText NumeroUnidad = view1.findViewById(R.id.editNumUnidadPoliza);
                EditText PlacasUnidad = view1.findViewById(R.id.editPlacasUnidadPoliza);
                EditText Empresa = view1.findViewById(R.id.editEmpresaPoliza);
                EditText NumeroPoliza = view1.findViewById(R.id.editNumPoliza);
                //Botones de las fechas
                TextView btnFechaPoliza = view1.findViewById(R.id.btnEditFechaPoliza);
                //fechas
                TextView FechaPoliza = view1.findViewById(R.id.txtEditFechaPoliza);

                Button btnActualizarPoliza = view1.findViewById(R.id.btnActualizarPoliza);

                //Ponemos los datos en los campos de texto
                NumeroUnidad.setText(polizas.getNumUnidad());
                PlacasUnidad.setText(polizas.getPlacas());
                Empresa.setText(polizas.getEmpresaPoliza());
                NumeroPoliza.setText(polizas.getNumeroPoliza());
                FechaPoliza.setText(polizas.getFechaVencimiento());
                //------------- FECHA VENCIMIENTO POLIZA ------------------
                btnFechaPoliza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        final int año = calendar.get(Calendar.YEAR);
                        final int mes = calendar.get(Calendar.MONTH);
                        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(holder.NumPoliza.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
                                FechaPoliza.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });

                dialogPlus.show();
                //------------- ACTUALIZAR UNIDAD ------------------
                btnActualizarPoliza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NumeroUnidad.getText().toString().isEmpty() || Empresa.getText().toString().isEmpty()
                                || PlacasUnidad.getText().toString().isEmpty()
                                || NumeroPoliza.getText().toString().isEmpty()) {
                            Toast.makeText(holder.NumPoliza.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("empresaPoliza", Empresa.getText().toString());
                            map.put("numeroPoliza", NumeroPoliza.getText().toString());
                            map.put("fechaVencimiento", FechaPoliza.getText().toString());

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Polizas")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.NumPoliza.getContext(), "Poliza Actualizada", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.NumPoliza.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_polizas, parent, false);
        return new AdapterPolizasUnidades.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView NumPoliza, NumUnidad;

        Button btnEditar, btnEliminar;
        CardView itemPoliza;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            NumPoliza = (TextView) itemView.findViewById(R.id.txtMostrarPoliza);
            NumUnidad = (TextView) itemView.findViewById(R.id.txtMostarUnidadPoliza);

            btnEditar = (Button) itemView.findViewById(R.id.btnEditarPoliza);
            btnEliminar = (Button) itemView.findViewById(R.id.btnEliminarPoliza);
            itemPoliza = (CardView) itemView.findViewById(R.id.itemPolizas);
        }
    }
}
