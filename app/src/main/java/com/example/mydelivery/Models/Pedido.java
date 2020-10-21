package com.example.mydelivery.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Pedido {
    int cantidad;
    private Menu menu;

    public Pedido(int c, Menu m){
        cantidad = c;
        menu = m;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id_restaurante",menu.id_restaurante);
        jo.put("id_menu",menu.id);
        jo.put("cantidad",cantidad);

        return jo;
    }
}
