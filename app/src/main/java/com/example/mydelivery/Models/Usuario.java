package com.example.mydelivery.Models;

import org.json.JSONObject;

public class Usuario {
    public String nombre, apellido, ci, telefono, email, password, rol, fechaderegistro;

    public Usuario(JSONObject jo){
        try {
            nombre = jo.getString("nombre");
            apellido = jo.getString("apellido");
            ci = jo.getString("ci");
            telefono = jo.getString("telefono");
            email = jo.getString("email");
            password = jo.getString("password");
            rol = jo.getString("rol");
            fechaderegistro = jo.getString("fechaderegistro");
        }catch(Exception e){
        }
    }

   public boolean isAdmin(){
        return rol.equals("admin");
    }
}
