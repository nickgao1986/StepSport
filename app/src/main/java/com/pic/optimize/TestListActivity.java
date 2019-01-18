package com.pic.optimize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.shoplistdownload.ContactsList;
import com.example.shoplistdownload.TabActivityWithAnimation;
import com.example.shoplistdownload.TestDropdownHideActivity;
import com.pic.optimize.arouter.ArouterTest;
import com.pic.optimize.database.DatabaseTestActivity;
import com.pic.optimize.expandabletext.ExpandableEditActivity;
import com.pic.optimize.export.ShareActivity;
import com.pic.optimize.fresco.FeedsMainAdapter;
import com.pic.optimize.menu.MenuActivity;
import com.pic.optimize.picwall.PicWallActivity;
import com.pic.optimize.recycleview.TestRecycleViewActivity;
import com.pic.optimize.rotatemenu.RoateMenuActivity;
import com.pic.optimize.satelite.SateActivity;
import com.pic.optimize.tab.TabActivity;
import com.pic.optimize.tutorail.TutorailActivity;
import com.pic.optimize.weibo.WriteBlog;

import java.util.ArrayList;

public class TestListActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = TestListActivity.class.getSimpleName();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);
        mListView = (ListView) findViewById(R.id.news_home_listview);
        mListView.setAdapter(new FeedsMainAdapter(this,getList()));
        mListView.setOnItemClickListener(this);

    }



    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("NewsSwitchActivity");
        list.add("SnapToScreenActivity");
        list.add("PicWallActivity");
        list.add("TestHttpActivity");
        list.add("TabActivityWithAnimation");
        list.add("OppositeDrawActivity");
        list.add("DialogTest");
        list.add("InputFaceActivity");
        list.add("LicenseActivity");
        list.add("UploadPhotoActivity");
        list.add("DatabaseTestActivity");
        list.add("TestDropdownHideActivity");
        list.add("ContactsList");
        list.add("ExpandListItemActivity");
        list.add("DragIconActivity");
        list.add("TestRecycleViewActivity");
        list.add("SateActivity");
        list.add("MenuActivity");
        list.add("ShareActivity");
        list.add("TutorailActivity");
        list.add("RoateMenuActivity");
        list.add("ArouterTest");
        list.add("TabActivity");
        list.add("ExpandableEditActivity");
        list.add("WriteBlog");
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
                TestHttpActivity.startActivity(TestListActivity.this);
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
                ExpandListItemActivity.startActivity(TestListActivity.this);
                break;
            case 14:
                DragIconActivity.startActivity(TestListActivity.this);
                break;
            case 15:
                TestRecycleViewActivity.startActivity(TestListActivity.this);
                break;
            case 16:
                SateActivity.startActivity(TestListActivity.this);
                break;
            case 17:
                MenuActivity.startActivity(TestListActivity.this);
                break;
            case 18:
                ShareActivity.startActivity(TestListActivity.this);
                break;
            case 19:
                TutorailActivity.startActivity(TestListActivity.this);
                break;
            case 20:
                RoateMenuActivity.startActivity(TestListActivity.this);
                break;
            case 21:
                ArouterTest.startActivity(TestListActivity.this);
                break;
            case 22:
                TabActivity.startActivity(TestListActivity.this);
                break;
            case 23:
                ExpandableEditActivity.startActivity(TestListActivity.this);
                break;
            case 24:
                WriteBlog.startActivity(TestListActivity.this);
                break;

        }
    }
}

