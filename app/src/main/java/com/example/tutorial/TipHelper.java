package com.example.tutorial;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;

public class TipHelper {

	
    private static String sTipsJsonFile = "tutorial_tips.json";
    private static String sTipsJsonFileForTablet = "tutorial_tips_tablet.json";
    private static List<TipInfo> sUnmarkedTips;
    
    public static final List<TipInfo> getUnmarkedTips(Context context) {
        if (sUnmarkedTips == null) {
            sUnmarkedTips = getUnmarkedTipsFromFile(context);
        }
        return sUnmarkedTips;
    }
    
    public static void mark(Context context, TipInfo tip) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(getTipKey(tip), true);
        editor.apply();
    }
    
    
    private static final List<TipInfo> getUnmarkedTipsFromFile(Context context) {
        List<TipInfo> tipInfoList = new ArrayList<TipInfo>();

        do {
            Tip temp[] = getTipsFromFile();
            if (temp == null || temp.length == 0) {
                break;
            }

            SharedPreferences sp = getSharedPreferences(context);
            Map<String, Boolean> markedTips = (Map<String, Boolean>) sp.getAll();
            if (markedTips == null || markedTips.size() == 0) {
                for (int i = 0; i < temp.length; ++i) {
                    Tip t = temp[i];
                    TipInfo tipInfo = new TipInfo();
                    tipInfo.setTip(t);
                    tipInfoList.add(tipInfo);
                }
                break;
            }

            //filter tips which is marked in local file
            for (int i = 0; i < temp.length; ++i) {
                Tip t = temp[i];
                if (!markedTips.containsKey(getTipKey(t))) {
                    TipInfo tipInfo = new TipInfo();
                    tipInfo.setTip(t);
                    tipInfoList.add(tipInfo);
                }
            }

        } while (false);

        return tipInfoList;
    }
    
    private static String getTipKey(Tip tip) {
        return "tutorial_" + tip.getScreen() + "_" + tip.getId() + "_" + tip.getViewId();
    }
    
    
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
    }

    private static Tip[] getTipsFromFile() {
        Tip result[] = null;
        try {
            Type type = new TypeToken<TipJsonInfo>() {
            }.getType();
            String jsonFile = sTipsJsonFile;
            InputStream inputStream = new AssetsFileReader().openTestAssetFile(jsonFile);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            TipJsonInfo sTipInfo = gson.fromJson(reader, type);
            result = sTipInfo.getTips();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    
    private static class AssetsFileReader {
        public InputStream openTestAssetFile(final String fileName) throws IOException {
            InputStream fileStream = this.getClass().getClassLoader()
                    .getResourceAsStream("assets/" + fileName);
            return fileStream;
        }
    }
    
    
}
