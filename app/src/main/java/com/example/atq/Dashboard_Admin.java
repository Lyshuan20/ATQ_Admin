package com.example.atq;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.atq.Adapter.AdapterFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Dashboard_Admin extends AppCompatActivity {

    ViewPager2 viewPager2;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager2 = findViewById(R.id.viewPagerAdmin);
        tabLayout = findViewById(R.id.tabLayoutAdmin);

        viewPager2.setAdapter(new AdapterFragment(this));
        //------------- MENU CON SLIDER PAGE PARA PANTALLA DE ADMIN ------------------
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_home));
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_bitacora));
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_unidades));
                        break;
                    case 3:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_rutas));
                        break;
                    case 4:
                        tab.setIcon(getResources().getDrawable(R.drawable.ic_reportes));
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }
}