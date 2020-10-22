package com.example.mydelivery.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Pedido {
    private int cantidad;
    private Menu menu;

    public Pedido(int c, Menu m){
        cantidad = c;
        menu = m;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("idrestaurante",menu.id_restaurante);
        jo.put("idmenu",menu.id);
        jo.put("cantidad",cantidad);

        return jo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
