package kr.co.yapp.campusrecorder;

import android.app.Application;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.yapp.campusrecorder.Data.DBAdapter;

/**
 * Created by BH on 2015-02-14.
 */
public class RecordApplication extends Application {
    private List<String> list;
    DBAdapter dbAdapter;
    public static int temp_position = 0;

    public static String fileName;

    @Override
    public void onCreate() {
        super.onCreate();
        dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.open();
        list = new ArrayList<String>();
        Cursor cursor = dbAdapter.getAllSubject();
        list.add("분류없음");
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }

    }
    public void addSubject(String subject){
       // dbAdapter.addSubject(subject);
        list.add(subject);
    }

    public List<String> getList() {
        return list;
    }


    public static void addExpandableFeatures(RecyclerView v) {
        v.getItemAnimator().setAddDuration(100);
        v.getItemAnimator().setRemoveDuration(100);
        v.getItemAnimator().setMoveDuration(200);
        v.getItemAnimator().setChangeDuration(100);
    }

}
