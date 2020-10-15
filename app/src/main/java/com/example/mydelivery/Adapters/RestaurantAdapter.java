package com.example.mydelivery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.Restaurante;
import com.example.mydelivery.R;
import com.example.mydelivery.Sesion;
import com.example.mydelivery.restaurant;

import org.json.JSONObject;

import java.util.ArrayList;


public class RestaurantAdapter extends BaseAdapter {
    private Context CONTEXT;

    private ArrayList<Restaurante> lista;
    private OnChange oc;
    public RestaurantAdapter(Context cont,ArrayList<Restaurante> l,OnChange oc){
        CONTEXT=cont;
        lista=l;
        this.oc = oc;
    }
    private int RestaurantePosition;
    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Restaurante getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater=(LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.restaurant_inicio,null);
        }
        TextView nombre =(TextView) view.findViewById(R.id.txt_ri_nombre);
        ImageView img =(ImageView) view.findViewById(R.id.img_ri_icon);

        ImageButton delete =(ImageButton) view.findViewById(R.id.btn_ri_delete),
                edit =(ImageButton) view.findViewById(R.id.btn_ri_edit);
        if(Sesion.usuario.isAdmin()){
            delete.setOnClickListener(new OnClicList(lista.get(i),Opcion.delete,lista));
            edit.setOnClickListener(new OnClicList(lista.get(i),Opcion.edit,lista));
        } else {
            delete.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
        }

        img.setOnClickListener(new OnClicList(lista.get(i), Opcion.select,lista));

        nombre.setText(lista.get(i).nombre);
        if(lista.get(i).img_logo!=null)
            img.setImageBitmap(lista.get(i).img_logo);
        return view;
    }
    enum Opcion {
        delete,
        edit,
        select
    }

    public class OnClicList implements View.OnClickListener {
        Restaurante restaurante;
        Opcion o;
        private ArrayList<Restaurante> lista;


        public OnClicList(Restaurante r,Opcion op,ArrayList l){
            restaurante=r;
            o=op;
            lista = l;
        }
        @Override
        public void onClick(View view) {
            switch(o){
                case delete:
                    Resource rr = new Resource("restaurant");
                    rr.delete(restaurante.id, new ResourceHandler() {
                        @Override
                        public void onSucces(JSONObject result) {
                            lista.remove(restaurante);
                            oc.onChange(RestaurantAdapter.this);
                        }
                        @Override
                        public void onFailure(JSONObject error) {

                        }
                    });
                    break;
                case edit:
                    Intent i = new Intent(CONTEXT, restaurant.class);
                    i.putExtra("restaurantJson",restaurante.json.toString());
                    CONTEXT.startActivity(i);
                    break;
                case select:

                    break;
            }
        }
    }
}