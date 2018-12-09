package com.pic.optimize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;

import com.example.json.HistoryResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class ParseJsonTest extends Activity {

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,ParseJsonTest.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseAssertDataUseGson();
        parseAssertData();
    }

    public void parseAssertDataUseGson() {
        InputStream is = null;
        try {
            is = this.getAssets().open("json2.json", Context.MODE_PRIVATE);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            Reader response = new StringReader(new String(buffer));
            Gson gson = new Gson();
            HistoryResponse mydata = gson.fromJson(response,HistoryResponse.class);
            Log.d("TAG","<<<<mydata="+mydata.data.task_list.get(0).status);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void parseAssertData() {
        InputStream is = null;
        try {
            is = this.getAssets().open("json1.json", Context.MODE_PRIVATE);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String temp = new String(buffer);

            Reader response = new StringReader(temp.toString());
            parseResponse(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void parseResponse(Reader response) throws IOException {
        JsonReader reader = new JsonReader(response);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("id".equals(name)) {
                String id = reader.nextString();
                System.out.println("===id="+id);
            }
            else if (name.equals("data")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();

                    String name1;
                    while (reader.hasNext()) {
                        name1 = reader.nextName();
                        if (name1.equals("data1")) {
                            String s1 = reader.nextString();
                            System.out.println("===s1="+s1);
                        } else if (name1.equals("data2")) {
                            String s2 = reader.nextString();
                            System.out.println("===s2="+s2);
                        }  else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
    }

}
