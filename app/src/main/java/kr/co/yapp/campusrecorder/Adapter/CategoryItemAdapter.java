package kr.co.yapp.campusrecorder.Adapter;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kr.co.yapp.campusrecorder.R;


public class CategoryItemAdapter extends ArrayAdapter<String> {


    private Context context;
    private int resource;
    private List<String> data;
    private LayoutInflater mInflater;
    public CategoryItemAdapter(Context context, int resource , List<String> objects) {
        super(context, resource,  objects);
        this.context=context;
        this.resource=resource;
        this.data=objects;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = mInflater.inflate(resource, null);
        }
        TextView category_item=(TextView)convertView.findViewById(R.id.category_item);
        category_item.setTextColor(context.getResources().getColor(R.color.textColor));
        category_item.setText(data.get(position).toString());

        return convertView;
    }
}
