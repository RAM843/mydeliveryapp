package com.example.mydelivery.Utils;

public class Query {
    private String query="";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void add(String key,String value){
        if(query.equals("")){
            query="?"+key+"="+value;
        }
        else{
            query="&"+key+"="+value;
        }
    }
}
