package kr.co.yapp.campusrecorder.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.co.yapp.campusrecorder.Adapter.RecordItemAdapter;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.Dialog.MediaDialogFragment;
import kr.co.yapp.campusrecorder.Dialog.RecordDialogFragment;
import kr.co.yapp.campusrecorder.Dialog.RecordInfoFragment;
import kr.co.yapp.campusrecorder.R;

//주석
@SuppressLint("SimpleDateFormat")
public class MainActivity extends ActionBarActivity {
    private CaldroidFragment caldroidFragment;

    public static int startCount = 0;

    String rName;
    String rPath;

    private DBAdapter dba;
    private Context ctx = this;
    private LinearLayout recfileSubject;
    private LayoutInflater inflater;

    private ListView _list;
    private List<RecFile> list;
    private RecordItemAdapter mRecFile;
    private Date oldselectDate1, selectDate1, fSelectDate1;

    //private DrawerLayout dlDrawer;
    Toolbar toolbar;
//    DrawerLayout dlDrawer;
//    ActionBarDrawerToggle dtToggle;
    ImageButton plus;

    //private DrawerLayout mDrawerLayout;
    //private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    //private DrawerArrowDrawable drawerArrow;
    // private boolean drawerArrowColor;
    private LinearLayout mDrawer;
    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
        Intent receivedIntent = getIntent();
        startCount = receivedIntent.getIntExtra("startCount", startCount);
        Log.d("TAG", "startCount = " + startCount);
        mInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.animation_leave);

        if (startCount == 0) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            startCount++;
        }

        setContentView(R.layout.activity_main);
        init();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.main_color);

//        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //이거만 추가시키면 앞에 아이콘 생김! (여기서는 네비게이션 드로우어)
//
//
//        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name) {
//
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
//                dlDrawer.closeDrawers();
//            }
//        });
//
//        navi_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent3 = new Intent(MainActivity.this, ListItemActivity.class);
//                startActivity(intent3);
//                dlDrawer.closeDrawers();
//                finish();
//            }
//        });
//
//        navi_help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlDrawer.closeDrawers();
//            }
//        });






        //


        plus = (ImageButton) findViewById(R.id.button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordDialogFragment dialogFragmentR = RecordDialogFragment.newInstance();
                dialogFragmentR.setStyle(R.style.Theme_Dialog_Transparent2, R.style.Theme_Dialog_Transparent2);
                dialogFragmentR.show(getFragmentManager(), "TAG");
            }
        });

        //findViewById(할당)
        _list = (ListView) findViewById(R.id.listView);
        //임시용으로


        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
//		 caldroidFragment = new CaldroidSampleCustomFragment();


        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        CaldroidFragment.selectedTextColor = getResources().getColor(R.color.white);

        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        //처음 실행 시, 오늘 날짜 클릭 활성화 된 상태로 (오늘 날짜 파란색, 파일들 나옴)
        Calendar cal = Calendar.getInstance();
        int fFirst = Calendar.DATE;

        cal.add(fFirst, 0);
        fSelectDate1 = cal.getTime();

        cal.add(fFirst, 0);
        Date fSelectDate2 = cal.getTime();


//        caldroidFragment.setBackgroundResourceForDate(R.drawable.ic_launcher, new Date());



        caldroidFragment.setSelectedDates(fSelectDate1, fSelectDate2);
        CaldroidFragment.selectedBackgroundDrawable = R.color.caldroid_selected_Background_color;
        //caldroidFragment.setTextColorForDate(R.color.caldroid_selected_Text_color,fSelectDate1);
        caldroidFragment.refreshView();
        listUpdate(fSelectDate1);

        // 달력 날짜 click 시
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                caldroidFragment.clearSelectedDates();
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(date);

                int first = Calendar.DATE;

                int count = 0;

                if (!cal2.after(cal)) {
                    while (!cal2.after(cal)) {
                        count++;
                        cal2.add(first, 1);
                    }
                    count--;
                } else if (!cal2.before(cal)) {
                    while (!cal2.before(cal)) {
                        count--;
                        cal.add(first, 1);
                    }
                }

                first = Calendar.DATE;

