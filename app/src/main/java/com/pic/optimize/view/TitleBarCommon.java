package com.pic.optimize.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pic.optimize.R;

public class TitleBarCommon extends RelativeLayout {
    private TextView mTvTitle, mTvLeft, mTvRight;
    private ImageView mIvLeft, mIvRight;
    private View mTitleContainer;
    private Context context;

    public TitleBarCommon(Context context) {
        this(context, null);

    }

    public TitleBarCommon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.mTitleContainer = LayoutInflater.from(context).inflate( R.layout.activity_base_header_common, this,true);
        init();
    }


    public void init() {
        mTvTitle = (TextView) mTitleContainer.findViewById(R.id.baselayout_tv_title);
        mIvLeft = (ImageView) mTitleContainer.findViewById(R.id.baselayout_iv_left);
        mTvLeft = (TextView) mTitleContainer.findViewById(R.id.baselayout_tv_left);
        mIvRight = (ImageView) mTitleContainer.findViewById(R.id.baselayout_iv_right);
        mTvRight = (TextView) mTitleContainer.findViewById(R.id.baselayout_tv_right_yunqi);
    }

    private LayoutInflater mLayoutInflater;
    public void setLayoutInflater(LayoutInflater inflater){
        mLayoutInflater = inflater;
    }
    /**自定义头部**/
    public void setCustomTitleBar(int customTitleLayoutId){
        if (customTitleLayoutId <=0) {
            this.setVisibility(View.GONE);
            return ;
        }
        removeAllViews();
        View baseHead;
        if(mLayoutInflater!=null){
            baseHead = mLayoutInflater.inflate(customTitleLayoutId, null);
        }else{
            baseHead = View.inflate(context,customTitleLayoutId, null);
        }
        RelativeLayout.LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        addView(baseHead,params);
    }

    /**自定义头部**/
    public void setCustomTitleBar(View baseHead){
        if (baseHead == null) {
            this.setVisibility(View.GONE);
            return ;
        }
        removeAllViews();
//        View baseHead = View.inflate(context,customTitleLayoutId, null);
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        addView(baseHead,params);
    }

    /**
     * 右TextView 文字
     *
     * @param
     * @return
     */
    public TitleBarCommon setRightTextViewString(String text) {
        if (!TextUtils.isEmpty(text)) {
            mTvRight.setText(text);
            mTvRight.setVisibility(View.VISIBLE);
        } else {
            mTvRight.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置右TextView点击事件
     *
     * @param lsnr
     * @return
     */
    public TitleBarCommon setRightTextViewListener(OnClickListener lsnr) {
        mTvRight.setOnClickListener(lsnr);
        return this;
    }

}
