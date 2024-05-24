package com.example.atq;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText EdtUsuario, EdtContra;
    Button btnInicio;
    //-------------------------------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EdtUsuario = findViewById(R.id.EdtUsuario);
        EdtContra = findViewById(R.id.EdtContra);
        btnInicio = findViewById(R.id.btnInicio);
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //------------- INICIAR SESION ------------------
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Variables de USUARIO y CONTRASEÑA
                String usuario = EdtUsuario.getText().toString();
                String contraseña = EdtContra.getText().toString();
                //Checar que los campos no esten vacios
                if (usuario.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Favor de ingresar sus datos", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Checar si existe el usuario
                            if (snapshot.hasChild(usuario)) {
                                String GetPass = snapshot.child(usuario).child("contra").getValue().toString();
                                //Checamos si la contraseña es correcta
                                if (GetPass.equals(contraseña)) {
                                    Toast.makeText(MainActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                    //Verificamos que tipo de usuario es
                                    String GetTipoEmpleado = snapshot.child(usuario).child("tipoUsuario").getValue().toString();
                                    //si es un USUARIO NORMAL
                                    if (GetTipoEmpleado.equals("NORMAL")) {
                                        startActivity(new Intent(MainActivity.this, Dashboard_Normal.class));
                                        finish();
                                    }
                                    //si es un USUARIO ADMINISTRADOR
                                    else {
                                        startActivity(new Intent(MainActivity.this, Dashboard_Admin.class));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "ERROR BD ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}