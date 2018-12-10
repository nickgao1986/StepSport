package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.shoplistdownload.ContactDetail;
import com.example.shoplistdownload.ContactsList;
import com.example.shoplistdownload.TabActivityWithAnimation;
import com.example.shoplistdownload.TestDropdownHideActivity;
import com.pic.optimize.database.DatabaseTestActivity;
import com.pic.optimize.fresco.FeedsMainAdapter;
import com.pic.optimize.picwall.PicWallActivity;

import java.util.ArrayList;

public class TestListActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);
        ListView listView = (ListView) findViewById(R.id.news_home_listview);
        listView.setAdapter(new FeedsMainAdapter(this,getList()));
        listView.setOnItemClickListener(this);
    }


    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("NewsSwitchActivity");
        list.add("SnapToScreenActivity");
        list.add("PicWallActivity");
        list.add("ParseJsonTest");
        list.add("TabActivityWithAnimation");
        list.add("OppositeDrawActivity");
        list.add("DialogTest");
        list.add("InputFaceActivity");
        list.add("LicenseActivity");
        list.add("UploadPhotoActivity");
        list.add("DatabaseTestActivity");
        list.add("TestDropdownHideActivity");
        list.add("ContactsList");
        list.add("ContactDetail");
        list.add("性能优化");
        list.add("图片优化");
        list.add("存储优化");
        list.add("加载优化");
        list.add("第三方框架");
        list.add("图片压缩");
        list.add("超大图片加载");
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                NewsSwitchActivity.startActivity(TestListActivity.this);
                break;
            case 1:
                SnapToScreenActivity.startActivity(TestListActivity.this);
                break;
            case 2:
                PicWallActivity.startActivity(TestListActivity.this);
                break;
            case 3:
                ParseJsonTest.startActivity(TestListActivity.this);
                break;
            case 4:
                TabActivityWithAnimation.startActivity(TestListActivity.this);
                break;
            case 5:
                OppositeDrawActivity.startActivity(TestListActivity.this);
                break;
            case 6:
                DialogTest.startActivity(TestListActivity.this);
                break;
            case 7:
                InputFaceActivity.startActivity(TestListActivity.this);
                break;
            case 8:
                LicenseActivity.startActivity(TestListActivity.this);
                break;
            case 9:
                UploadPhotoActivity.startActivity(TestListActivity.this);
                break;
            case 10:
                DatabaseTestActivity.startActivity(TestListActivity.this);
                break;
            case 11:
                TestDropdownHideActivity.startActivity(TestListActivity.this);
                break;
            case 12:
                ContactsList.startActivity(TestListActivity.this);
                break;
            case 13:
                ContactDetail.startActivity(TestListActivity.this);
                break;
        }
    }
}

