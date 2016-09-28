package kr.co.yapp.campusrecorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.R;

/**
 * Created by BH on 2015-02-14.
 */
public class RecordItemAdapter extends ArrayAdapter<RecFile> {
    private Context context;
    private int resource;
    private List<RecFile> data;
    private LayoutInflater mInflater;
    public RecordItemAdapter(Context context, int resource , List<RecFile> objects) {
        super(context, resource,  objects);
        this.context=context;
        this.resource=resource;
        this.data=objects;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public String makeTime(int second){
        String str = "";
        String parsing = String.format("%02d", second/3600);//64800 -> 10800
        str = str + parsing + ":";
        parsing = String.format("%02d", (second%3600)/60);
        str = str + parsing + ":";
        parsing = String.format("%02d", (second%3600)%60);
        str = str + parsing;

        return str;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = mInflater.inflate(resource, null);
        }
        TextView tvTitle=(TextView)convertView.findViewById(R.id.list_title);
        TextView tvRunTime=(TextView)convertView.findViewById(R.id.list_runtime);
        TextView tvDate=(TextView)convertView.findViewById(R.id.list_recordtime);

        tvTitle.setText(data.get(position).getsName()+"-"+data.get(position).getrName());
        tvRunTime.setText(makeTime(data.get(position).getRecTime()));
        tvDate.setText(data.get(position).getRecDate());

        return convertView;
    }
}
