package kr.co.yapp.campusrecorder.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

import kr.co.yapp.campusrecorder.Adapter.CategoryItemAdapter;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Dialog.SubAddDialogFragment;
import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.RecordApplication;


public class CategoryActivity extends ActionBarActivity {
    DBAdapter dba;
    Context ctx = this;
    private CategoryItemAdapter cItem;
    private LayoutInflater inflater;
    ListView categoryList;
    List<String> list;
    List<String> subList;
    Button btn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        init();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.main_color);

        setSupportActionBar(toolbar);
        categoryList = (ListView)findViewById(R.id.list);
        /**
         * 리스트뷰 세팅
         *  디비 접근, 전체 분류 가져오기
         */
//        잠시 죽임
        RecordApplication app;
        app = (RecordApplication)getApplicationContext();
        subList = app.getList();
        list = new ArrayList<String>();
        for(int i=0;i<subList.size();i++){
            list.add(subList.get(i));
        }

        cItem = new CategoryItemAdapter(getApplicationContext(),R.layout.item_category,list);
        categoryList.setAdapter(cItem);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list = new ArrayList<String>();
                for(int i=0;i<subList.size();i++){
                    list.add(subList.get(i));
                }
                Log.d("position", "" + position);
                String str = list.get(position);

                Intent intent = getIntent();
                intent.putExtra("data_name",str);
                setResult(1,intent);

                finish();
            }
        });

        //항목 추가 버튼 클릭 리스너
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubAddDialogFragment dialogFragment = SubAddDialogFragment.newInstance();
                dialogFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
                dialogFragment.show(getFragmentManager(), "TAG");


            }
        });
    }

    public void init() {
        dba = new DBAdapter(getApplicationContext());
        dba = dba.open();
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        btn = (Button) findViewById(R.id.btn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* for font */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
