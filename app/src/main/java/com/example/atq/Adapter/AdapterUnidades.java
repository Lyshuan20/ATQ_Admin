package com.example.atq.Adapter;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.atq.Model.Unidades;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class AdapterUnidades extends FirebaseRecyclerAdapter<Unidades, AdapterUnidades.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterUnidades(@NonNull FirebaseRecyclerOptions<Unidades> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Unidades unidades) {
        holder.NumUnidad.setText(unidades.getNumUnidad());
        holder.PlacasUnidad.setText(unidades.getPlacas());
        Glide.with(holder.FotoUnidad.getContext())
                .load(unidades.getFotoUnidad())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.FotoUnidad);
        //------------- VER UNIDAD ------------------
        holder.itemUnidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.NumUnidad.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_unidades);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarUnidad);

                EditText NumUnidad = dialog.findViewById(R.id.VerNumUnidad);
                EditText NumSerie = dialog.findViewById(R.id.VerNumSerie);
                EditText placas = dialog.findViewById(R.id.VerPlacasUnidad);

                NumSerie.setText(unidades.getNumSerie());
                NumUnidad.setText(unidades.getNumUnidad());
                placas.setText(unidades.getPlacas());

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
        //------------- ELIMINAR UNIDAD ------------------
        holder.btnElminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.NumUnidad.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Los datos eliminados no se pueden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Eliminar de Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(unidades.getFotoUnidad()).delete();
                        //Eliminar de Realtime Database
                        FirebaseDatabase.getInstance().getReference().child("Unidades")
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
                        Toast.makeText(holder.NumUnidad.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR UNIDAD ------------------
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.NumUnidad.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_unidades))
                        .setExpanded(true, 1000)
                        .create();
                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText NumeroUnidad = view1.findViewById(R.id.editNumUnidad);
                EditText NumeroSerie = view1.findViewById(R.id.editNumSerie);
                EditText PlacasUnidad = view1.findViewById(R.id.editPlacasUnidad);

                Button btnActualizarUnidad = view1.findViewById(R.id.btnActualizarUnidad);

                //Ponemos los datos en los campos de texto
                NumeroUnidad.setText(unidades.getNumUnidad());
                NumeroSerie.setText(unidades.getNumSerie());
                PlacasUnidad.setText(unidades.getPlacas());

                dialogPlus.show();
                //------------- ACTUALIZAR UNIDAD ------------------
                btnActualizarUnidad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NumeroUnidad.getText().toString().isEmpty() || NumeroSerie.getText().toString().isEmpty()
                                || PlacasUnidad.getText().toString().isEmpty()) {
                            Toast.makeText(holder.NumUnidad.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("numSerie", NumeroSerie.getText().toString());
                            map.put("numUnidad", NumeroUnidad.getText().toString());
                            map.put("placas", PlacasUnidad.getText().toString());

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Unidades")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.NumUnidad.getContext(), "Unidad Actualizada", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.NumUnidad.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_unidades, parent, false);
        return new AdapterUnidades.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView NumUnidad, PlacasUnidad;
        Button btnEditar, btnElminar;
        ImageView FotoUnidad;
        CardView itemUnidades;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            NumUnidad = (TextView) itemView.findViewById(R.id.txtMostrarUnidad);
            PlacasUnidad = (TextView) itemView.findViewById(R.id.txtMostarPlacasUnidad);
            FotoUnidad = (ImageView) itemView.findViewById(R.id.MostrarFotoUnidad);

            btnEditar = (Button) itemView.findViewById(R.id.btnEditarUnidad);
            btnElminar = (Button) itemView.findViewById(R.id.btnEliminarUnidad);
            itemUnidades = (CardView) itemView.findViewById(R.id.itemUnidades);
        }
    }
}
