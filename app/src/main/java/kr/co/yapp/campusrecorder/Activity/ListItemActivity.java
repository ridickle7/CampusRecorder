package kr.co.yapp.campusrecorder.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.yapp.campusrecorder.Adapter.ListItemAdapter;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.Data.SectionItem;
import kr.co.yapp.campusrecorder.Dialog.RecordDialogFragment;
import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.RecordApplication;


public class ListItemActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    //    SwipeMenuListView listView;
    RecyclerView recyclerView;
    SearchView searchView;
    List<String> subList;
    DBAdapter dba;
    Context ctx = this;
    LayoutInflater mInflater;

    String rName;
    String rPath;
    String arg_String;

    Toolbar toolbar;
//    DrawerLayout dlDrawer;
//    ActionBarDrawerToggle dtToggle;
//    private LinearLayout mDrawer;

    private ListItemAdapter mRecFile;
    private LinearLayoutManager linearLayoutManager;
    //private RecordDateItemAdapter mRecFile2;

    ArrayList list;
    ImageButton plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        arg_String = "전체";
        dba = new DBAdapter(this);
        dba.open();
        mInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = new ArrayList();

        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.animation_leave);

        RecordApplication app;
        app = (RecordApplication) getApplicationContext();
        List<String> subappList = app.getList();

        subList = new ArrayList<String>();
        subList.add("전체");
        for (int i = 0; i < subappList.size(); i++) {
            subList.add(subappList.get(i));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.main_color);


        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //이거만 추가시키면 앞에 아이콘 생김! (여기서는 네비게이션 드로우어)
//        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//        };
//        dlDrawer.setDrawerListener(dtToggle);
//
//        ImageButton navi_cal = (ImageButton) findViewById(R.id.na_cal);
//        ImageButton navi_list = (ImageButton) findViewById(R.id.na_list);
//        ImageButton navi_help = (ImageButton) findViewById(R.id.na_help);
//
//        navi_cal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent3 = new Intent(ListItemActivity.this, MainActivity.class);
//                startActivity(intent3);
//                dlDrawer.closeDrawers();
//                finish();
//            }
//        });
//
//        navi_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlDrawer.closeDrawers();
//            }
//        });
//
//        navi_help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlDrawer.closeDrawers();
//            }
//        });

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner,
                toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        ArrayAdapter<String> adapters = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                subList);

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(adapters);

        spinner.setOnItemSelectedListener(this);

        setSupportActionBar(toolbar);



/*        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapters = new ArrayAdapter<String>(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                subList);

        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(adapters, this);*/


