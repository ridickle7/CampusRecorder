package kr.co.yapp.campusrecorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.Data.SectionItem;
import kr.co.yapp.campusrecorder.R;

/**
 * Created by BH on 2015-02-14.
 * <p/>
 * ListItemActivity 에서 섹션을 적용 할수 있는 어댑터
 */
public class SectionItemAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private int resource2;//안씀
    private ArrayList data;
    private LayoutInflater mInflater;

    public SectionItemAdapter(Context context, int resource, ArrayList objects) {

        super(context, resource, resource, objects);
        this.context = context;
        this.resource = resource;
        this.data = objects;
        this.resource2 = R.layout.text_section;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (SectionItem.class.isInstance(data.get(position))) {
            return 0;
        } else {
            return 1;
        }
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (SectionItem.class.isInstance(data.get(position))) {         // Item이 섹션아이템이면!
            SectionItem item = (SectionItem) data.get(position);        // SectionItem으로 캐스팅
            convertView  = mInflater.inflate(R.layout.text_section, null);    //뷰에 섹션 뷰 인플레이트
//                //터치 ㄴㄴ함
//                v.setOnTouchListener(null);
//                v.setOnLongClickListener(null);
//                v.setLongClickable(false);
//                v.setClickable(false);

            final TextView textSection = (TextView) convertView.findViewById(R.id.textSeparator);
//            textSection.setText(item.getTitle());    //섹션 테스트 뷰에 텍스트 입력
            textSection.setText(position+"");

            return convertView;
        } else { // 섹션 아이템이 아니면!                         -> RecFile이면
            RecFile ri = (RecFile) data.get(position);              //Recfile로 캐스팅
            convertView  = mInflater.inflate(R.layout.item_list, null);     //뷰에 섹션 뷰 인플레이트

            TextView tvTitle = (TextView) convertView.findViewById(R.id.list_title);
            TextView tvRunTime = (TextView) convertView.findViewById(R.id.list_runtime);
            TextView tvDate = (TextView) convertView.findViewById(R.id.list_recordtime);
            tvTitle.setText(ri.getsName() + "-" + ri.getrName());
            tvRunTime.setText(makeTime(ri.getRecTime()));
            tvDate.setText(ri.getRecDate());

            return convertView;
        }
    }
}
