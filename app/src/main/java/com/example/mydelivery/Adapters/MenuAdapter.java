package com.example.mydelivery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.Menu;
import com.example.mydelivery.Models.Restaurante;
import com.example.mydelivery.R;
import com.example.mydelivery.Sesion;

import org.json.JSONObject;

import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {
    private Context CONTEXT;

    private ArrayList<Menu> lista;
    private OnChange oc;
    private OnSelectItem osi;
    public MenuAdapter(Context cont,ArrayList<Menu> l,OnChange oc,OnSelectItem os){
        CONTEXT=cont;
        lista=l;
        this.oc = oc;
        this.osi = os;
    }
    private int MenuPosition;
    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Menu getItem(int i) {
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
            view=inflater.inflate(R.layout.menu_inicio,null);
        }
        TextView nombre =(TextView) view.findViewById(R.id.txt_mi_nombre);
        TextView precio =(TextView) view.findViewById(R.id.txt_mi_precio);
        ImageView img =(ImageView) view.findViewById(R.id.img_mi_fotoproducto);

        LinearLayout ly = (LinearLayout) view.findViewById(R.id.lyt_mi_listbuttons);

        Button agregar =(Button) view.findViewById(R.id.btn_mi_agregar);
        Button edit =(Button) view.findViewById(R.id.btn_mi_editar);
        Button delete =(Button) view.findViewById(R.id.btn_mi_eliminar);
        if(Sesion.usuario.isAdmin()){
            ly.removeView(agregar);
            delete.setOnClickListener(new MenuAdapter.OnClicList(lista.get(i), MenuAdapter.Opcion.delete,lista));
            edit.setOnClickListener(new MenuAdapter.OnClicList(lista.get(i), MenuAdapter.Opcion.edit,lista));
        } else {
            ly.removeView(delete);
            ly.removeView(edit);
            agregar.setOnClickListener(new MenuAdapter.OnClicList(lista.get(i),MenuAdapter.Opcion.edit,lista));

        }

        nombre.setText(lista.get(i).nombre);
        precio.setText(lista.get(i).precio+"");
        if(lista.get(i).img_fotografiaProducto!=null)
            img.setImageBitmap(lista.get(i).img_fotografiaProducto);
        return view;
    }
    enum Opcion {
        delete,
        edit,
        agregar
    }

    public class OnClicList implements View.OnClickListener {
        Menu menu;
        MenuAdapter.Opcion o;
        private ArrayList<Restaurante> lista;


        public OnClicList(Menu r, MenuAdapter.Opcion op, ArrayList l){
            menu=r;
            o=op;
            lista = l;
        }
        @Override
        public void onClick(View view) {
            switch(o){
                case delete:
                    Resource rr = new Resource("menu");
                    rr.delete(menu.id, new ResourceHandler() {
                        @Override
                        public void onSucces(JSONObject result) {
                            lista.remove(menu);
                            oc.onChange(MenuAdapter.this);
                        }
                        @Override
                        public void onFailure(JSONObject error) {

                        }
                    });
                    break;
                case edit:
                case agregar:
                    osi.onSelect(menu);
                    break;

            }
        }
    }
}
