package com.example.atq.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.atq.Model.Choferes;
import com.example.atq.Model.GastosUnidades;
import com.example.atq.Model.Liquidaciones;
import com.example.atq.Model.Unidades;
import com.example.atq.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentReportes extends Fragment {

    CardView btnReporteDiario, btnReporteMensual, btnReporteUnidad, btnGastoUnidad,
            btnGastoGeneral, btnReporteOperador;
    //------------- FIREBASE ------------------
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //------------- EXCEL REPORTES ------------------
    String FechaReporteD, UnidadSeleccionadaR, UnidadSeleccionadaG, OperadorSeleccionado;
    String FechaHoy;
    //Noti push
    NotificationManagerCompat notificationManager;
    Notification notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reportes, container, false);

        btnReporteDiario = root.findViewById(R.id.btnReporteDiario);
        btnReporteMensual = root.findViewById(R.id.btnReporteMensual);
        btnReporteUnidad = root.findViewById(R.id.btnReporteUnidad);
        btnGastoUnidad = root.findViewById(R.id.btnGastoUnidad);
        btnGastoGeneral = root.findViewById(R.id.btnGastoGeneral);
        btnReporteOperador = root.findViewById(R.id.btnReporteOperador);
        //------------- FIREBASE ------------------
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //------------- BOTON REPORTE DIARIO -----------------
        btnReporteDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_reporte_diario);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarReporteDiario);
                Button btnCrear = dialog.findViewById(R.id.btnCrearReporteDiario);

                //Boton de la fecha del reporte diario
                TextView btnFechaReporteDiario = dialog.findViewById(R.id.btnFechaReporteDiario);
                //TextView De la fecha
                TextView txtFechaReporteDiario = dialog.findViewById(R.id.txtFechaReporteDiario);

                //------------- FECHA REPORTE ------------------
                btnFechaReporteDiario.setOnClickListener(new View.OnClickListener() {
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
                                FechaReporteD = date;
                                txtFechaReporteDiario.setText(date);
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
                //------------- BOTON CREAR REPORTE DIARIO DIALOG ------------------
                btnCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (FechaReporteD == null) {
                            Toast.makeText(getActivity(), "ERROR. Campo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            final List<Liquidaciones> ListLiquidaciones = new ArrayList<>();
                            databaseReference.child("Liquidaciones").orderByChild("fechaLiquidacion")
                                    .equalTo(FechaReporteD).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot item : snapshot.getChildren()) {
                                                    String Fecha = item.child("fechaLiquidacion").getValue().toString();
                                                    String Unidad = item.child("numUnidad").getValue().toString();
                                                    String Ruta = item.child("rutaLiquidacion").getValue().toString();
                                                    String Operador = item.child("nombreOperador").getValue().toString();
                                                    int Vueltas = Integer.parseInt(item.child("numeroVueltas").getValue().toString());
                                                    int Liquidacion = Integer.parseInt(item.child("cantidadLiquidacion").getValue().toString());
                                                    ListLiquidaciones.add(new Liquidaciones(Fecha, Liquidacion, Operador, Unidad, Ruta, Vueltas));
                                                }
                                                String NombreReporte = "REPORTE DIARIO " + FechaReporteD;
                                                GuardarExcelRD(ListLiquidaciones, NombreReporte, "REPORTE DIARIO");
                                                dialog.dismiss();
                                                //------------------------------------------------------------
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                                                            NotificationManager.IMPORTANCE_DEFAULT);

                                                    NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                                    manager.createNotificationChannel(channel);
                                                }
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                                                        .setSmallIcon(android.R.drawable.stat_notify_chat)
                                                        .setContentTitle("Excel descargado")
                                                        .setContentText("Haga clic aqui para ir a la dirección del archivo");

                                                Intent intent = new Intent().setClassName("com.google.android.documentsui", "com.android.documentsui.ViewDownloadsActivity");

                                                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
                                                builder.setContentIntent(pendingIntent);
                                                builder.setAutoCancel(true);

                                                notification = builder.build();
                                                notificationManager = NotificationManagerCompat.from(getActivity());
                                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    return;
                                                }
                                                notificationManager.notify(1, notification);
                                            } else {
                                                Toast.makeText(getActivity(), "ERROR. NO SE ENCUENTRAN LIQUIDACIONES", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }
                });
                dialog.show();

            }
        });
        //------------- BOTON REPORTE MENSUAL -----------------
        btnReporteMensual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FechaHoy = getDate();
            }
        });
        //------------- BOTON REPORTE UNIDAD -----------------
        btnReporteUnidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_reporte_unidad);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarReporteUnidad);
                Button btnCrear = dialog.findViewById(R.id.btnCrearReporteUnidad);

                //MOSTRAR SPINNER DE LAS UNIDADES
                Spinner unidadReporte = dialog.findViewById(R.id.SpnUnidadRU);

                final List<Unidades> ListUnidades = new ArrayList<>();

                //METODO DROPDOWN UNIDADES
                databaseReference.child("Unidades").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String numUnidad = item.child("numUnidad").getValue().toString();
                                String placasUnidad = item.child("placas").getValue().toString();
                                ListUnidades.add(new Unidades(numUnidad, placasUnidad));
                            }
                            ArrayAdapter<Unidades> AdapterUnidades = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListUnidades);
                            unidadReporte.setAdapter(AdapterUnidades);
                            //PONER EL VALOR DE LA UNIDAD SEGUN LA UNIDAD SELECCIONADA
                            unidadReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    UnidadSeleccionadaR = ListUnidades.get(i).getNumUnidad();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY UNIDADES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //------------- BOTON CREAR REPORTE UNIDAD DIALOG ------------------
                btnCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final List<Liquidaciones> ListLiquidaciones = new ArrayList<>();
                        databaseReference.child("Liquidaciones").orderByChild("numUnidad")
                                .equalTo(UnidadSeleccionadaR).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot item : snapshot.getChildren()) {
                                                String Fecha = item.child("fechaLiquidacion").getValue().toString();
                                                String Unidad = item.child("numUnidad").getValue().toString();
                                                String Ruta = item.child("rutaLiquidacion").getValue().toString();
                                                String Operador = item.child("nombreOperador").getValue().toString();
                                                int Vueltas = Integer.parseInt(item.child("numeroVueltas").getValue().toString());
                                                int Liquidacion = Integer.parseInt(item.child("cantidadLiquidacion").getValue().toString());
                                                ListLiquidaciones.add(new Liquidaciones(Fecha, Liquidacion, Operador, Unidad, Ruta, Vueltas));
                                            }
                                            String NombreReporte = "REPORTE UNIDAD " + UnidadSeleccionadaR;
                                            GuardarExcelRD(ListLiquidaciones, NombreReporte, "REPORTE UNIDAD");
                                            dialog.dismiss();
                                            //------------------------------------------------------------
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                                                        NotificationManager.IMPORTANCE_DEFAULT);

                                                NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                                manager.createNotificationChannel(channel);
                                            }
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                                    .setContentTitle("Excel descargado")
                                                    .setContentText("Haga clic aqui para ir a la dirección del archivo");

                                            Intent intent = new Intent().setClassName("com.google.android.documentsui", "com.android.documentsui.ViewDownloadsActivity");

                                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
                                            builder.setContentIntent(pendingIntent);
                                            builder.setAutoCancel(true);

                                            notification = builder.build();
                                            notificationManager = NotificationManagerCompat.from(getActivity());
                                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            notificationManager.notify(1, notification);
                                        } else {
                                            Toast.makeText(getActivity(), "ERROR. NO SE ENCUENTRAN LIQUIDACIONES", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                dialog.show();
            }
        });
        //------------- BOTON GASTO UNIDAD -----------------
        btnGastoUnidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_reporte_gasto_unidad);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarReporteGastoU);
                Button btnCrear = dialog.findViewById(R.id.btnCrearReporteGastoU);

                //MOSTRAR SPINNER DE LAS UNIDADES
                Spinner unidadReporte = dialog.findViewById(R.id.SpnUnidadRGU);

                final List<Unidades> ListUnidades = new ArrayList<>();

                //METODO DROPDOWN UNIDADES
                databaseReference.child("Unidades").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String numUnidad = item.child("numUnidad").getValue().toString();
                                String placasUnidad = item.child("placas").getValue().toString();
                                ListUnidades.add(new Unidades(numUnidad, placasUnidad));
                            }
                            ArrayAdapter<Unidades> AdapterUnidades = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListUnidades);
                            unidadReporte.setAdapter(AdapterUnidades);
                            //PONER EL VALOR DE LA UNIDAD SEGUN LA UNIDAD SELECCIONADA
                            unidadReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    UnidadSeleccionadaG = ListUnidades.get(i).getNumUnidad();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY UNIDADES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //------------- BOTON CREAR REPORTE UNIDAD DIALOG ------------------
                btnCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final List<GastosUnidades> ListGastos = new ArrayList<>();
                        databaseReference.child("Gastos").orderByChild("numUnidad")
                                .equalTo(UnidadSeleccionadaG).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot item : snapshot.getChildren()) {
                                                String Fecha = item.child("fechaGasto").getValue().toString();
                                                String Unidad = item.child("numUnidad").getValue().toString();
                                                String TipoGasto = item.child("tipoGasto").getValue().toString();
                                                String Descripcion = item.child("descripción").getValue().toString();
                                                int Monto = Integer.parseInt(item.child("monto").getValue().toString());
                                                ListGastos.add(new GastosUnidades(Unidad, Fecha, TipoGasto, Monto, Descripcion));
                                            }
                                            String NombreReporte = "REPORTE GASTO UNIDAD " + UnidadSeleccionadaG;
                                            GuardarExcelRG(ListGastos, NombreReporte, "REPORTE GASTO UNIDAD");
                                            dialog.dismiss();
                                            //------------------------------------------------------------
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                                                        NotificationManager.IMPORTANCE_DEFAULT);

                                                NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                                manager.createNotificationChannel(channel);
                                            }
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                                    .setContentTitle("Excel descargado")
                                                    .setContentText("Haga clic aqui para ir a la dirección del archivo");

                                            Intent intent = new Intent().setClassName("com.google.android.documentsui", "com.android.documentsui.ViewDownloadsActivity");

                                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
                                            builder.setContentIntent(pendingIntent);
                                            builder.setAutoCancel(true);

                                            notification = builder.build();
                                            notificationManager = NotificationManagerCompat.from(getActivity());
                                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            notificationManager.notify(1, notification);
                                        } else {
                                            Toast.makeText(getActivity(), "ERROR. NO SE ENCUENTRAN LIQUIDACIONES", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                dialog.show();
            }
        });
        //------------- BOTON GASTO GENERAL -----------------
        btnGastoGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<GastosUnidades> ListGastos = new ArrayList<>();
                databaseReference.child("Gastos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String Fecha = item.child("fechaGasto").getValue().toString();
                                String Unidad = item.child("numUnidad").getValue().toString();
                                String TipoGasto = item.child("tipoGasto").getValue().toString();
                                String Descripcion = item.child("descripción").getValue().toString();
                                int Monto = Integer.parseInt(item.child("monto").getValue().toString());
                                ListGastos.add(new GastosUnidades(Unidad, Fecha, TipoGasto, Monto, Descripcion));
                            }
                            String NombreReporte = "REPORTE GASTO GENERAL";
                            GuardarExcelRG(ListGastos, NombreReporte, "REPORTE GASTO GENERAL");
                            //------------------------------------------------------------
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                                        NotificationManager.IMPORTANCE_DEFAULT);

                                NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                manager.createNotificationChannel(channel);
                            }
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                    .setContentTitle("Excel descargado")
                                    .setContentText("Haga clic aqui para ir a la dirección del archivo");

                            Intent intent = new Intent().setClassName("com.google.android.documentsui", "com.android.documentsui.ViewDownloadsActivity");

                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
                            builder.setContentIntent(pendingIntent);
                            builder.setAutoCancel(true);

                            notification = builder.build();
                            notificationManager = NotificationManagerCompat.from(getActivity());
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            notificationManager.notify(1, notification);
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO SE ENCUENTRAN LIQUIDACIONES", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //------------- BOTON REPORTE OPERADOR -----------------
        btnReporteOperador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------------- DIALOG ------------------
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.agregar_reporte_operador);

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
                Button btnCerrar = dialog.findViewById(R.id.btnCancelarReporteOperador);
                Button btnCrear = dialog.findViewById(R.id.btnCrearReporteOperador);

                Spinner SpinnerOperadores = dialog.findViewById(R.id.SpnUnidadRO);

                final List<Choferes> ListChoferes = new ArrayList<>();
                //METODO DROPDOWN OPERADORES
                databaseReference.child("Choferes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                String nomChofer = item.child("nombreChofer").getValue().toString();
                                String apeChofer = item.child("apellidosChofer").getValue().toString();
                                ListChoferes.add(new Choferes(nomChofer, apeChofer));
                            }
                            ArrayAdapter<Choferes> AdapterChoferes = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ListChoferes);
                            SpinnerOperadores.setAdapter(AdapterChoferes);
                            //PONER EL VALOR DEL OPERADOR SEGUN EL OPERADOR SELECCIONADO
                            SpinnerOperadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    String NombreCompleto = ListChoferes.get(i).getNombreChofer() + " " + ListChoferes.get(i).getApellidosChofer();
                                    OperadorSeleccionado = NombreCompleto;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "ERROR. NO HAY CHOFERES EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //------------- CERRAR DIALOG ------------------
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //------------- BOTON CREAR REPORTE UNIDAD DIALOG ------------------
                btnCrear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final List<Liquidaciones> ListLiquidaciones = new ArrayList<>();
                        databaseReference.child("Liquidaciones").orderByChild("nombreOperador")
                                .equalTo(OperadorSeleccionado).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot item : snapshot.getChildren()) {
                                                String Fecha = item.child("fechaLiquidacion").getValue().toString();
                                                String Unidad = item.child("numUnidad").getValue().toString();
                                                String Ruta = item.child("rutaLiquidacion").getValue().toString();
                                                String Operador = item.child("nombreOperador").getValue().toString();
                                                int Vueltas = Integer.parseInt(item.child("numeroVueltas").getValue().toString());
                                                int Liquidacion = Integer.parseInt(item.child("cantidadLiquidacion").getValue().toString());
                                                ListLiquidaciones.add(new Liquidaciones(Fecha, Liquidacion, Operador, Unidad, Ruta, Vueltas));
                                            }
                                            String NombreReporte = "REPORTE OPERADOR " + OperadorSeleccionado;
                                            GuardarExcelRD(ListLiquidaciones, NombreReporte, "REPORTE OPERADOR");
                                            dialog.dismiss();
                                            //------------------------------------------------------------
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel channel = new NotificationChannel("myCh", "My Channel",
                                                        NotificationManager.IMPORTANCE_DEFAULT);

                                                NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                                manager.createNotificationChannel(channel);
                                            }
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "myCh")
                                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                                    .setContentTitle("Excel descargado")
                                                    .setContentText("Haga clic aqui para ir a la dirección del archivo");

                                            Intent intent = new Intent().setClassName("com.google.android.documentsui", "com.android.documentsui.ViewDownloadsActivity");

                                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
                                            builder.setContentIntent(pendingIntent);
                                            builder.setAutoCancel(true);

                                            notification = builder.build();
                                            notificationManager = NotificationManagerCompat.from(getActivity());
                                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                return;
                                            }
                                            notificationManager.notify(1, notification);
                                        } else {
                                            Toast.makeText(getActivity(), "ERROR. NO SE ENCUENTRAN LIQUIDACIONES", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                dialog.show();
            }
        });
        return root;
    }

    //------------- DESCARGAR EXCEL DEL REPORTES GANANCIAS ------------------
    public void GuardarExcelRD(List<Liquidaciones> dataList, String NombreReporteDiario, String TipoReporte) {
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        //ESTILO DE CELDA (ENCABEZADOS)
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        //ESTILO DE CELDA (DATOS)
        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = null;
        sheet = workbook.createSheet(TipoReporte);

        Row row = null;
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 5300);
        sheet.setColumnWidth(3, 5100);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);

        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("FECHA");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("UNIDAD");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("RUTA");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("OPERADOR");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("VUELTAS");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("LIQUIDACION");
        cell.setCellStyle(cellStyle);

        //DATOS
        for (int i = 0; i < dataList.size(); i++) {
            Row rowData = sheet.createRow(i + 5);

            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getFechaLiquidacion());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getNumUnidad());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getRutaLiquidacion());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getNombreOperador());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getNumeroVueltas());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getCantidadLiquidacion());
            cell.setCellStyle(cellStyle2);
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, NombreReporteDiario + ".xls");
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            Toast.makeText(getActivity(), "Reporte descargado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    //------------- DESCARGAR EXCEL DEL REPORTES GASTOS ------------------
    public void GuardarExcelRG(List<GastosUnidades> dataList, String NombreReporteGasto, String TipoReporte) {
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        //ESTILO DE CELDA (ENCABEZADOS)
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        //ESTILO DE CELDA (DATOS)
        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = null;
        sheet = workbook.createSheet(TipoReporte);

        Row row = null;
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 5300);
        sheet.setColumnWidth(3, 5100);
        sheet.setColumnWidth(4, 5000);

        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("FECHA");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("UNIDAD");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("TIPO GASTO");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("DESCRIPCIÓN");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("MONTO");
        cell.setCellStyle(cellStyle);

        //DATOS
        for (int i = 0; i < dataList.size(); i++) {
            Row rowData = sheet.createRow(i + 5);

            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getFechaGasto());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getNumUnidad());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getTipoGasto());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getDescripción());
            cell.setCellStyle(cellStyle2);

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getMonto());
            cell.setCellStyle(cellStyle2);

        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, NombreReporteGasto + ".xls");
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            Toast.makeText(getActivity(), "Reporte descargado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    //------------- MOSTRAR FECHA DE HOY ------------------
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}