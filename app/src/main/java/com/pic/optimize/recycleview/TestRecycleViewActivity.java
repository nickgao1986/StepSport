package com.pic.optimize.recycleview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.pic.optimize.R;
import com.pic.optimize.recycleview.adapter.RecyclerAdapter;
import com.pic.optimize.recycleview.bean.NormalItemInfo;

import java.util.ArrayList;


public class TestRecycleViewActivity extends Activity {


    private static final String TAG = TestRecycleViewActivity.class.getSimpleName();

    private RecyclerView mListRecyclerView;

    private RecyclerAdapter mAdapter;
    private ArrayList<NormalItemInfo> mSymptomList = new ArrayList<>();
    private String[] mDescriptionArray;


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TestRecycleViewActivity.class);
        context.startActivity(intent);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_list_acitivity);
        mListRecyclerView = (RecyclerView) findViewById(R.id.baby_recycler_view);

        mDescriptionArray = getResources().getStringArray(R.array.description);

        updateSymptomInfo();

        mAdapter = new RecyclerAdapter(this, mSymptomList);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListRecyclerView.setAdapter(mAdapter);


        RelativeLayout knowledge = (RelativeLayout) findViewById(R.id.knowledge);
        knowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void updateSymptomInfo() {
        if (isFinishing()) {
            return;
        }

        for(int i=0;i<mDescriptionArray.length;i++) {
            NormalItemInfo symptomInfo = new NormalItemInfo();
            symptomInfo.name = CalendarCont.mList[i];
            symptomInfo.description = mDescriptionArray[i];
            mSymptomList.add(symptomInfo);
        }
    }

}
