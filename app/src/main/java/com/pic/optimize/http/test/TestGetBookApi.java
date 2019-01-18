package com.pic.optimize.http.test;


import com.pic.optimize.http.api.ApiUtil;

import org.json.JSONObject;

import static com.pic.optimize.http.api.ApiKey.DATA;

/**
 * Created by gyj on 2018/4/20.
 */

public class TestGetBookApi extends ApiUtil {

    public String mData;

    public TestGetBookApi() {
        super();
    }



    @Override
    protected String getUrl() {
        return "http://139.199.89.89/api/v1/books";
    }

    @Override
    protected  void parseData(JSONObject jsonObject) throws Exception {
        mData = jsonObject.optString(DATA);
    }
}
