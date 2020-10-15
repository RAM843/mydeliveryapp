package com.example.mydelivery.Models;

import android.graphics.Bitmap;

import com.example.mydelivery.Api.Url;
import com.example.mydelivery.Utils.OnLoadImg;
import com.example.mydelivery.Utils.TaskImg;

import org.json.JSONObject;

public class Restaurante {
    public String id,nombre, nit, propietario, direccion, telefono,logo, fechaderegistro, foto_lugar;
    public double log,lat;
    public Bitmap img_logo=null,img_foto_lugar=null;
    public LoadAllImages olimgs=null;
    public JSONObject json;
    public Restaurante(JSONObject jo){
        json =jo;
        try{
            id = jo.getString("_id");
            nombre = jo.getString("nombre");
            nit = jo.getString("nit");
            propietario = jo.getString("propietario");
            direccion = jo.getString("calle");
            telefono = jo.getString("telefono");
            log = jo.getDouble("log");
            lat = jo.getDouble("lat");
            logo = jo.getString("logo");
            fechaderegistro = jo.getString("fechaderegistro");
            foto_lugar = jo.getString("foto_lugar");
            loadLogoImg();
            loadFotoLugarImg();
        }catch (Exception e){

        }
    }
    public Restaurante(JSONObject jo,LoadAllImages o){
        json = jo;
        olimgs = o;

        try{
            id = jo.getString("_id");
            nombre = jo.getString("nombre");
            nit = jo.getString("nit");
            propietario = jo.getString("propietario");
            direccion = jo.getString("calle");
            telefono = jo.getString("telefono");
            log = jo.getDouble("log");
            lat = jo.getDouble("lat");
            logo = jo.getString("logo");
            fechaderegistro = jo.getString("fechaderegistro");
            foto_lugar = jo.getString("foto_lugar");
            loadLogoImg();
            loadFotoLugarImg();
        }catch (Exception e){

        }

    }

    private void loadLogoImg(){
        TaskImg ti = new TaskImg(new OnLoadImg() {
            @Override
            public void onLoadImg(Bitmap img) {
                img_logo = img;
                if(olimgs!=null)
                    loadAllimgs();
            }
        });
        ti.execute(Url.downloadRestaurantLogoImg(logo));
    }
    private void loadFotoLugarImg(){
        TaskImg ti = new TaskImg(new OnLoadImg() {
            @Override
            public void onLoadImg(Bitmap img) {
                img_foto_lugar = img;
                if(olimgs!=null)
                    loadAllimgs();
            }
        });
        ti.execute(Url.downloadRestaurantFotoLugarImg(foto_lugar));
    }

    public void loadAllimgs(){
        if(img_logo!=null&&img_foto_lugar!=null)
            olimgs.finishLoadImages(this);
    }
}
