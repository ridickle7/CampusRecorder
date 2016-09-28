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

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import kr.co.yapp.campusrecorder.Activity.CategoryActivity;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Lib.StyledTextView;
import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.Activity.RecordActivity;
import kr.co.yapp.campusrecorder.RecordApplication;

/**dd
 * Created by BH on 2015-02-13.
 */
public class RecordDialogFragment extends DialogFragment {
    private static final int RESULT_OK = 1;
    LinearLayout cancleButton,OkButton;
    EditText editName,editInfo,editFile;
    Button btn;
    String rName;
    DBAdapter dba;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_save,container,false);



        cancleButton = (LinearLayout)v.findViewById(R.id.dialog_layout_button_cancel);
        OkButton = (LinearLayout)v.findViewById(R.id.dialog_layout_button_ok);
        editName = (EditText)v.findViewById(R.id.edit_name);
        btn = (Button)v.findViewById(R.id.btn_sub_plus);
        dba = new DBAdapter(getActivity().getApplicationContext());
        dba = dba.open();


        /**
         * TODO 분류추가 (Button 클릭 시)
         * 다이얼로그 생성 -> 디비에 추가
         */


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),CategoryActivity.class);
                startActivityForResult(intent, 1);
            }
        });




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
                StyledTextView tv = (StyledTextView)v.findViewById(R.id.btn_reg_OK);

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    tv.setTextColor(Color.WHITE);
                }else if(event.getAction() == MotionEvent.ACTION_UP){

                    tv.setTextColor(colorRed);
                }

                return false;
            }
        });

        //확인 버튼
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editName.getText().toString();
                String sub = btn.getText().toString();
                if(TextUtils.isEmpty(str)) {
                    SuperToast superToast = new SuperToast(getActivity(), Style.getStyle(Style.RED));
                    superToast.setDuration(SuperToast.Duration.SHORT);
                    superToast.setText("파일의 이름이 입력되지 않았습니다");
                    superToast.setIcon(R.drawable.ic_launcher, SuperToast.IconPosition.LEFT);
                    superToast.show();
                }
                else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), RecordActivity.class);
                    RecordApplication.fileName = sub + "-" + str;
                    intent.putExtra("rName", str);
                    intent.putExtra("sName", sub);
                    startActivity(intent);

                    dismiss();
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode",resultCode+"");
        Log.d("requestCode",requestCode+"");
        if(resultCode==RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if(requestCode==1) // MemoInputActivity에서 호출한 경우에만 처리합니다.
            {				   // 받아온 정보로 db를 조회하고 화면을 갱신합니다.
                btn.setText(data.getStringExtra("data_name"));
            }
        }
    }

    public static RecordDialogFragment newInstance(){
        RecordDialogFragment fragment = new RecordDialogFragment();
        return fragment;
    }


}
