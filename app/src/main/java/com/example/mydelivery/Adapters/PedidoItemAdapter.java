package com.example.mydelivery.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mydelivery.Models.Pedido;
import com.example.mydelivery.R;
import java.util.ArrayList;

public class PedidoItemAdapter extends BaseAdapter {
    private Context CONTEXT;

    private ArrayList<Pedido> lista;
    private OnChange oc;
    private OnSelectItem os;
    public PedidoItemAdapter(Context cont,ArrayList<Pedido> l,OnChange oc,OnSelectItem os){
        CONTEXT=cont;
        lista=l;
        this.oc = oc;
        this.os = os;
    }
    public  ArrayList<Pedido> getLista(){
        return  lista;
    }


    private int PedidoPosition;
    @Override
    public int getCount() {
        return lista.size();
    }


    @Override
    public Pedido getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater=(LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.pedido_item,null);
        }
        TextView nombre =(TextView) view.findViewById(R.id.txt_pi_nombre);
        TextView precio =(TextView) view.findViewById(R.id.txt_pi_precio);
        final TextView total = (TextView) view.findViewById(R.id.txt_pi_dtotal);
        ImageView img =(ImageView) view.findViewById(R.id.img_pi_fp);
        final EditText cantidad = (EditText) view.findViewById(R.id.txt_pi_cantidad);

        ImageButton delete =(ImageButton) view.findViewById(R.id.btn_pi_delete);

        nombre.setText(lista.get(i).getMenu().nombre);
        precio.setText(lista.get(i).getMenu().precio+"");
        total.setText(lista.get(i).getMenu().precio+"*"+lista.get(i).getCantidad()+"="+(lista.get(i).getMenu().precio*lista.get(i).getCantidad())+" Bs.");
        if(lista.get(i).getMenu().img_fotografiaProducto!=null)
            img.setImageBitmap(lista.get(i).getMenu().img_fotografiaProducto);
        cantidad.setText(lista.get(i).getCantidad()+"");

        cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(cantidad.getText().toString().length()>0) {
                    lista.get(i).setCantidad(Integer.parseInt(cantidad.getText().toString()));
                    total.setText(lista.get(i).getMenu().precio + "*" + lista.get(i).getCantidad() + "=" + (lista.get(i).getMenu().precio * lista.get(i).getCantidad()) + " Bs.");
                    os.onSelect(lista.get(i));
                }
            }
        });

        delete.setOnClickListener(new OnClicList(lista.get(i)));
        return view;
    }

    public class OnClicList implements View.OnClickListener {
        Pedido pedido;
        private ArrayList<Pedido> lista;


        public OnClicList(Pedido p){
            pedido = p;
        }
        @Override
        public void onClick(View view) {
            lista.remove(pedido);
            oc.onChange(PedidoItemAdapter.this);
        }
    }

}
