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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atq.Model.Usuarios;
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

public class AdapterUsuarios extends FirebaseRecyclerAdapter<Usuarios, AdapterUsuarios.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterUsuarios(@NonNull FirebaseRecyclerOptions<Usuarios> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Usuarios usuarios) {
        holder.nomUsuario.setText(usuarios.getNombreUsuario());
        holder.tipoUsuario.setText(usuarios.getTipoUsuario());
        //------------- LEER USUARIO ------------------
        holder.itemUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(holder.nomUsuario.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ver_usuario);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCerrarUsuario);

                EditText nomUsuario = dialog.findViewById(R.id.VerNombreUsuario);
                EditText nomCom = dialog.findViewById(R.id.VerNombreCompleto);
                EditText correo = dialog.findViewById(R.id.VerCorreoUsuario);
                EditText contraUsuario = dialog.findViewById(R.id.VerContraseñaUsuario);
                EditText tipoUsuario = dialog.findViewById(R.id.VerTipoUsuario);

                nomUsuario.setText(usuarios.getNombreUsuario());
                nomCom.setText(usuarios.getNombreCompleto());
                correo.setText(usuarios.getEmail());
                contraUsuario.setText(usuarios.getContra());
                tipoUsuario.setText(usuarios.getTipoUsuario());

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
        //------------- ELIMINAR USUARIO ------------------
        holder.EliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.nomUsuario.getContext());
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Los datos eliminados no se pueden recuperar");
                //------------------------------------------------------------
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (holder.nomUsuario.getText().toString().equals("C")) {
                            Toast.makeText(holder.nomUsuario.getContext(), "Este usuario no puede ser eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            //BORRAR USUARIO DE BD
                            FirebaseDatabase.getInstance().getReference().child("Usuarios")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).removeValue();
                            //ACTUALIZAR DATOS PARA ESCRITORIO
                            String ProcesoActualizar = "1";
                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                            Toast.makeText(holder.nomUsuario.getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //------------------------------------------------------------
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.nomUsuario.getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        //------------- EDITAR USUARIO ------------------
        holder.EditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.nomUsuario.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_usuarios))
                        .setExpanded(true, 1200)
                        .create();
                //dialogPlus.show();

                View view1 = dialogPlus.getHolderView();
                //Declaramos la(s) variable(s) de los datos
                EditText NombreUsuario = view1.findViewById(R.id.editNombreUsuario);
                EditText NombreCompleto = view1.findViewById(R.id.editNombreCompleto);
                EditText CorreoUsuario = view1.findViewById(R.id.editCorreoUsuario);
                EditText ContraUsuario = view1.findViewById(R.id.editContraseñaUsuario);
                RadioButton RBAdmin = view1.findViewById(R.id.editRBAdmin);
                RadioButton RBNormal = view1.findViewById(R.id.editRBNormal);

                Button btnActualizarUsuario = view1.findViewById(R.id.btnActualizarUsuario);

                //Ponemos los datos en los campos de texto
                NombreUsuario.setText(usuarios.getNombreUsuario());
                NombreCompleto.setText(usuarios.getNombreCompleto());
                CorreoUsuario.setText(usuarios.getEmail());
                ContraUsuario.setText(usuarios.getContra());

                //Seleccionamos uno de los RadioButton segun el tipo de usuario
                if (usuarios.getTipoUsuario().equals("ADMIN")) {
                    RBAdmin.setChecked(true);
                } else {
                    RBNormal.setChecked(true);
                }
                dialogPlus.show();
                //------------- ACTUALIZAR USUARIO ------------------
                btnActualizarUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NombreUsuario.getText().toString().isEmpty() || NombreCompleto.getText().toString().isEmpty()
                                || ContraUsuario.getText().toString().isEmpty() || CorreoUsuario.getText().toString().isEmpty()) {
                            Toast.makeText(holder.nomUsuario.getContext(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombreUsuario", NombreUsuario.getText().toString());
                            map.put("nombreCompleto", NombreCompleto.getText().toString());
                            map.put("email", CorreoUsuario.getText().toString());
                            map.put("contra", ContraUsuario.getText().toString());

                            //PONER EL TIPO DE USUARIO
                            String TipoUsuarioFinal;
                            //Le ponemos el tipo de usuario
                            if (RBAdmin.isChecked()) {
                                TipoUsuarioFinal = "ADMIN";
                            } else {
                                TipoUsuarioFinal = "NORMAL";
                            }
                            map.put("tipoUsuario", TipoUsuarioFinal);

                            //Actualizar en firebase
                            FirebaseDatabase.getInstance().getReference().child("Usuarios")
                                    .child(getRef(holder.getAdapterPosition()).getKey()).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //ACTUALIZAR DATOS PARA ESCRITORIO
                                            String ProcesoActualizar = "1";
                                            FirebaseDatabase.getInstance().getReference().child("DatoActualizado").setValue(ProcesoActualizar);
                                            Toast.makeText(holder.nomUsuario.getContext(), "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.nomUsuario.getContext(), "Actualización erronea", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_usuarios, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        //Datos del usuario
        TextView nomUsuario, tipoUsuario;
        //Botones
        Button EliminarUsuario, EditarUsuario;
        CardView itemUsuarios;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            nomUsuario = itemView.findViewById(R.id.txtMostrarUsuario);
            tipoUsuario = itemView.findViewById(R.id.txtMostarTipoUsuario);

            EliminarUsuario = itemView.findViewById(R.id.btnEliminarUsuario);
            EditarUsuario = itemView.findViewById(R.id.btnEditarUsuario);

            itemUsuarios = itemView.findViewById(R.id.itemUsuarios);
        }
    }
}
