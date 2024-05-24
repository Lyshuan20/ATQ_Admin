package com.example.atq.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.atq.Dashboard_Admin;
import com.example.atq.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FragmentInicioPersonal extends Fragment {

    ImageView btnCambiarPagina;
    //GRAFICAS
    PieChart GraficGanaciaChofer;
    BarChart GraficHorasUsuarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inicio_personal, container, false);

        btnCambiarPagina = root.findViewById(R.id.btnCambiarPagina2);

        GraficGanaciaChofer = root.findViewById(R.id.GraficGananciaChofer);
        GraficHorasUsuarios = root.findViewById(R.id.GraficHorasUsuarios);

        //-------
        calcularGananciaPorOperador();
        calcularHorasTrabajadas();

        //------------- BOTÓN PAGINA ------------------
        btnCambiarPagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard_Admin.class);
                startActivity(intent);
            }
        });
        //------------
        return root;
    }

    //------------- GANACIAS POR OPERADOR ------------------
    private Map<String, Double> calcularGananciaPorOperador() {
        final Map<String, Double> gananciaPorOperador = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Liquidaciones");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener el valor de "nombreOperador" y "cantidadLiquidacion" de cada nodo
                    String nombreOperador = snapshot.child("nombreOperador").getValue(String.class);
                    double cantidadLiquidacion = snapshot.child("cantidadLiquidacion").getValue(Double.class);

                    // Sumar la cantidadLiquidacion a la ganancia correspondiente en gananciaPorOperador
                    if (gananciaPorOperador.containsKey(nombreOperador)) {
                        double gananciaActual = gananciaPorOperador.get(nombreOperador);
                        gananciaPorOperador.put(nombreOperador, gananciaActual + cantidadLiquidacion);
                    } else {
                        gananciaPorOperador.put(nombreOperador, cantidadLiquidacion);
                    }
                }

                // Una vez que se hayan procesado todos los datos, actualiza el gráfico
                configurarGraficoPieOperador(gananciaPorOperador);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });

        // Devolver un mapa vacío (los datos se llenarán en onDataChange)
        return gananciaPorOperador;
    }

    //-------------------------------
    private void configurarGraficoPieOperador(Map<String, Double> gananciaPorOperador) {
        List<PieEntry> entries = convertirDatosParaGraficoPieOperador(gananciaPorOperador);

        PieDataSet dataSet = new PieDataSet(entries, "");
        PieData pieData = new PieData(dataSet);
        dataSet.setValueTextSize(11);
        dataSet.setValueTextColor(Color.WHITE);

        // Configurar colores de Material Design
        int[] colores = ColorTemplate.MATERIAL_COLORS;
        dataSet.setColors(colores);

        // Configurar leyendas
        Legend legend = GraficGanaciaChofer.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12);
        legend.setFormSize(20);
        legend.setFormToTextSpace(2);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        // Obtener nombres de operadores y cortarlos para mostrar solo la primera palabra
        List<String> nombresOperadores = new ArrayList<>(gananciaPorOperador.keySet());
        List<String> nombresCortados = new ArrayList<>();

        for (String nombre : nombresOperadores) {
            String[] palabras = nombre.split("\\s+");
            nombresCortados.add(palabras[0]);
        }

        // Crear entradas de leyenda personalizadas
        List<LegendEntry> customLegendEntries = new ArrayList<>();

        for (String nombre : nombresCortados) {
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.label = nombre;
            legendEntry.formColor = colores[nombresCortados.indexOf(nombre)];
            customLegendEntries.add(legendEntry);
        }

        // Aplicar las leyendas personalizadas al gráfico
        legend.setCustom(customLegendEntries.toArray(new LegendEntry[0]));

        // Configurar el gráfico de pie
        GraficGanaciaChofer.setData(pieData);
        GraficGanaciaChofer.setDrawEntryLabels(false);
        GraficGanaciaChofer.getDescription().setEnabled(false); // Deshabilita la descripción del gráfico
        GraficGanaciaChofer.invalidate(); // Actualiza el gráfico
    }

    //-------------------------------
    private List<PieEntry> convertirDatosParaGraficoPieOperador(Map<String, Double> gananciaPorOperador) {
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : gananciaPorOperador.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey())); // Conserva el nombre en las leyendas
        }

        return entries;
    }

    //------------- HORAS TRABAJADAS POR EMPLEADO ------------------
    private void calcularHorasTrabajadas() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Asistencias");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Crear un mapa para almacenar las sumas de horas trabajadas por cada empleado
                Map<String, Double> totalHorasPorEmpleado = new HashMap<>();

                // Iterar sobre las fechas
                for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                    // Iterar sobre los empleados en esa fecha
                    for (DataSnapshot empleadoSnapshot : fechaSnapshot.getChildren()) {
                        // Obtener datos
                        String nombreEmpleado = empleadoSnapshot.child("NombreEmpleado").getValue(String.class);
                        String horaEntrada = empleadoSnapshot.child("HoraEntrada").getValue(String.class);
                        String horaSalida = empleadoSnapshot.child("HoraSalida").getValue(String.class);

                        // Calcular horas trabajadas
                        double horasTrabajadas = restarHoras(horaSalida, horaEntrada);

                        // Verificar si ya existe un total para ese empleado
                        if (totalHorasPorEmpleado.containsKey(nombreEmpleado)) {
                            // Actualizar el total acumulado de horas trabajadas para ese empleado
                            double totalHoras = totalHorasPorEmpleado.get(nombreEmpleado);
                            totalHorasPorEmpleado.put(nombreEmpleado, totalHoras + horasTrabajadas);
                        } else {
                            // Agregar un nuevo total para ese empleado
                            totalHorasPorEmpleado.put(nombreEmpleado, horasTrabajadas);
                        }
                    }
                }

                // Ahora puedes utilizar totalHorasPorEmpleado para mostrar o almacenar la información
                // Puedes llamar a una función para configurar el gráfico de barras con estos datos
                configurarGraficoBarrasHorasTotales(totalHorasPorEmpleado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });
    }

    //-------------------------------
    private double restarHoras(String horaSalida, String horaEntrada) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Date fechaEntrada = format.parse(horaEntrada);
            Date fechaSalida = format.parse(horaSalida);

            // Crear instancias de Calendar y establecer las fechas
            Calendar calendarEntrada = Calendar.getInstance();
            Calendar calendarSalida = Calendar.getInstance();
            calendarEntrada.setTime(fechaEntrada);
            calendarSalida.setTime(fechaSalida);

            // Calcular la diferencia en horas
            long diferenciaMillis = calendarSalida.getTimeInMillis() - calendarEntrada.getTimeInMillis();
            long diferenciaHoras = TimeUnit.MILLISECONDS.toHours(diferenciaMillis);
            return diferenciaHoras;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Manejar errores de formato de hora
        }
    }

    //-------------------------------
    private void configurarGraficoBarrasHorasTotales(Map<String, Double> totalHorasPorEmpleado) {
        List<BarEntry> entries = convertirDatosParaGraficoBarras(totalHorasPorEmpleado);

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setValueTextSize(12f);
        // Configurar colores de Material Design
        int[] colores = ColorTemplate.MATERIAL_COLORS;
        dataSet.setColors(colores);  // Puedes personalizar el color de las barras

        BarData barData = new BarData(dataSet);
        GraficHorasUsuarios.setData(barData);

        // Configurar el eje X (empleados)
        XAxis xAxis = GraficHorasUsuarios.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(totalHorasPorEmpleado.keySet()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Configurar el eje Y (horas trabajadas)
        YAxis yAxisLeft = GraficHorasUsuarios.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Actualizar el gráfico
        GraficHorasUsuarios.getDescription().setEnabled(false);
        GraficHorasUsuarios.invalidate();
    }

    //-------------------------------
    private List<BarEntry> convertirDatosParaGraficoBarras(Map<String, Double> totalHorasPorEmpleado) {
        List<BarEntry> entries = new ArrayList<>();

        // Índice para la posición en el gráfico
        float i = 0f;

        // Iterar sobre los empleados y sus horas totales
        for (Map.Entry<String, Double> entryEmpleado : totalHorasPorEmpleado.entrySet()) {
            // Obtener el nombre del empleado
            String nombreEmpleado = entryEmpleado.getKey();

            // Obtener las horas totales para este empleado
            double horasTotales = entryEmpleado.getValue();

            // Agregar la entrada al gráfico
            entries.add(new BarEntry(i++, (float) horasTotales));
        }

        return entries;
    }

    //-------------------------------
    private class XAxisValueFormatter extends ValueFormatter {
        private final Set<String> labels;

        public XAxisValueFormatter(Set<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.toArray(new String[0])[index];
            }
            return "";
        }
    }
}