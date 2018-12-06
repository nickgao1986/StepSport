package com.example.textviewwithurl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;

import com.example.item.LinkInfo;
import com.example.model.MessageFaceModel;
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
		MessageFaceModel.getInstance().init(this);
		tvState = (ScrollText)findViewById(R.id.news_statustxt);
		IntroView news_item_text = (IntroView)findViewById(R.id.news_item_text);
		//String s = "(#%$%$%3434343434343$%$%youjiancau@163.com$dfsfsfsdffds^15959224872)dfsfdsafsaf@153.cn&&fefrewafrewfjwio(fsfsfsd@tre.com.cn()()()www.baidu.com3242343243www.sohu.com@afjiofjfjaof";
		String s = "dsafsdfsdf(#可怜)fsgfdg(#鬼脸)fdgdfgdfgdfgdfgdfgfdgfdgfdgfdgdfg";

		news_item_text.setTitleList(ParseNewsInfoUtil.parseStr(s));
		news_item_text.setOnClickLinkListener(this);
		initStatus();

	}
	
    private void initStatus(){
    	ivState = (ImageView) findViewById(R.id.news_statusinput);
    	String s = "dsafsdfsdf(#闭嘴)fsgfdg(#睡觉)fdgdfgdfgdfgdfgdfgfdgfdgfdgfdgdfgfgdfgdfgdfgfdgfdgfdgfdgdfg";
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
