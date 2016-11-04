package kr.co.yapp.campusrecorder.Dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;

import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.Data.RecFile;


/**
 * Created by BH on 2015-02-14.
 * TODO 예외 처리, ON/OFF 토글 변경
 */
public class MediaDialogFragment extends DialogFragment {

    LinearLayout cancleButton, OkButton;
    String rName;
    DiscreteSeekBar progress;
    Context ctx = getActivity();
    TextView tvStart;

    MediaPlayer m;
    Button playBtn;
    private int flag = 0;
    private int current = 0;
    private int duration = 0;
    private boolean running = true;
    private RecFile item;
    private TextView tvTitle, tvDate;
    ToggleButton play;
    private int state = 0;
    private int playLength = 0;
    static final int STATE_STOP = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_PAUSE = 2;
    int resetFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media, container, false);

        cancleButton = (LinearLayout) v.findViewById(R.id.dialog_layout_button_cancel);
        OkButton = (LinearLayout) v.findViewById(R.id.dialog_layout_button_ok);
        tvTitle = (TextView) v.findViewById (R.id.tv_media_title);
        tvDate = (TextView) v.findViewById(R.id.tv_media_date);
        tvStart = (TextView) v.findViewById(R.id.startTime);

        tvTitle.setText(item.getsName() + "-" + item.getrName());
        tvDate.setText(item.getRecDate());
        tvStart.setText("00:00:00 / "+makeTime(item.getRecTime()));

        progress = (DiscreteSeekBar) v.findViewById(R.id.progress2);
        progress.setMin(0);
        playBtn = (Button) v.findViewById(R.id.btn_play);
        play = (ToggleButton) v.findViewById(R.id.btn_play);
        m = new MediaPlayer();

        try {
            m.setDataSource(item.getPath());
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final DiscreteSeekBar progress2 = (DiscreteSeekBar) v.findViewById(R.id.progress2);
        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration();
                progress.setProgress(0);
                progress.setMax(duration);
                progress.postDelayed(onEverySecond, 1000);
            }
        });

        play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("플래그:",""+flag);

                    if (!isChecked) {//정지상태
                        if (state == STATE_STOP) {//정지 상태 : 처음 부터 시작
                            state = STATE_RUNNING;
                            m.start();
                            progress.postDelayed(onEverySecond, 1000);

                        } else {//일시 정지 상태 : 중간부터 시작
                            state = STATE_RUNNING;
                            playLength = m.getCurrentPosition();
                            m.seekTo(playLength);
                            m.start();
                            progress.postDelayed(onEverySecond, 1000);
                        }
                    } else {//재생 중 -> 일시 정지
                        m.pause();
                        playLength = m.getCurrentPosition();
                        state = STATE_PAUSE;
                    }
            }
        });
/*        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                state = STATE_STOP;
                play.setChecked(true);
            }
        });*/


/*
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    m.setDataSource(item.getPath());
                    m.prepare();
                    m.start();
                    progress.postDelayed(onEverySecond, 1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/


        progress.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                //일단 여기서 seekBar 문제!!! -> if(fromUser) -> true -> mseekTo(value(1)) -> updateTime() -> ;
                if (fromUser) {
                    m.seekTo(value);
                    updateTime();
                } else {
                    if (value >= seekBar.getMax()) {
                        play.setChecked(true);
                        //   m.stop();
                        //    m.release();
                        m.seekTo(0);
                        progress.setProgress(0);
                        state = STATE_STOP;
                    }
                Log.d("노래 전체 길이 : ", "" + item.getRecTime());
                }
            }
        });


        cancleButton.setOnClickListener(new View.OnClickListener()

                                        {

                                            @Override
                                            public void onClick(View v) {
                                                dismiss();
                                            }
                                        }

        );

        //확인 버튼
        OkButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                dismiss();
            }

        });
        return v;
    }

    @Override
    public void onDestroy() {
        m.stop();
        super.onDestroy();

    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (true == running) {
                if (progress != null) {
                    progress.setProgress(m.getCurrentPosition());
                }

                if (m.isPlaying()) {
                    progress.postDelayed(onEverySecond, 1000);
                    updateTime();
                }

                int total = item.getRecTime() - current/1000;
                Log.d("뺄셈 : ", ""+total);

                if(total == 1 && resetFlag == 0){
                    Log.d("지금은 1초이다 : ", "" + resetFlag);
                    resetFlag = 1;
                }

                else if((total == 1 && resetFlag == 1) || total == 0){
                    Log.d("1초가 2번 나왔다. : ", "" + resetFlag);
                    m.pause();
                    m.seekTo(0);
                    progress.setProgress(0);
                    resetFlag = 0;
                    state = STATE_STOP;
                    play.setChecked(true);                      //Log.d("진실",""+play.isChecked());
                    tvStart.setText("00:00:00 / "+makeTime(item.getRecTime()));
                }
            }
        }
    };

    private void updateTime() {
        do {
            current = m.getCurrentPosition();
            System.out.println("duration - " + duration + " current- "
                    + current);
            int dSeconds = duration / 1000 % 60;
            int dMinutes = (duration / (1000 * 60)) % 60;
            int dHours = (duration / (1000 * 60 * 60)) % 24;

            int cSeconds = current / 1000 % 60;
            int cMinutes = (current / (1000 * 60)) % 60;
            int cHours = (current / (1000 * 60 * 60)) % 24;

            if (dHours == 0) {
                tvStart.setText(String.format("00:%02d:%02d / 00:%02d:%02d", cMinutes, cSeconds, dMinutes, dSeconds));
            } else {
                tvStart.setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", cHours, cMinutes, cSeconds, dHours, dMinutes, dSeconds));
            }

            try {
                //  Log.d("Value: ", String.valueOf((int) (current * 100 / duration)));
                if (progress.getProgress() >= 100) {
                    break;
                }
            } catch (Exception e) {
            }
        } while (progress.getProgress() < 0);
    }

    public static MediaDialogFragment newInstance(int arg, RecFile item) {
        MediaDialogFragment fragment = new MediaDialogFragment();
        Bundle args = new Bundle();
        args.putInt("count", arg);
        fragment.setArguments(args);
        fragment.setComplexVariable(item);
        return fragment;
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

    public void setComplexVariable(RecFile item) {
        this.item = item;
    }
}