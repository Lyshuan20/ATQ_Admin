package com.example.atq;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.atq.Adapter.AdapterFragmentPersonal;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Dashboard_Personal extends AppCompatActivity {

    ViewPager2 viewPager2;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_personal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager2 = findViewById(R.id.viewPagerPersonal);
        tabLayout = findViewById(R.id.tabLayoutPersonal);

        viewPager2.setAdapter(new AdapterFragmentPersonal(this));
        //------------- MENU CON SLIDER PAGE PARA PANTALLA DE ADMIN ------------------
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_home));
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_operadores));
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_usuario));
                        break;
                    case 3:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_huella));
                        break;
                    case 4:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_asistencias));
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }
}