package com.example.textviewwithurl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;

import com.example.item.LinkInfo;
import com.example.model.NewsModel;
import com.example.model.StateFaceModel;
import com.example.util.ParseNewsInfoUtil;
import com.example.view.IntroView;
import com.example.view.IntroView.OnClickLinkListener;
import com.example.view.ScrollText;
import com.pic.optimize.R;

public class NewsActivity extends Activity implements OnClickLinkListener{

	private ScrollText tvState;
	private ImageView ivState;
	private NewsModel newsModel = NewsModel.getInstance();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_activity);
		StateFaceModel.getInstance().init(this);
		IntroView news_item_text = (IntroView)findViewById(R.id.news_item_text);
		String text = "如何在TextView中插入表情符号如(#f1)，邮件youjiancau@sina.com,电话号码18559298168呢，默认的TextView是支持不了这个特性的";

		news_item_text.setTitleList(ParseNewsInfoUtil.parseStr(text));
		news_item_text.setOnClickLinkListener(this);
	}
	
    private void initStatus(){
    	ivState = (ImageView) findViewById(R.id.news_statusinput);
    	String s = "厦门这边的沙茶面很好吃，尤其是民族路上的一家很有名的店(#f2)";
    	newsModel.setStatus(s);
    	String strModel = newsModel.getStatus();
        setStateText(strModel);
	}
    
	private void setStateText(String strModel){
		if(!TextUtils.isEmpty(strModel)){
			tvState.setStateList(newsModel.getStateList());
			tvState.setText(strModel);
            tvState.init(getWindowManager(), handler);
            tvState.startScroll();
            tvState.start();
        }
	}
	
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ScrollText.TEXT_TIMER:
					if(tvState != null){
						tvState.scrollText();
					}
					break;
				default:
					break;
				}
			}
		};

	@Override
	public void onClick(LinkInfo linkInfo) {
		System.out.println("====linkInfo="+linkInfo.getContent());
	}
    

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
