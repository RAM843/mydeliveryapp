package com.example.mydelivery.Api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

public class UploadFile {
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static void uploadRestaurantLogo(String id, File f,ResourceHandler rh) throws FileNotFoundException {
        if(Config._token!=null){
            asyncHttpClient.addHeader("authorization","data "+Config._token);
        }
        RequestParams params = new RequestParams();
        params.put("img_logo",f);
        asyncHttpClient.post(Config.ApiURL+"/restaurant/upload/"+id+"/logo",params,new Resource.JsonHttpHandler(rh));
    }
    public static void uploadRestaurantFotoLugar(String id, File f,ResourceHandler rh) throws FileNotFoundException {
        if(Config._token!=null){
            asyncHttpClient.addHeader("authorization","data "+Config._token);
        }
        RequestParams params = new RequestParams();
        params.put("img_foto_lugar",f);
        asyncHttpClient.post(Config.ApiURL+"/restaurant/upload/"+id+"/foto_lugar",params,new Resource.JsonHttpHandler(rh));

    }
    public static void uploadMenuFoto(String id, File f,ResourceHandler rh) throws FileNotFoundException {
        if(Config._token!=null){
            asyncHttpClient.addHeader("authorization","data "+Config._token);
        }
        RequestParams params = new RequestParams();
        params.put("img_foto_producto",f);
        asyncHttpClient.post(Config.ApiURL+"/menu/upload/"+id+"/foto_producto",params,new Resource.JsonHttpHandler(rh));

    }
}
