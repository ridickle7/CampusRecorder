package kr.co.yapp.campusrecorder.Lib;

import android.app.Activity;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import kr.co.yapp.campusrecorder.R;

/**
 * Created by ridickle on 2016. 11. 14..
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
//    private Toast toast;
    private SuperToast superToast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
//            toast.cancel();
        }
    }

    public void showGuide() {
//        toast = Toast.makeText(activity,
//                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
//        toast.show();

        superToast = new SuperToast(activity, Style.getStyle(Style.RED));
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setText("\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.");
        superToast.setIcon(R.drawable.ic_launcher, SuperToast.IconPosition.LEFT);
        superToast.show();
    }
}
