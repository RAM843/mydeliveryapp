package com.example.mydelivery.Api;

import com.example.mydelivery.Utils.Query;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Queue;

import cz.msebera.android.httpclient.Header;

public class Resource {
    protected AsyncHttpClient asyncHttpClient;
    protected String ruta;
     public Resource(String ruta){
         this.ruta = ruta;
         asyncHttpClient = new AsyncHttpClient();
         if(Config._token!=null){
             asyncHttpClient.addHeader("authorization","data "+Config._token);
         }
     }
     public RequestParams generateParams(JSONObject jo){
         RequestParams params = JsonHelper.toRequestParams(jo);
         return params;
     }
     public void post(JSONObject data,final ResourceHandler rh){
         RequestParams params = generateParams(data);
         asyncHttpClient.post(Config.ApiURL+"/"+ruta,params,new JsonHttpHandler(rh));

     }
     public void get(final ResourceHandler rh){
         asyncHttpClient.get(Config.ApiURL+"/"+ruta,new JsonHttpHandler(rh));

     }
    public void get(Query q, final ResourceHandler rh){
        asyncHttpClient.get(Config.ApiURL+"/"+ruta+q.getQuery(),new JsonHttpHandler(rh));

    }
     public void put(String id,JSONObject data,ResourceHandler rh){
         RequestParams params = generateParams(data);
         asyncHttpClient.put(Config.ApiURL+"/"+ruta+"/"+id,params,new JsonHttpHandler(rh));

     }
     public void patch(String id,JSONObject data,ResourceHandler rh){
         RequestParams params = generateParams(data);
         asyncHttpClient.patch(Config.ApiURL+"/"+ruta+"/"+id,params,new JsonHttpHandler(rh));

     }
     public void delete(String id,ResourceHandler rh){
         asyncHttpClient.delete(Config.ApiURL+"/"+ruta+"/"+id,new JsonHttpHandler(rh));

     }


     static class JsonHttpHandler extends JsonHttpResponseHandler{
         ResourceHandler rh;
         public JsonHttpHandler (ResourceHandler rh){
             this.rh =rh;
         }
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
             rh.onSucces(response);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
             rh.onFailure(errorResponse);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
             JSONObject jo = new JSONObject();
             try{
                 jo.put("msn",responseString);;
             }catch (JSONException e){
                 e.printStackTrace();
             }
             rh.onFailure(jo);
         }

         @Override
         public boolean getUseSynchronousMode() {
             return false;
         }
     }


}
