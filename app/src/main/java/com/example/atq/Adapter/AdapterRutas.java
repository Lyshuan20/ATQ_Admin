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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Model.Rutas;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class AdapterRutas extends FirebaseRecyclerAdapter<Rutas, AdapterRutas.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRutas(@NonNull FirebaseRecyclerOptions<Rutas> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Rutas rutas) {
        holder.NombreRuta.setText(rutas.getNombreRuta());
        holder.FechaRuta.setText(rutas.getFechaIngreso());

        //------------- VER RUTA ------------------
        holder.itemRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.NombreRuta.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_rutas);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarRuta);

                EditText nomRuta = dialog.findViewById(R.id.VerNombreRuta);
                EditText fechaRuta = dialog.findViewById(R.id.VerFechaRuta);

                nomRuta.setText(rutas.getNombreRuta());
                fechaRuta.setText(rutas.getFechaIngreso());

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
        //------------- ELIMINAR RUTA ------------------
        holder.btnEliminarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.NombreRuta.getContext());
                builder.setTitle("¿Estas Seguro?");
                builder.setMessage("Los datos eliminados no se puden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Rutas")
                                .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                        //ACTUALIZAR DATOS PARA ESCRITORIO
                        String ProcesoActualizar = "1";
                        FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                        Toast.makeText(holder.NombreRuta.getContext(), "Ruta Eliminada", Toast.LENGTH_SHORT).show();
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.NombreRuta.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR RUTA ------------------
        holder.btnEditarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.NombreRuta.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_rutas))
                        .setExpanded(true, 660)
                        .create();
                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText NombreRuta = view1.findViewById(R.id.editNombreRuta);

                Button btnActualizarRuta = view1.findViewById(R.id.btnActualizarRuta);
                //Ponemos los datos en los campos de texto
                NombreRuta.setText(rutas.getNombreRuta());

                dialogPlus.show();
                //------------- ACTUALIZAR RUTA ------------------
                btnActualizarRuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NombreRuta.getText().toString().isEmpty()) {
                            Toast.makeText(holder.NombreRuta.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombreRuta", NombreRuta.getText().toString());

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Rutas")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.NombreRuta.getContext(), "Ruta actualizada", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.NombreRuta.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rutas, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        //Datos de la ruta
        TextView NombreRuta, FechaRuta;
        //Botones
        Button btnEliminarRuta, btnEditarRuta;
        CardView itemRutas;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            NombreRuta = itemView.findViewById(R.id.txtMostrarRuta);
            FechaRuta = itemView.findViewById(R.id.txtMostarFechaRuta);

            btnEditarRuta = itemView.findViewById(R.id.btnEditarRuta);
            btnEliminarRuta = itemView.findViewById(R.id.btnEliminarRuta);
            itemRutas = itemView.findViewById(R.id.itemRutas);

        }
    }
}
