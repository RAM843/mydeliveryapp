package com.example.mydelivery.Models;
import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Orden {
    public String id=null,idrestaurante,idcliente,hora_pedido,estado;
    public double pago_total;
    public ArrayList<Pedido> pedidos;
    LatLng lugardeenvio;

    public JSONObject json;
    LoadAllImages lai;

    public Orden (JSONObject jo){
        try{
            loadToJson(jo);
        }catch (Exception e){
        }
    }
    public Orden(JSONObject jo,LoadAllImages o){
        lai = o;
        try{
            loadToJson(jo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loadToJson(JSONObject jo) throws JSONException {
        json = jo;
        id = jo.getString("_id");
        if (jo.has("idrestaurante"))
        idrestaurante = jo.getString("idrestaurante");
        idcliente = jo.getString("idcliente");
        hora_pedido = jo.getString("hora_pedido");
        estado = jo.getString("estado");
        if (jo.has("lugardeenvio"))
        lugardeenvio = new LatLng(jo.getJSONObject("lugardeenvio").getDouble("lat"),
                jo.getJSONObject("lugardeenvio").getDouble("log"));
        pago_total = jo.getDouble("pago_total");
        pedidos = new ArrayList<>();

        final JSONArray ps = jo.getJSONArray("pedidos");
        for (int i=0;i<ps.length();i++) {
            JSONObject jop = ps.getJSONObject(i);
            Pedido p = new Pedido(jop.getInt("cantidad"), jop.getString("idmenu"), new LoadAllImages() {
                @Override
                public void finishLoadImages(Object o) {
                    pedidos.add((Pedido)o);
                    if (pedidos.size()==ps.length()){
                        lai.finishLoadImages(Orden.this);
                    }
                }
            });
        }
    }
    public void save(final OnSaveModel osm) throws JSONException {
        Resource rm = new Resource("orden");
        JSONObject jo = new JSONObject();
        jo.put("estado",estado);
        ResourceHandler rh = new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                osm.onSave(Orden.this);
            }
            @Override
            public void onFailure(JSONObject error) {
                try {
                    osm.onFailed(error.getString("msn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        rm.patch(id, jo,rh);
    }
}
