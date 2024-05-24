package com.example.atq.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.atq.Fragment.FragmentBitacora;
import com.example.atq.Fragment.FragmentInicio;
import com.example.atq.Fragment.FragmentReportes;
import com.example.atq.Fragment.FragmentRutas;
import com.example.atq.Fragment.FragmentUnidades;

public class AdapterFragment extends FragmentStateAdapter {

    public AdapterFragment(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentInicio();
            case 1:
                return new FragmentBitacora();
            case 2:
                return new FragmentUnidades();
            case 3:
                return new FragmentRutas();
            default:
                return new FragmentReportes();

            //case 5: return new FragmentOperadores();
            //case 6: return new FragmentUsuarios();
            //default: return new FragmentHuellas();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