//                               //이전 글씨 리셋
//                caldroidFragment.setTextColorForDate(R.color.caldroid_text_color, selectDate1);

                cal = Calendar.getInstance();
                cal.add(first, -count);
                selectDate1 = cal.getTime();

                cal.add(first, 0);
                Date selectDate2 = cal.getTime();
                caldroidFragment.setSelectedDates(selectDate1, selectDate2);


                Cursor cursor2 = dba.getAllDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (cursor2.moveToNext()){
                    String dateStr =cursor2.getString(0);
                    Log.d("Date Test",dateStr);

                    try {
                        Date markedDate = sdf.parse(dateStr);
                        String temp_str1 = markedDate.getYear()+""+markedDate.getMonth()+""+markedDate.getDate();
                        String temp_str2 = selectDate1.getYear()+""+selectDate1.getMonth()+""+selectDate1.getDate();
                        Log.d("Date Compare : ", temp_str1 + " == " + temp_str2);
                        if(temp_str1.equals(temp_str2)) {
                            Log.d("겹치는 경우", temp_str1);
                            //여기다 어떻게 해주어야 색깔이 바뀔까?
                            caldroidFragment.setBackgroundResourceForDate(R.color.black, markedDate);
                        }
                        else {
                            Log.d("아닌 경우", temp_str2);
                            caldroidFragment.setBackgroundResourceForDate(R.drawable.bg_corner_triangle, markedDate);
                            //caldroidFragment.setBackgroundResourceForDate(R.drawable.mark,markedDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                CaldroidFragment.selectedBackgroundDrawable = R.color.caldroid_selected_Background_color;
                caldroidFragment.refreshView();

                listUpdate(selectDate1);

            }
//
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
        listener.onSelectDate(new Date(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume()", "onResume 실행");
        listUpdate(new Date());
    }

    public void listUpdate(Date selectDate1) {
        list = new ArrayList<RecFile>();
        RecFile item = new RecFile();

        //해당 날짜와 매치되는 녹음 파일들을 불러옴
        Cursor cursor = dba.getRecByDate(selectDate1);
        list = new ArrayList<RecFile>();
        Log.d("Test", cursor.getCount() + " " + selectDate1.getYear() + " " + selectDate1.getDate());
        while (cursor.moveToNext()) { // DB에서 해당 캐리어에 담긴 ITEM 들을 불러모음
            RecFile newItem = new RecFile();
            newItem.setsName(cursor.getString(5));
            newItem.setRecTime(cursor.getInt(2));
            newItem.setRecDate(cursor.getString(1));
            newItem.setrName(cursor.getString((4)));
            newItem.setPath(cursor.getString(6));
            newItem.setRId(cursor.getInt(0));
            list.add(newItem);
        }

        TextView temp = (TextView) findViewById(R.id.temp);
        if (list.isEmpty() == true)
            temp.setText("녹음 파일이 없습니다.");
        else
            temp.setText("녹음 파일이 없습니다.");

        mRecFile = new RecordItemAdapter(getApplicationContext(), R.layout.item_list, list);
        _list.setAdapter(mRecFile);
        _list.setBackgroundColor(Color.WHITE);
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xCD, 0xCE,
//                        0xCF)));
//
//                // set item width
//                openItem.setWidth(dp2px(80));
//                // set a icon
//                openItem.setIcon(R.drawable.ic_share);
//                // add to menu
//                menu.addMenuItem(openItem);
//
//                // create "description" item
//                SwipeMenuItem descriptItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                descriptItem.setBackground(new ColorDrawable(Color.rgb(0xBB, 0xBC,
//                        0xBE)));
//                // set item width
//                descriptItem.setWidth(dp2px(80));
//                // set a icon
//                descriptItem.setIcon(R.drawable.ic_modify);
//                // add to menu
//                menu.addMenuItem(descriptItem);
//
//                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getApplicationContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xA8, 0xAA,
//                        0xAD)));
//                // set item width
//                deleteItem.setWidth(dp2px(80));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };

//        // set creator
//        _list.setMenuCreator(creator);
//        //
//        _list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        // open
//                        // 공유하기 (암시적인 인텐트 - 액션만 지정(보내기만 함))
//
//                        RecFile newItem = new RecFile();
//
//                        newItem = dba.getRecFile(list.get(position).getRId());
//                        rName = newItem.getrName();
//                        rPath = newItem.getPath();
//
//                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
////                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(rPath));
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + rPath));
//                        shareIntent.setType("video/mp4");
//                        startActivity(Intent.createChooser(shareIntent, "공유하기"));
//                        //Toast.makeText(getApplicationContext(), "file://" + rPath, Toast.LENGTH_SHORT).show();
////SMS인 경우 (두 번 공유하기가 나와 수정)
////                        shareIntent.putExtra("exit_on_sent", true);
////                        shareIntent.putExtra("sms_body", "MMS 테스트입니다.");
////                        startActivity(Intent.createChooser(shareIntent, "How do you want to send message?"));
//
//                        break;
//                    case 1:
//                        //DB 상
//                        RecFile rec = list.get(position);
////                        RecordApplication.temp_position = position;
//                        RecordInfoFragment infoFragment = RecordInfoFragment.newInstance(4, rec, list, position);
//                        infoFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
//                        infoFragment.show(getFragmentManager(), "TAG");
//                        break;
//
//                    case 2:
//                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//                        alert_confirm.setMessage("파일을 삭제 하시겠습니까? \n 삭제 시 녹음파일을 복구할 수 없습니다.").setCancelable(false).setPositiveButton("확인",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 'YES'
//                                        //DB 상
//                                        Log.d("ddd", "" + list.get(position).getRecDate());
//                                        dba.deleteRecFile(list.get(position).getRId());
//
//                                        //리스트 상
//                                        list.remove(position);
//                                        mRecFile.notifyDataSetChanged();
//
//                                    }
//                                }).setNegativeButton("취소",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 'No'
//                                        dialog.cancel();
//                                    }
//                                });
//                        AlertDialog alert = alert_confirm.create();
//                        alert.show();
//                        break;
//
//                }
//                return false;
//            }
//        });

        //media 재생
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecFile rec = list.get(position);
                MediaDialogFragment dialogFragment = MediaDialogFragment.newInstance(3, rec);
                dialogFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
                dialogFragment.show(getFragmentManager(), "TAG");
            }
        });


        _list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            AlertDialog dialog;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // TODO Auto-generated method stub
                // 다이얼로그 생성
                final View alertView = mInflater.inflate(R.layout.item_alertdialog, null);
                alertView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecFile newItem = new RecFile();

                        newItem = dba.getRecFile(list.get(position).getRId());
                        rName = newItem.getrName();
                        rPath = newItem.getPath();

                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(rPath));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + rPath));
                        shareIntent.setType("video/mp4");
                        startActivity(Intent.createChooser(shareIntent, "공유하기"));
                        //Toast.makeText(getApplicationContext(), "file://" + rPath, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                // open
                // 공유하기 (암시적인 인텐트 - 액션만 지정(보내기만 함))

                alertView.findViewById(R.id.modify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DB 상
                        RecFile rec = list.get(position);
                        RecordInfoFragment infoFragment = RecordInfoFragment.newInstance(4, rec, list, position);
                        infoFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
                        infoFragment.show(getFragmentManager(), "TAG");
                        dialog.dismiss();
                    }
                });
                alertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

                        alert_confirm.setMessage("파일을 삭제 하시겠습니까? \n 삭제 시 녹음파일을 복구할 수 없습니다.").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 'YES'
                                        //DB 상
                                        Log.d("ddd", "" + list.get(position).getRecDate());
                                        dba.deleteRecFile(list.get(position).getRId());

                                        //리스트 상
                                        list.remove(position);
                                        mRecFile.notifyDataSetChanged();

                                    }
                                }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 'No'
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = alert_confirm.create();
                        alert.show();
                        dialog.dismiss();
                    }
                });

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("");
                ab.setView(alertView);
                dialog = ab.show();

                return true;
            }
        });
    }


    /**
     * 현재 액티비티가 다시 포커스를 받을때
     * DB에서 캐리어 꺼내서 표시
     */

    public void init() {
        dba = new DBAdapter(getApplicationContext());
        dba = dba.open();
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String text = null;
//        if (dtToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                text = "Application icon";
                break;

            case R.id.action_record:
                //액션버튼, Record 액션(마이크버튼) 을 눌렀을때
                Intent intent3 = new Intent(MainActivity.this, ListItemActivity.class);
                startActivity(intent3);
                finish();

            default:
                return false;
        }
        return true;
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

    public void getDialogValue(List<RecFile> list){
        mRecFile = new RecordItemAdapter(getApplicationContext(), R.layout.item_list, list);
        _list.setAdapter(mRecFile);
    }


    /* for font */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}