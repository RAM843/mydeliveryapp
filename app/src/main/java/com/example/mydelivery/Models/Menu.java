package com.example.mydelivery.Models;

import android.graphics.Bitmap;

import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Api.UploadFile;
import com.example.mydelivery.Api.Url;
import com.example.mydelivery.Utils.OnLoadImg;
import com.example.mydelivery.Utils.TaskImg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class Menu {
    public String id=null,nombre,descripcion,fechaderegistro, fotografia_producto;
    public double precio;

    public File nueva_imagen=null;

    public Bitmap img_fotografiaProducto=null;
    public LoadAllImages olimgs=null;
    public JSONObject json;

    public Menu (JSONObject jo){
        json =jo;
        try{
            loadToJson(jo);
            loadFotoProductoImg();
        }catch (Exception e){

        }
    }
    public Menu(JSONObject jo,LoadAllImages o){
        json = jo;
        olimgs = o;

        try{
            loadToJson(jo);
            loadFotoProductoImg();
        }catch (Exception e){

        }

    }

    public void loadToJson(JSONObject jo) throws JSONException {
        id = jo.getString("_id");
        nombre = jo.getString("nombre");
        descripcion = jo.getString("descripcion");
        if (jo.has("fotografia_producto"))
            fotografia_producto = jo.getString("fotografia_producto");
        precio = jo.getDouble("precio");
        fechaderegistro = jo.getString("fechaderegistro");


    }

    public Menu(String nombre, String descripcion, double precio, File img) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.nueva_imagen = img;
    }

    public void save(final OnSaveModel osm) throws JSONException {
        Resource rm = new Resource("menu");
        JSONObject jo = new JSONObject();
        jo.put("nombre",nombre);
        jo.put("descripcion",descripcion);
        jo.put("precio",precio);
        ResourceHandler rh = new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    loadToJson(result.getJSONObject("doc"));
                    uploadFotoproducto(osm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        if (id==null){
            rm.post(jo,rh);
        }else{
            rm.patch(id, jo,rh);
        }

    }
    public void uploadFotoproducto( final OnSaveModel osm){
        if (nueva_imagen!=null){
            try {
                UploadFile.uploadMenuFoto(id, nueva_imagen, new ResourceHandler() {
                    @Override
                    public void onSucces(JSONObject result) {
                        try {
                            fotografia_producto = result.getJSONObject("doc").getString("fotografia_producto");
                            loadFotoProductoImg();
                            osm.onSave(Menu.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(JSONObject error) {
                        try {
                            osm.onFailed(error.getString("msn"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            osm.onSave(this);
        }
    }

    private void loadFotoProductoImg(){
        TaskImg ti = new TaskImg(new OnLoadImg() {
            @Override
            public void onLoadImg(Bitmap img) {
                img_fotografiaProducto = img;
                if(olimgs!=null)
                    loadAllimgs();
            }
        });
        ti.execute(Url.downloadMenuFotoProductoImg(fotografia_producto));
    }


    public void loadAllimgs(){
            olimgs.finishLoadImages(this);
    }
}
