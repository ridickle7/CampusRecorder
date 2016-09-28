package kr.co.yapp.campusrecorder.Dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.yapp.campusrecorder.Activity.CategoryActivity;
import kr.co.yapp.campusrecorder.Activity.ListItemActivity;
import kr.co.yapp.campusrecorder.Activity.MainActivity;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.Lib.StyledTextView;
import kr.co.yapp.campusrecorder.R;

/**
 * 녹음 정보 , 수정, 확인
 * Created by BH on 2015-05-21
 */
public class RecordInfoFragment extends DialogFragment {
    private static final int RESULT_OK = 1;
    LinearLayout cancleButton, OkButton;

    String rName;
    DBAdapter dba;
    static List<RecFile> arrList = null;
    static ArrayList arrList2 = null;
    static int pos;

    private Button editCategory;
    private EditText editRecord;
    private TextView textFile, textTime, textByte,textTitle;
    private RecFile item;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        dba = new DBAdapter(getActivity().getApplicationContext());
        dba = dba.open();

        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //Soft 키 재조정, 근데 다이얼로그가 길어서 필요업씀


        editRecord = (EditText) v.findViewById(R.id.info_edit_name);
        editCategory = (Button) v.findViewById(R.id.info_edit_sub);
        textFile = (TextView) v.findViewById(R.id.info_text_file);
        textTime =(TextView) v.findViewById(R.id.info_text_time);
        textTitle = (TextView)v.findViewById(R.id.info_title);
        textByte = (TextView) v.findViewById(R.id.info_text_byte);
        cancleButton = (LinearLayout)v.findViewById(R.id.btn_info_no);
        OkButton = (LinearLayout)v.findViewById(R.id.btn_info_OK);

        //텍스트 할당

        //파일 경로 marguee 추가
       textFile.setText(item.getPath());
       textFile.setEllipsize(TextUtils.TruncateAt.MARQUEE);
       textFile.setMarqueeRepeatLimit(1000);
       textFile.setSelected(true);
        //파일 재생 시간
        textTime.setText(makeTime(item.getRecTime()));
        Log.d("TIME", item.getRecTime() + "");
        textByte.setText(item.getSize()+"MB");
        textTitle.setText(item.getsName()+" - "+item.getrName());

        editRecord.setText(item.getrName());
        editCategory.setText(item.getsName());

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CategoryActivity.class);
                startActivityForResult(intent, 1);
                //카테고리 수정
            }
        });

/*        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rname= editRecord.getText().toString();
                dba.updateName(item.getRId(),rname);
                item.setrName(rname);
                textTitle.setText(item.getsName()+" - "+item.getrName());
            }
        });*/


        //취소버튼
        cancleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        OkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int colorRed = getResources().getColor(R.color.main_color);
                StyledTextView tv = (StyledTextView) v.findViewById(R.id.tv_info_OK);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    tv.setTextColor(Color.WHITE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    tv.setTextColor(colorRed);
                }

                return false;
            }
        });
        //확인 버튼
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  //TODO DBA 처리 캔슬버튼 삭제 예정, 각 수정 버튼 -> db연결
                String rname= editRecord.getText().toString();
                dba.updateName(item.getRId(), rname);
                item.setrName(rname);
                textTitle.setText(item.getsName() + " - " + item.getrName());

                String sname = editCategory.getText().toString();
                dba.updateCategory(item.getRId(), sname);


                if(getActivity().getLocalClassName().equals("Activity.MainActivity")){
                    RecFile rec = arrList.get(pos);
                    rec.setrName(rname);
                    rec.setsName(sname);
                    MainActivity activity = (MainActivity)getActivity();
                    activity.getDialogValue(arrList);
                }
                else{
                    RecFile rec = (RecFile)arrList2.get(pos);
                    rec.setrName(rname);
                    rec.setsName(sname);
                    ListItemActivity activity = (ListItemActivity)getActivity();
                    activity.getDialogValue(arrList2);
                }

                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode", resultCode + "");
        Log.d("requestCode", requestCode + "");
        if (resultCode == RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1) // MemoInputActivity에서 호출한 경우에만 처리합니다.
            {                   // 받아온 정보로 db를 조회하고 화면을 갱신합니다.
               // btn.setText(data.getStringExtra("data_name"));
                String sname = data.getStringExtra("data_name");
                editCategory.setText(sname);
                item.setsName(sname);
                textTitle.setText(item.getsName() + " - " + item.getrName());
             //   dba.updateCategory(item.getRId(),sname);
            }
        }
    }

    public static RecordInfoFragment newInstance(int arg, RecFile item, List<RecFile> list, int position) {
        RecordInfoFragment fragment = new RecordInfoFragment();
        Bundle args = new Bundle();
        args.putInt("count",arg);
        arrList = list;
        pos = position;
        fragment.setArguments(args);
        fragment.setComplexVariable(item);
        return fragment;
    }


    public static RecordInfoFragment newInstance_list(int arg, RecFile item, ArrayList list, int position) {
        RecordInfoFragment fragment = new RecordInfoFragment();
        Bundle args = new Bundle();
        args.putInt("count",arg);
        arrList2 = list;
        pos = position;
        fragment.setArguments(args);
        fragment.setComplexVariable(item);
        return fragment;
    }

    public void setComplexVariable(RecFile item) {
        this.item = item;
    }

    public String makeTime(int second) {
        String str = "";
        String parsing = String.format("%02d", second / 3600);//64800 -> 10800
        str = str + parsing + ":";
        parsing = String.format("%02d", (second % 3600) / 60);
        str = str + parsing + ":";
        parsing = String.format("%02d", (second % 3600) % 60);
        str = str + parsing;
        return str;
    }

}
