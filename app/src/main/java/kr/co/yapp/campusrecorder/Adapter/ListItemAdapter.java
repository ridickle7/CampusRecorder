package kr.co.yapp.campusrecorder.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Data.RecFile;
import kr.co.yapp.campusrecorder.Data.SectionItem;
import kr.co.yapp.campusrecorder.Data.listItem;
import kr.co.yapp.campusrecorder.Dialog.MediaDialogFragment;
import kr.co.yapp.campusrecorder.Dialog.RecordInfoFragment;
import kr.co.yapp.campusrecorder.R;

/**
 * Created by home on 2016-09-25.
 */
public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    Context ctx;
    private ArrayList data;
    LayoutInflater mInflate;
    DBAdapter dba;
    FragmentManager fragmentManager;
    Activity activity;

    public ListItemAdapter(Context ctx, ArrayList data, DBAdapter dba, FragmentManager fragmentManager, Activity activity) {
        this.ctx = ctx;
        this.data = data;
        mInflate = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dba = dba;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        float dp = ctx.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);

        switch (type) {
            case HEADER:
                view = mInflate.inflate(R.layout.text_section, parent, false);
                ListItemViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                view = mInflate.inflate(R.layout.item_list, null, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ListItemViewHolder child = new ListChildViewHolder(view);

                return child;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, final int position) {
        final listItem item = (listItem) data.get(position);
        if (item.getType() == 0) {
            final SectionItem headerItem = (SectionItem) item;
            final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
            itemController.header_title.setText(headerItem.getTitle());
            itemController.header_number.setText(headerItem.getItemNumber() + "");
        } else {
            final RecFile childItem = (RecFile) item;
            final ListChildViewHolder itemController = (ListChildViewHolder) holder;

            itemController.child_title.setText(childItem.getsName() + "-" + childItem.getrName());
            itemController.child_runTime.setText(makeTime(childItem.getRecTime()));
            itemController.child_date.setText(childItem.getRecDate());

            itemController.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaDialogFragment dialogFragment = MediaDialogFragment.newInstance(3, childItem);
                    dialogFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
                    dialogFragment.show(fragmentManager, "TAG");
                }
            });


            itemController.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                AlertDialog dialog;

                @Override
                public boolean onLongClick(View v) {

                    final View alertView = mInflate.inflate(R.layout.item_alertdialog, null);
                    alertView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            RecFile newItem = dba.getRecFile(childItem.getRId());
                            String rName = newItem.getrName();
                            String rPath = newItem.getPath();

                            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                            //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(rPath));
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + rPath));
                            shareIntent.setType("video/mp4");
                            activity.startActivity(Intent.createChooser(shareIntent, "공유하기"));
//                            Toast.makeText(getApplicationContext(), "file://" + rPath, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    // open
                    // 공유하기 (암시적인 인텐트 - 액션만 지정(보내기만 함))

                    alertView.findViewById(R.id.modify).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //DB 상
                            RecordInfoFragment infoFragment = RecordInfoFragment.newInstance_list(4, childItem, data, position);
                            infoFragment.setStyle(R.style.Theme_Dialog_Transparent, R.style.Theme_Dialog_Transparent);
                            infoFragment.show(fragmentManager, "TAG");
                            dialog.dismiss();
                        }
                    });

                    alertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            alert_confirm.setMessage("파일을 삭제 하시겠습니까? \n 삭제 시 녹음파일을 복구할 수 없습니다.").setCancelable(false).setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 'YES'
                                            //DB 상
                                            Log.d("ddd", "" + childItem.getRecDate());
                                            dba.deleteRecFile(childItem.getRId());

                                            //리스트 상
                                            data.remove(position);
                                        }
                                    }).setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 'No'
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                            dialog.dismiss();
                        }
                    });

                    AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                    ab.setTitle("");
                    ab.setView(alertView);
                    dialog = ab.show();

                    return true;
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        listItem temp = (listItem) data.get(position);
        return temp.getType();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ListHeaderViewHolder extends ListItemViewHolder {
        public TextView header_title, header_number;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.textSeparator);
            header_number = (TextView) itemView.findViewById(R.id.textNumber);
        }
    }

    public class ListChildViewHolder extends ListItemViewHolder {
        TextView child_title;
        TextView child_runTime;
        TextView child_date;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.list_title);
            child_runTime = (TextView) itemView.findViewById(R.id.list_runtime);
            child_date = (TextView) itemView.findViewById(R.id.list_recordtime);
        }
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        public ListItemViewHolder(View v) {
            super(v);
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
}


