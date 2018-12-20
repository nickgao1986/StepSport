package com.pic.optimize.recycleview.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pic.optimize.R;
import com.pic.optimize.recycleview.bean.NormalItemInfo;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.SymptomHolder> {

    private Context mContext;
    private ArrayList<NormalItemInfo> mSymptomList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public RecyclerAdapter(Context context, ArrayList<NormalItemInfo> symptomList) {
        mSymptomList = symptomList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.normal_recycleview_item, parent, false);
        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, final int position) {
        holder.icon.setSelected(mSymptomList.get(position).isSelected);
        holder.nameTV.setText(mSymptomList.get(position).name);
        holder.description.setText(mSymptomList.get(position).description);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSymptomList.get(position).isSelected) {
                    deSelectAll();
                }else{
                    deSelectOther(position);
                }
                notifyDataSetChanged();
            }
        });
    }

    public void deSelectOther(final int position) {
       for (int i=0;i<mSymptomList.size();i++) {
           if(i == position) {
               mSymptomList.get(i).isSelected = true;
           }else{
               mSymptomList.get(i).isSelected = false;
           }
       }
    }

    public void deSelectAll() {
        for (int i=0;i<mSymptomList.size();i++) {
            mSymptomList.get(i).isSelected = false;
        }
    }

    @Override
    public int getItemCount() {
        return mSymptomList.size();
    }



    public class SymptomHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView nameTV;
        private TextView description;
        private View mView;


        private SymptomHolder(View itemView) {
            super(itemView);
            mView = itemView;
            icon = (ImageView)itemView.findViewById(R.id.symptom_icon);
            nameTV = (TextView)itemView.findViewById(R.id.symptom_name);
            description = (TextView)itemView.findViewById(R.id.description);
        }
    }

}
