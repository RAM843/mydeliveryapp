package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydelivery.Adapters.OnChange;
import com.example.mydelivery.Adapters.OnSelectItem;
import com.example.mydelivery.Adapters.OrdenesAdapter;
import com.example.mydelivery.Adapters.PedidoItemAdapter;
import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.LoadAllImages;
import com.example.mydelivery.Models.OnSaveModel;
import com.example.mydelivery.Models.Orden;
import com.example.mydelivery.Models.Pedido;
import com.example.mydelivery.Utils.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ordenes extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes);
        loadComponents();
    }
    LinearLayout listaOrdenes;
    TextView tituloOrden;
    GridView listadePedidos;
    TextView totalPedido;
    RadioButton rbEspera;
    RadioButton rbProceso;
    RadioButton rbEnviado;
    Button btnGuardarEstado;
    private void loadComponents(){
        listaOrdenes = findViewById(R.id.ly_ao_listapedidos);
        tituloOrden = findViewById(R.id.txt_ao_detalle);
        listadePedidos = findViewById(R.id.gv_ao_listamenus);
        totalPedido = findViewById(R.id.txt_ao_preciototal);
        rbEspera = findViewById(R.id.rb_ao_espera);
        rbProceso = findViewById(R.id.rb_ao_proceso);
        rbEnviado = findViewById(R.id.rb_ao_enviado);

        rbEspera.setOnClickListener(this);
        rbProceso.setOnClickListener(this);
        rbEnviado.setOnClickListener(this);

        btnGuardarEstado = findViewById(R.id.btn_ao_guardarestado);
        btnGuardarEstado.setOnClickListener(this);
        arrayListOrdenes = new ArrayList<>();
        loadOrdenes();
    }


    ArrayList<Orden> arrayListOrdenes;
    public void loadOrdenes(){
        arrayListOrdenes =new ArrayList<>();
        Resource r = new Resource("orden");
        r.get( new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    final JSONArray lista = result.getJSONArray("data");
                    for (int i=0;i<lista.length();i++){
                        new Orden(lista.getJSONObject(i), new LoadAllImages() {
                            @Override
                            public void finishLoadImages(Object o) {
                                arrayListOrdenes.add((Orden)o);
                                if(lista.length()==arrayListOrdenes.size()){
                                    refreshListaOrdenes();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(final JSONObject error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    Orden selectOrden = null;
    public void refreshListaOrdenes(){
        OrdenesAdapter oa = new OrdenesAdapter(getApplication(),arrayListOrdenes, new OnSelectItem() {
            @Override
            public void onSelect(Object item) {
                selectOrden = (Orden) item;
                showPedidos();
            }
        });
        listaOrdenes.removeAllViews();
        for (int i=0;i<oa.getCount();i++){
            listaOrdenes.addView(oa.getView(i,null,null));
        }
    }

    public void showPedidos(){
        PedidoItemAdapter pi = new PedidoItemAdapter(getApplicationContext(), selectOrden.pedidos, new OnChange() {
            @Override
            public void onChange(BaseAdapter ba) {

            }
        }, new OnSelectItem() {
            @Override
            public void onSelect(Object item) {

            }
        });
        pi.setEditable(false);
        listadePedidos.setAdapter(pi);
        totalPedido.setText(calcularTotal()+" Bs.");
        setValueButtons(selectOrden.estado);
    }
    public void setValueButtons(String v){
        switch (v){
            case "espera":
                rbEspera.toggle();
                break;
            case "proceso":
                rbProceso.toggle();
                break;
            case "enviado":
                rbEnviado.toggle();
                break;
        }
    }




    private double calcularTotal() {
        double total = 0;
        for (Pedido p: selectOrden.pedidos){
            total+=p.getCantidad()*p.getMenu().precio;
        }
        return total;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_ao_espera:
                if (selectOrden!=null)
                    selectOrden.estado="espera";
                break;
            case R.id.rb_ao_proceso:
                if (selectOrden!=null)
                    selectOrden.estado="proceso";
                break;
            case R.id.rb_ao_enviado:
                if (selectOrden!=null)
                    selectOrden.estado="enviado";
                break;
            case R.id.btn_ao_guardarestado:
                if (selectOrden!=null) {
                    try {
                        selectOrden.save(new OnSaveModel() {
                            @Override
                            public void onSave(Object o) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Estado Modificado",Toast.LENGTH_SHORT).show();
                                        loadOrdenes();
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final String err) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),err,Toast.LENGTH_SHORT).show();
                                        loadOrdenes();
                                    }
                                });

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}