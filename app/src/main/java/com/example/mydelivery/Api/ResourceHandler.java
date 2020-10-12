package com.example.mydelivery.Api;

import org.json.JSONObject;

public interface ResourceHandler {
    void onSucces(JSONObject result);
    void onFailure(JSONObject error);
}
