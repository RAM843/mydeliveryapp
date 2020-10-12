package com.example.mydelivery.Api;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class Auth  {
    private AsyncHttpClient asyncHttpClient;
    public Auth(){
        asyncHttpClient = new AsyncHttpClient();
    }

    public void login(String email, String password,ResourceHandler rh){
        RequestParams params = new RequestParams();
        params.add("email",email);
        params.add("password",password);
        asyncHttpClient.post(Config.ApiURL+"/auth/login",params,new Resource.JsonHttpHandler(rh));
    }
}
