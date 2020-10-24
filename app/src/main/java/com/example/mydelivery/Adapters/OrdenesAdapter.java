package com.example.mydelivery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.mydelivery.Models.Orden;
import com.example.mydelivery.R;
import java.util.ArrayList;

public class OrdenesAdapter  extends BaseAdapter {
    private Context CONTEXT;
    private ArrayList<Orden> lista;
    private OnSelectItem osi;
    public OrdenesAdapter(Context cont,ArrayList<Orden> l,OnSelectItem os){
        CONTEXT=cont;
        lista=l;
        this.osi = os;
    }
    private int OrdenPosition;
    @Override
    public int getCount() {
        return lista.size();
    }
    @Override
    public Orden getItem(int i) {
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
            view=inflater.inflate(R.layout.orden_pedido_item,null);
        }
        TextView codigo =(TextView) view.findViewById(R.id.txt_opi_codigo);
        TextView hora =(TextView) view.findViewById(R.id.txt_opi_hora);
        TextView total =(TextView) view.findViewById(R.id.txt_opi_precio);
        TextView estado =(TextView) view.findViewById(R.id.txt_opi_estado);

        codigo.setText(lista.get(i).id.substring(lista.get(i).id.length()-9));
        hora.setText(lista.get(i).hora_pedido.split(" ")[1]);
        total.setText(lista.get(i).pago_total+" Bs.");
        estado.setText(lista.get(i).estado);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                osi.onSelect(lista.get(i));
            }
        };
        codigo.setOnClickListener(ocl);
        hora.setOnClickListener(ocl);
        total.setOnClickListener(ocl);
        estado.setOnClickListener(ocl);
        return view;
    }
}
