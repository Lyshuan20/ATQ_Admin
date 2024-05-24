package com.example.atq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.atq.Model.Unidades;
import com.example.atq.R;

import java.util.ArrayList;

public class AdapterImagenUnidades extends BaseAdapter {

    LayoutInflater layoutInflater;
    private ArrayList<Unidades> unidadesList;
    private Context context;

    public AdapterImagenUnidades(ArrayList<Unidades> unidadesList, Context context) {
        this.unidadesList = unidadesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return unidadesList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_unidades_imagen, null);
        }
        ImageView gridImage = view.findViewById(R.id.GV_ImagenUnidad);
        TextView txtNumUni = view.findViewById(R.id.GV_txtNumUnidad);

        Glide.with(context).load(unidadesList.get(i).getFotoUnidad()).into(gridImage);
        txtNumUni.setText(unidadesList.get(i).getNumUnidad());

        return view;
    }
}