//        listView = (SwipeMenuListView) findViewById(R.id.listView);
        recyclerView = (RecyclerView) findViewById(R.id.listView);

        plus = (ImageButton) findViewById(R.id.button2);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordDialogFragment dialogFragmentR = RecordDialogFragment.newInstance();
                dialogFragmentR.setStyle(R.style.Theme_Dialog_Transparent2, R.style.Theme_Dialog_Transparent2);
                dialogFragmentR.show(getFragmentManager(), "TAG");
            }
        });

    }

    void listUpDate(final ArrayList list) {
//        mRecFile = new SectionItemAdapter(getApplicationContext(), R.layout.item_list, list);
//
//        listView.setAdapter(mRecFile);
//        listView.setBackgroundColor(Color.WHITE);
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//            public void create(SwipeMenu menu) {
//                // create "open" item
//                if(menu.getViewType() == 1) {       // SectionItem이 아닐 경우
//                    SwipeMenuItem openItem = new SwipeMenuItem(
//                            getApplicationContext());
//                    // set item background
//                    openItem.setBackground(new ColorDrawable(Color.rgb(0xCD, 0xCE,
//                            0xCF)));
//                    // set item width
//                    openItem.setWidth(dp2px(80));
//                    // set a icon
//                    openItem.setIcon(R.drawable.ic_share);
//                    // add to menu
//                    menu.addMenuItem(openItem);
//
//                    // create "description" item
//                    SwipeMenuItem descriptItem = new SwipeMenuItem(
//                            getApplicationContext());
//                    // set item background
//                    descriptItem.setBackground(new ColorDrawable(Color.rgb(0xBB, 0xBC,
//                            0xBE)));
//                    // set item width
//                    descriptItem.setWidth(dp2px(80));
//                    // set a icon
//                    descriptItem.setIcon(R.drawable.ic_modify);
//                    // add to menu
//                    menu.addMenuItem(descriptItem);
//
//                    // create "delete" item
//                    SwipeMenuItem deleteItem = new SwipeMenuItem(
//                            getApplicationContext());
//                    // set item background
//                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xA8, 0xAA,
//                            0xAD)));
//                    // set item width
//                    deleteItem.setWidth(dp2px(80));
//                    // set a icon
//                    deleteItem.setIcon(R.drawable.ic_delete);
//                    // add to menu
//                    menu.addMenuItem(deleteItem);
//                }
//            }
//        };
//
//        // set creator
//        listView.setMenuCreator(creator);
//
//        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                if (!SectionItem.class.isInstance(list.get(position))) {
//                    final RecFile item = (RecFile) list.get(position);
//                    switch (index) {
//                        case 0:
//                            // open
//                            // 공유하기 (암시적인 인텐트 - 액션만 지정(보내기만 함))
//
//                            RecFile newItem = new RecFile();
//                            newItem = dba.getRecFile(item.getRId());
//                            rName = newItem.getrName();
//                            rPath = newItem.getPath();
//
//                            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
////                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(rPath));
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + rPath));
//                            shareIntent.setType("video/mp4");
//                            startActivity(Intent.createChooser(shareIntent, "공유하기"));
////                            Toast.makeText(getApplicationContext(), "file://" + rPath, Toast.LENGTH_SHORT).show();
////공유하기 2번 나오는 거
////                        shareIntent.putExtra("exit_on_sent", true);
////                        shareIntent.putExtra("sms_body", "MMS 테스트입니다.");
////                        startActivity(Intent.createChooser(shareIntent, "How do you want to send message?"));
//
//                            break;
//                        case 1:
//                            //DB 상
//                            RecFile rec = (RecFile) list.get(position);
//                            RecordInfoFragment infoFragment = RecordInfoFragment.newInstance_list(4, rec, list, position);
//                            infoFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
//                            infoFragment.show(getFragmentManager(), "TAG");
//                            break;
//
//                        case 2:
//                            //DB 상
//                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ListItemActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//                            alert_confirm.setMessage("파일을 삭제 하시겠습니까? \n 삭제 시 녹음파일을 복구할 수 없습니다.").setCancelable(false).setPositiveButton("확인",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // 'YES'
//                                            //DB 상
//                                            Log.d("ddd", "" + item.getRecDate());
//                                            dba.deleteRecFile(item.getRId());
//
//                                            //리스트 상
//                                            list.remove(position);
//                                            mRecFile.notifyDataSetChanged();
//                                            //mRecFile2.notifyDataSetChanged();
//
//                                        }
//                                    }).setNegativeButton("취소",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // 'No'
//                                            dialog.cancel();
//                                        }
//                                    });
//                            AlertDialog alert = alert_confirm.create();
//                            alert.show();
//                            break;
//                    }
//                    return false;
//                } else {
//                    return false;
//                }
//            }
//        });
//        //media 재생
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (!SectionItem.class.isInstance(list.get(position))) {
//                    RecFile rec = (RecFile) list.get(position);
//                    MediaDialogFragment dialogFragment = MediaDialogFragment.newInstance(3, rec);
//                    dialogFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
//                    dialogFragment.show(getFragmentManager(), "TAG");
//                } else {
//                    view.setClickable(false);
//                    view.setLongClickable(false);
//                }
//            }
//        });
        recyclerView.setHasFixedSize(false);

        mRecFile = new ListItemAdapter(getApplicationContext(), list, dba, getFragmentManager(), ListItemActivity.this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRecFile);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        RecordApplication.addExpandableFeatures(recyclerView);
//                        recyclerView.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    protected void onResume() {
        super.onResume();
        //Log.d("arg_String : ", arg_String);
        Cursor search;
        if (arg_String.equals("전체")) {
            search = dba.getAllItem();
        } else {
            search = dba.getRecBySearch(arg_String);
        }

        // Toast.makeText(getApplicationContext(), search.getCount() + "", Toast.LENGTH_SHORT).show();

        list.clear();
        String temp = "temp"; // 이전 데이터의 Date 값 저장 (비교 위해서) , 맨첨은 임의의 값
        int prevTitlePosition = 0;
        int static_position = 0;

        int childCount = 0;

        while (search.moveToNext()) { // DB에서 해당 캐리어에 담긴 ITEM 들을 불러모음
            RecFile newItem = new RecFile();

            newItem.setsName(search.getString(5));
            newItem.setRecTime(search.getInt(2));
            newItem.setRecDate(search.getString(1));
            newItem.setrName(search.getString((4)));
            newItem.setPath(search.getString(6));
            newItem.setRId(search.getInt(0));

            if ((!newItem.getRecDate().equals(temp)) || (list.size() == 0)) { //이전 Date 값과 다르면..! 또는 맨 처음에 나온 것이라면?
                if((list.size() == 0) && (isEqual(newItem.getRecDate()))){
                    list.add(new SectionItem(0, "Today"));//리스트에 섹션 아이템 저장
                }
                else {
                    list.add(new SectionItem(0, newItem.getRecDate()));//리스트에 섹션 아이템 저장

                    SectionItem forInputNumber = (SectionItem) list.get(prevTitlePosition);
                    forInputNumber.setItemNumber(childCount);

                    // 데이터 리셋
                    childCount = 0;
                    prevTitlePosition = list.size() - 1;
                }

                temp = newItem.getRecDate();                    // 비교값 갱신
            }

            list.add(newItem);

            static_position++;

            childCount++;
        }

        listUpDate(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_item, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint("파일 이름을 적으세요");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                arg_String = arg0;
                Cursor search = dba.getRecBySearch(arg0);
                Log.d("arg_String : ", arg_String + "   " + arg0);
                // Toast.makeText(getApplicationContext(), arg0 + search.getCount() + "", Toast.LENGTH_SHORT).show();

                list.clear();


                String temp = "temp";
                while (search.moveToNext()) { // DB에서 해당 캐리어에 담긴 ITEM 들을 불러모음
                    RecFile newItem = new RecFile();

                    newItem.setsName(search.getString(5));
                    newItem.setRecTime(search.getInt(2));
                    newItem.setRecDate(search.getString(1));
                    newItem.setrName(search.getString((4)));
                    newItem.setPath(search.getString(6));
                    newItem.setRId(search.getInt(0));


                    if ((!newItem.getRecDate().equals(temp)) || (list.size() == 0)) { //이전 Date 값과 다르면..! 또는 맨 처음에 나온 것이라면?
                        if((list.size() == 0) && (isEqual(newItem.getRecDate()))){
                            list.add(new SectionItem(0, "Today"));//리스트에 섹션 아이템 저장
                        }
                        else {
                            list.add(new SectionItem(0, newItem.getRecDate()));//리스트에 섹션 아이템 저장
                        }

                        temp = newItem.getRecDate();                    // 비교값 갱신
                    }

                    list.add(newItem);
                }

                listUpDate(list);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                return false;
            }
        });
        // 검색필드를 항상 표시하고싶을 경우false, 아이콘으로 보이고 싶을 경우 true
        return true;
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (dtToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id ==  R.id.action_record){
            Intent intent3 = new Intent(ListItemActivity.this, MainActivity.class);
            startActivity(intent3);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        dtToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        dtToggle.onConfigurationChanged(newConfig);
//    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String qStr = subList.get(position);
        Cursor search;
        if (position == 0) {
            search = dba.getAllItem();
        } else {
            search = dba.getRecBySub(qStr);
        }

        list.clear();
        String temp = "temp"; // 이전 데이터의 Date 값 저장 (비교 위해서) , 맨첨은 임의의 값

        while (search.moveToNext()) { // DB에서 해당 캐리어에 담긴 ITEM 들을 불러모음
            RecFile newItem = new RecFile();

            newItem.setsName(search.getString(5));
            newItem.setRecTime(search.getInt(2));
            newItem.setRecDate(search.getString(1));
            newItem.setrName(search.getString((4)));
            newItem.setPath(search.getString(6));
            newItem.setRId(search.getInt(0));

            if ((!newItem.getRecDate().equals(temp)) || (list.size() == 0)) { //이전 Date 값과 다르면..! 또는 맨 처음에 나온 것이라면?
                if((list.size() == 0) && (isEqual(newItem.getRecDate()))){
                    list.add(new SectionItem(0, "Today"));//리스트에 섹션 아이템 저장
                }
                else {
                    list.add(new SectionItem(0, newItem.getRecDate()));//리스트에 섹션 아이템 저장
                }
                temp = newItem.getRecDate();                    // 비교값 갱신
            }
            list.add(newItem);
        }
        listUpDate(list);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public boolean isEqual(String targetDate){
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return todayDate.equals(targetDate);
    }

    public void getDialogValue(ArrayList arrList){
        mRecFile = new ListItemAdapter(getApplicationContext(), arrList, dba, getFragmentManager(), ListItemActivity.this);
        recyclerView.setAdapter(mRecFile);
    }


    /* for font */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
