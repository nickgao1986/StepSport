package com.pic.optimize.http.test;


import android.util.Log;

import com.pic.optimize.http.api.ApiUtil;

import org.json.JSONObject;

import static com.pic.optimize.http.api.ApiKey.DATA;

/**
 * Created by gyj on 2018/4/20.
 */

public class TestBookApi extends ApiUtil {


    public TestBookApi(int bookid,String bookName) {
        super();
        addParam("bookid", String.valueOf(bookid));
        addParam("bookName", bookName);
    }

    @Override
    protected String getUrl() {
        return "http://139.199.89.89/api/v1/books";
    }

    @Override
    protected  void parse(JSONObject jsonObject) throws Exception {
        JSONObject data = jsonObject.optJSONObject(DATA);
        if (data != null) {
            Log.d("TAG","<<<<<data="+data);
        }
    }

}
