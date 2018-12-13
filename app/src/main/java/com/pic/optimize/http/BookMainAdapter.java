package com.pic.optimize.http;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pic.optimize.R;

import java.util.ArrayList;

public class BookMainAdapter extends BaseAdapter{

    private ArrayList<Book> mList = new ArrayList<>();
    private Context mContext;

    public BookMainAdapter(Context context) {
        this.mContext = context;
    }
    public void setList(ArrayList<Book> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position).bookName;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feeds_main_item,null);
            holder = new ViewHolder();
            holder.mTextView = (TextView)convertView.findViewById(R.id.main_text);
            holder.mPic = (SimpleDraweeView)convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Book book = mList.get(position);
        holder.mTextView.setText(book.bookName);
        holder.mPic.setImageURI(book.icon);

        return convertView;
    }

    class ViewHolder{
        public TextView mTextView;
        public SimpleDraweeView mPic;
    }
}
