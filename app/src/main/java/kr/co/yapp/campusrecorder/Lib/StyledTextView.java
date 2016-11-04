package kr.co.yapp.campusrecorder.Lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import kr.co.yapp.campusrecorder.R;

/**
 * Created by Administrator on 2015-09-10.
 */
public class StyledTextView extends TextView {

    public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        applyTypeface(context, attrs);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        applyTypeface(context, attrs);
    }

    public StyledTextView(Context context) {
        super(context);
    }

    private void applyTypeface(Context context, AttributeSet attrs){
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
        String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
        Typeface typeface = null;
        try{
            typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
            setTypeface(typeface);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}