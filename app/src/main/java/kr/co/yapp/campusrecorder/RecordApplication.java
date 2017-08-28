package kr.co.yapp.campusrecorder;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;

import com.tsengvn.typekit.Typekit;

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
    public static Context tempContext;

    public static String fileName;

    public static Typeface typeface;
    public static Typeface typeface_bold;

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

        typeface = Typekit.createFromAsset(this, "NotoSansKR-Regular-Hestia.otf");
        Typekit.getInstance()
                .addNormal(typeface)
                .addBold(Typekit.createFromAsset(this, "NotoSansKR-Black-Hestia.otf"));

        typeface_bold = Typeface
                .createFromAsset(this.getAssets(), "NotoSansKR-Black-Hestia.otf");


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

    public static void setRecordActivityContext(Context ctx){
        tempContext = ctx;
    }
}
