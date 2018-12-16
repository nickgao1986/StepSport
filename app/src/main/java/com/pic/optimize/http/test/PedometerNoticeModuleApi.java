package com.pic.optimize.http.test;


import android.util.Log;

import com.pic.optimize.http.api.ApiBase;
import com.pic.optimize.http.constant.Url;

import org.json.JSONObject;

import static com.pic.optimize.http.api.ApiKey.DATA;

/**
 * Created by gyj on 2018/4/20.
 */

public class PedometerNoticeModuleApi extends ApiBase {


    public PedometerNoticeModuleApi(boolean isPedometerMainPage) {
        super();
    }

    @Override
    protected String getUrl() {
        return Url.HOST_URL + "/preg_intf/walkmate/get_text";
    }

    @Override
    protected  void parse(JSONObject jsonObject) throws Exception {
        JSONObject data = jsonObject.optJSONObject(DATA);
        if (data != null) {
            Log.d("TAG","<<<<<data="+data);
        }
    }

}
