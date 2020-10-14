package com.example.mydelivery.Api;

public class Url {
    public static String downloadRestaurantLogoImg(String filename){
        return Config.ApiURL+"/restaurant/download/logo/"+filename;
    }
    public static String downloadRestaurantFotoLugarImg(String filename){
        return Config.ApiURL+"/restaurant/download/foto_lugar/"+filename;
    }
    public static String downloadMenuFotoProductoImg(String filename){
        return Config.ApiURL+"/menu/download/foto_producto/"+filename;
    }
}
