package kr.co.yapp.campusrecorder.Dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import kr.co.yapp.campusrecorder.Adapter.CategoryItemAdapter;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.RecordApplication;

/**
 * Created by BH on 2015-02-13.
 */
public class SubAddDialogFragment extends DialogFragment {
    LinearLayout cancleButton,OkButton;
    EditText editName,editInfo,editFile;
    Button btn;
    String rName;
    DBAdapter dba;

    ListView categoryList;
    List<String> list;
    private CategoryItemAdapter cItem;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add,container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //Soft 키 등장시 사이즈 재조정
        cancleButton = (LinearLayout)v.findViewById(R.id.dialog_layout_button_cancel);
        OkButton = (LinearLayout)v.findViewById(R.id.dialog_layout_button_ok);
        editName = (EditText)v.findViewById(R.id.edit_name);


        //취소버튼
        cancleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //확인 버튼
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double fileSize = 0;
                String name = editName.getText().toString();
                RecordApplication app;
                app = (RecordApplication)getActivity().getApplicationContext();
                app.addSubject(name);

                list = app.getList();
                categoryList = (ListView) getActivity().findViewById(R.id.list);
                cItem = new CategoryItemAdapter(getActivity(),R.layout.item_category,list);
                categoryList.setAdapter(cItem);

                dismiss();
            }
        });
        return v;
    }
    public static SubAddDialogFragment newInstance(){
        SubAddDialogFragment fragment = new SubAddDialogFragment();
        return fragment;
    }


}
