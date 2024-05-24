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
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Model.Huellas;
import com.example.atq.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class AdapterHuellas extends FirebaseRecyclerAdapter<Huellas, AdapterHuellas.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterHuellas(@NonNull FirebaseRecyclerOptions<Huellas> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Huellas huellas) {
        holder.IdHuella.setText(huellas.getIDHuella());
        holder.NomUsuarioHuella.setText(huellas.getNomUsuario());

        //------------- VER HUELLA ------------------
        holder.btnMostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.IdHuella.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_huella);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarHuella);

                EditText IdHuella = dialog.findViewById(R.id.VerIdHuella);
                EditText TipoEmpleado = dialog.findViewById(R.id.VerTipoEmpleado);
                EditText NomUsuario = dialog.findViewById(R.id.VerNomUsuarioHuella);
                EditText NomCompleUsuario = dialog.findViewById(R.id.VerNombreCompletoHuella);
                EditText ContraHuella = dialog.findViewById(R.id.VerContraseñaHuella);

                IdHuella.setText("ID: " + huellas.getIDHuella());
                TipoEmpleado.setText(huellas.getTipoEmpleado());
                NomUsuario.setText(huellas.getNomUsuario());
                NomCompleUsuario.setText(huellas.getNomCompleto());
                ContraHuella.setText(huellas.getContra());
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
        //------------- ELIMINAR HUELLA ------------------
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.IdHuella.getContext());
                builder.setTitle("¿Estas Seguro?");
                builder.setMessage("Los datos eliminados no se puden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int IdEliminado = Integer.parseInt(holder.IdHuella.getText().toString());
                        int ProcesoEliminar = 2;
                        FirebaseDatabase.getInstance().getReference().child("UltimoIdBorrado").setValue(IdEliminado);
                        FirebaseDatabase.getInstance().getReference().child("EstadoHuella").setValue(ProcesoEliminar);

                        FirebaseDatabase.getInstance().getReference().child("Huellas")
                                .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                        //ACTUALIZAR DATOS PARA ESCRITORIO
                        String ProcesoActualizar = "1";
                        FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                        Toast.makeText(holder.IdHuella.getContext(), "Huella Eliminada", Toast.LENGTH_SHORT).show();
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.IdHuella.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_huellas, parent, false);
        return new AdapterHuellas.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView NomUsuarioHuella, IdHuella;
        //Botones
        Button btnMostar, btnEliminar;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            NomUsuarioHuella = itemView.findViewById(R.id.txtMostrarNomHuella);
            IdHuella = itemView.findViewById(R.id.txtMostarIdHuella);

            btnMostar = itemView.findViewById(R.id.btnMostrarHuella);
            btnEliminar = itemView.findViewById(R.id.btnEliminarHuella);
        }
    }
}
