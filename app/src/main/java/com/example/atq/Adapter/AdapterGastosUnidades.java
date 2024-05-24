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

import com.example.atq.Model.GastosUnidades;
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

public class AdapterGastosUnidades extends FirebaseRecyclerAdapter<GastosUnidades, AdapterGastosUnidades.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterGastosUnidades(@NonNull FirebaseRecyclerOptions<GastosUnidades> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull GastosUnidades gastos) {
        holder.UnidadGasto.setText("UNIDAD: " + gastos.getNumUnidad());
        holder.FechaGasto.setText(gastos.getFechaGasto());
        //------------- VER GASTO------------------
        holder.itemGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.UnidadGasto.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_gastos);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarGasto);

                EditText UnidadGasto = dialog.findViewById(R.id.VerUnidadGasto);
                EditText FechaGasto = dialog.findViewById(R.id.VerFechaGasto);
                EditText TipoGasto = dialog.findViewById(R.id.VerTipoGasto);
                EditText Monto = dialog.findViewById(R.id.VerMontoGasto);
                EditText Desc = dialog.findViewById(R.id.VerDescGasto);

                String MontoString = String.valueOf(gastos.getMonto());

                UnidadGasto.setText(gastos.getNumUnidad());
                FechaGasto.setText(gastos.getFechaGasto());
                TipoGasto.setText(gastos.getTipoGasto());
                Monto.setText(MontoString);
                Desc.setText(gastos.getDescripción());
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
        //------------- ELIMINAR GASTO------------------
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.UnidadGasto.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Los datos eliminados no se pueden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Gastos")
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
                        Toast.makeText(holder.UnidadGasto.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR GASTO------------------
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.UnidadGasto.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_gasto))
                        .setExpanded(true, 1350)
                        .create();
                //dialogPlus.show();
                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText NumeroUnidad = view1.findViewById(R.id.editNumUnidadGasto);
                EditText TipoDeGasto = view1.findViewById(R.id.editTipoDeGasto);
                EditText Monto = view1.findViewById(R.id.editMontoGasto);
                EditText Descripcion = view1.findViewById(R.id.editDescGasto);
                //Botones de las fechas
                TextView btnFechaGasto = view1.findViewById(R.id.btnEditFechaGasto);
                //fechas
                TextView FechaGasto = view1.findViewById(R.id.txtEditFechaGasto);

                Button btnActualizarGasto = view1.findViewById(R.id.btnActualizarGasto);

                //Ponemos los datos en los campos de texto
                NumeroUnidad.setText(gastos.getNumUnidad());
                TipoDeGasto.setText(gastos.getTipoGasto());
                String MontoString = String.valueOf(gastos.getMonto());
                Monto.setText(MontoString);
                Descripcion.setText(gastos.getDescripción());
                FechaGasto.setText(gastos.getFechaGasto());
                //------------- FECHA VENCIMIENTO POLIZA ------------------
                btnFechaGasto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        final int año = calendar.get(Calendar.YEAR);
                        final int mes = calendar.get(Calendar.MONTH);
                        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(holder.UnidadGasto.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
                                FechaGasto.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });

                dialogPlus.show();
                //------------- ACTUALIZAR UNIDAD ------------------
                btnActualizarGasto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NumeroUnidad.getText().toString().isEmpty() || TipoDeGasto.getText().toString().isEmpty()
                                || Monto.getText().toString().isEmpty()
                                || Descripcion.getText().toString().isEmpty()) {
                            Toast.makeText(holder.UnidadGasto.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            int MontoInt = Integer.valueOf(Monto.getText().toString());
                            Map<String, Object> map = new HashMap<>();
                            map.put("descripción", Descripcion.getText().toString());
                            map.put("monto", MontoInt);
                            map.put("fechaGasto", FechaGasto.getText().toString());

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Gastos")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.UnidadGasto.getContext(), "Gasto Actualizado", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.UnidadGasto.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gastos, parent, false);
        return new AdapterGastosUnidades.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView UnidadGasto, FechaGasto;

        Button btnEliminar, btnEditar;
        CardView itemGasto;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            UnidadGasto = (TextView) itemView.findViewById(R.id.txtMostrarGasto);
            FechaGasto = (TextView) itemView.findViewById(R.id.txtMostarFechaGasto);

            btnEditar = (Button) itemView.findViewById(R.id.btnEditarGasto);
            btnEliminar = (Button) itemView.findViewById(R.id.btnEliminarGasto);
            itemGasto = (CardView) itemView.findViewById(R.id.itemGastos);
        }
    }

}
