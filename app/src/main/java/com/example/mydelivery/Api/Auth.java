package com.example.mydelivery.Api;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class Auth  {
    private AsyncHttpClient asyncHttpClient;
    public Auth(){
        asyncHttpClient = new AsyncHttpClient();
    }

    public void login(String email, String password, final ResourceHandler rh){
        RequestParams params = new RequestParams();
        params.add("email",email);
        params.add("password",password);
        asyncHttpClient.post(Config.ApiURL+"/auth/login",params,new Resource.JsonHttpHandler(new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    Config._token = result.getString("_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rh.onSucces(result);
            }

            @Override
            public void onFailure(JSONObject error) {
                rh.onFailure(error);
            }
        }));
    }
}
