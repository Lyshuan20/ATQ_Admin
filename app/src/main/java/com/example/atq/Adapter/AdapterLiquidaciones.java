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

import com.example.atq.Model.Liquidaciones;
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

public class AdapterLiquidaciones extends FirebaseRecyclerAdapter<Liquidaciones, AdapterLiquidaciones.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterLiquidaciones(@NonNull FirebaseRecyclerOptions<Liquidaciones> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Liquidaciones liqui) {
        holder.FechaLiquid.setText(liqui.getFechaLiquidacion());
        holder.UnidadLiquid.setText(liqui.getNumUnidad());

        //------------- VER LIQUIDACION ------------------
        holder.itemLiquidacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.UnidadLiquid.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_liquidaciones);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarLiquidaciones);

                EditText NumUnidad = dialog.findViewById(R.id.VerNumUnidadLiqui);
                EditText Operador = dialog.findViewById(R.id.VerOperadorLiqui);
                EditText Ruta = dialog.findViewById(R.id.VerRutaLiquidacion);
                EditText FechaTrabajo = dialog.findViewById(R.id.VerFechaLiquidacion);
                EditText MontoL = dialog.findViewById(R.id.VerCantLiquidacion);
                EditText NumVueltas = dialog.findViewById(R.id.VerNumVueltas);
                EditText Combustible = dialog.findViewById(R.id.VerCombustible);
                EditText Bol1 = dialog.findViewById(R.id.VerNumBol1);
                EditText Bol2 = dialog.findViewById(R.id.VerNumBol2);
                EditText Bol3 = dialog.findViewById(R.id.VerNumBol3);


                NumUnidad.setText(liqui.getNumUnidad());
                Operador.setText(liqui.getNombreOperador());
                Ruta.setText(liqui.getRutaLiquidacion());
                FechaTrabajo.setText(liqui.getFechaLiquidacion());
                //DE INT A STRING
                String MontoI = String.valueOf(liqui.getCantidadLiquidacion());
                String NumVueltasI = String.valueOf(liqui.getNumeroVueltas());
                String CombustibleI = String.valueOf(liqui.getCantidadCombustible());
                String Bol1I = String.valueOf(liqui.getNumeroBoletera1());
                String Bol2I = String.valueOf(liqui.getNumeroBoletera2());
                String Bol3I = String.valueOf(liqui.getNumeroBoletera3());

                MontoL.setText(MontoI);
                NumVueltas.setText(NumVueltasI);
                Combustible.setText(CombustibleI);
                Bol1.setText(Bol1I);
                Bol2.setText(Bol2I);
                Bol3.setText(Bol3I);
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

        //------------- ELIMINAR LIQUIDACIÓN ------------------
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.UnidadLiquid.getContext());
                builder.setTitle("¿Estas Seguro?");
                builder.setMessage("Los datos eliminados no se puden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Liquidaciones")
                                .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                        //ACTUALIZAR DATOS PARA ESCRITORIO
                        String ProcesoActualizar = "1";
                        FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                        Toast.makeText(holder.UnidadLiquid.getContext(), "Liquidacion Eliminada", Toast.LENGTH_SHORT).show();
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.UnidadLiquid.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR LIQUIDACION ------------------
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.UnidadLiquid.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_liquidaciones))
                        .setExpanded(true, 1840)
                        .create();
                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText MontoL = view1.findViewById(R.id.EditCantLiquidacion);
                EditText NumVueltas = view1.findViewById(R.id.EditNumVueltas);
                EditText Combustible = view1.findViewById(R.id.EditCombustible);
                EditText Bol1 = view1.findViewById(R.id.EditBoletera1);
                EditText Bol2 = view1.findViewById(R.id.EditBoletera2);
                EditText Bol3 = view1.findViewById(R.id.EditBoletera3);

                //Botones de las fechas
                TextView btnFechaTrabajo = view1.findViewById(R.id.EditbtnFechaLiqui);
                //fechas
                TextView FechaTrabajo = view1.findViewById(R.id.EdittxtFechaLiqui);

                Button btnActualizarLiquidacion = view1.findViewById(R.id.btnActualizarLiquidacion);
                //Ponemos los datos en los campos de texto
                //---- INT A STRING ----
                String MontoLS = String.valueOf(liqui.getCantidadLiquidacion());
                String NumVueltasS = String.valueOf(liqui.getNumeroVueltas());
                String CombustibleS = String.valueOf(liqui.getCantidadCombustible());
                String Bol1S = String.valueOf(liqui.getNumeroBoletera1());
                String Bol2S = String.valueOf(liqui.getNumeroBoletera2());
                String Bol3S = String.valueOf(liqui.getNumeroBoletera3());
                MontoL.setText(MontoLS);
                NumVueltas.setText(NumVueltasS);
                Combustible.setText(CombustibleS);
                Bol1.setText(Bol1S);
                Bol2.setText(Bol2S);
                Bol3.setText(Bol3S);
                FechaTrabajo.setText(liqui.getFechaLiquidacion());

                //------------- FECHA DE LIQUIDACION ------------------
                btnFechaTrabajo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        final int año = calendar.get(Calendar.YEAR);
                        final int mes = calendar.get(Calendar.MONTH);
                        final int dia = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(holder.UnidadLiquid.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                                mes = mes + 1;
                                String date = año + "-" + mes + "-" + dia;
                                FechaTrabajo.setText(date);
                            }
                        }, año, mes, dia);
                        dialog.show();
                    }
                });

                dialogPlus.show();
                //------------- ACTUALIZAR RUTA ------------------
                btnActualizarLiquidacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MontoL.getText().toString().isEmpty() || NumVueltas.getText().toString().isEmpty() ||
                                Combustible.getText().toString().isEmpty() || Bol1.getText().toString().isEmpty()
                                || Bol2.getText().toString().isEmpty() || Bol3.getText().toString().isEmpty()) {
                            Toast.makeText(holder.UnidadLiquid.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            //De String a INT
                            int MontoI = Integer.valueOf(MontoL.getText().toString());
                            int CombustibleI = Integer.valueOf(Combustible.getText().toString());
                            int Bol1I = Integer.valueOf(Bol1.getText().toString());
                            int Bol2I = Integer.valueOf(Bol2.getText().toString());
                            int Bol3I = Integer.valueOf(Bol3.getText().toString());
                            int NumVueltasI = Integer.valueOf(NumVueltasS);
                            Map<String, Object> map = new HashMap<>();
                            map.put("cantidadCombustible", CombustibleI);
                            map.put("cantidadLiquidacion", MontoI);
                            map.put("fechaLiquidacion", FechaTrabajo.getText().toString());
                            map.put("numeroBoletera1", Bol1I);
                            map.put("numeroBoletera2", Bol2I);
                            map.put("numeroBoletera3", Bol3I);
                            map.put("numeroVueltas", NumVueltasI);


                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Liquidaciones")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.UnidadLiquid.getContext(), "Liquidacion actualizada", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.UnidadLiquid.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_liquidaciones, parent, false);
        return new AdapterLiquidaciones.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView UnidadLiquid, FechaLiquid;
        Button btnEliminar, btnEditar;
        CardView itemLiquidacion;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            UnidadLiquid = itemView.findViewById(R.id.txtMostrarLiquidacion);
            FechaLiquid = itemView.findViewById(R.id.txtMostarFechaLiquidacion);

            btnEliminar = itemView.findViewById(R.id.btnEliminarLiquidacion);
            btnEditar = itemView.findViewById(R.id.btnEditarLiquidacion);

            itemLiquidacion = (CardView) itemView.findViewById(R.id.itemLiquidaciones);
            ;

        }
    }

}
