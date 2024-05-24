package com.example.atq.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.atq.Fragment.FragmentAsistencias;
import com.example.atq.Fragment.FragmentHuellas;
import com.example.atq.Fragment.FragmentInicioPersonal;
import com.example.atq.Fragment.FragmentOperadores;
import com.example.atq.Fragment.FragmentUsuarios;

public class AdapterFragmentPersonal extends FragmentStateAdapter {

    public AdapterFragmentPersonal(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentInicioPersonal();
            case 1:
                return new FragmentOperadores();
            case 2:
                return new FragmentUsuarios();
            case 3:
                return new FragmentHuellas();
            default:
                return new FragmentAsistencias();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
