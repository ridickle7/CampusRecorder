package kr.co.yapp.campusrecorder.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

import kr.co.yapp.campusrecorder.IRecordAidlInterface;
import kr.co.yapp.campusrecorder.R;
import kr.co.yapp.campusrecorder.RecordService;
import kr.co.yapp.campusrecorder.TimerTime;


public class RecordActivity extends ActionBarActivity {

    private MediaRecorder myAudioRecorder;  // 안드로이드 녹음 객체
    private String outputFile = null;       // 최종 저장 파일 경로
    private Button start, stop, play, pause, stopBtn;
    private ArrayList<String> outputFileList;   // 임시 저장 파일 리스트
    //private Chronometer timer;
    private ToggleButton toggle;        //재생/일시정지버튼
    private TextView timeText;

    int state = 0;
    int count = 0;

    private CountDownTimer timer;
    int timeValue = 0;

    IRecordAidlInterface mRecord;       //서비스 바인더 인터페이스

    //상태 상수
    static final int STATE_STOP = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_PAUSE = 2;

    ServiceConnection srvConn = new ServiceConnection() {       //서비스 연결 객체
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRecord = IRecordAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRecord = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_record);

        //브로드캐스트 리시버 등록
        //서비스 -> 액티비티로 통신
        registerReceiver(abcd, new IntentFilter("xyz"));            //stop 브로드캐스트
        registerReceiver(playReceiver, new IntentFilter("play"));   // 재생 브로드캐스트
        registerReceiver(pauseReceiver, new IntentFilter("pause")); // 일시 정지 브로드 캐스트

        Intent init = getIntent();      //인텐트 데이어 받기 (녹음 저장 프래그먼트로부터 받음)
        final String sName = init.getStringExtra("sName");      // 분야 이름
        final String rName = init.getStringExtra("rName");      // 녹음 파일 이름
        toolbar.setTitle(sName + " - " + rName);                 //  툴바 타이틀 세팅   분야 - 파일이름
        toolbar.setBackgroundResource(R.color.main_color);      // 툴바 색 조정
        setSupportActionBar(toolbar);                               ///액션바를 툴바로 지정

        stopBtn = (Button) findViewById(R.id.stopBtn);
        outputFileList = new ArrayList<String>();               // 임시 파일 리스트
//        pause = (Button) findViewById(R.id.button);       //주석처리해도 영향을 끼치지 않아 주석처리함(4.25.토)
        toggle = (ToggleButton) findViewById(R.id.toggle_play);
        timeText = (TextView) findViewById(R.id.timer);

        //테스트
        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        toggle.setWidth(toggle.getHeight());

        Intent intent = new Intent(this, RecordService.class);
        intent.putExtra("sName", sName);
        intent.putExtra("rName", rName);
        // 액티비티 -> 서비스 로 전송하기 인텐트 세팅
        bindService(intent, srvConn, BIND_AUTO_CREATE);

        //타이머 세팅
        final TimerTime timeStr = new TimerTime(0);
        //타이머 설정
        // 1초마다 카운터, 최대 36000 초 까지 설정
        timer = new CountDownTimer(36000 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //매 초마다
                if (state == STATE_RUNNING) {
                    //상태가 현재 녹음 중일 때,
                    timeValue++;
                    timeStr.setTime(timeValue);     //타이머 형태로 문자열을 받앋옴
                    timeText.setText(timeStr.toString());   //타이머 세팅
                }
            }

            @Override
            public void onFinish() {

            }
        };

        toggle.setOnClickListener(new View.OnClickListener() {      //PLAY/PAUSE 버튼
            @Override
            public void onClick(View v) {
                // 녹음 버튼 클릭 했을 때
                try {
                    if (state == STATE_STOP) {
                        mRecord.play();
                        //정지 상태 일때 -> 재생

                    } else if (state == STATE_PAUSE) {
                        mRecord.play();
                        // 일시 정지 상태일 때.. 역시 재생

                    } else {
                        //그 외엔 일시정지
                        mRecord.pause();

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (state == STATE_PAUSE) {
                        mRecord.stop();
                    } else {
                        mRecord.stop();
                    }
                    state = STATE_STOP;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    /**
     * 상태 변경 함수
     *
     *  바꾸려는 상태를 받아와서 해당 상태로 변경 시키고 토글 버튼의 상태를 바꿈
     * @param state 바꾸려는 상태
     */
    public void stateChange(int state) {
        this.state = state;

        if (state == STATE_RUNNING) {
            toggle.setChecked(false);
        } else {
            toggle.setChecked(true);
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        try {
            state = mRecord.getServiceState();  //resume 마다 서비스 상태 파악
            if (state == RecordService.STATE_PREV || state == RecordService.STATE_PAUSE) {
                toggle.setChecked(true);
            } else {
                toggle.setChecked(false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //해당 액티비티가 종료될때, 리시버 종료, 바인드 서비스 연결 해제
        unregisterReceiver(pauseReceiver);
        unregisterReceiver(playReceiver);
        unregisterReceiver(abcd);
        unbindService(srvConn);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    // back 버튼 눌렀을떄
    // 함부로 종료되는걸 막기 위함
    public void onBackPressed() {

        // TODO Auto-generated method stub

        // super.onBackPressed(); //지워야 실행됨
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RecordActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alert_confirm.setMessage("녹음을 종료 하시겠습니까? \n 확인 시 녹음이 저장되지 않습니다.").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        finish();
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

    }

    //stop 리시버, 해당 액티비티 종료
    private final BroadcastReceiver abcd = new BroadcastReceiver() {    //종료 동작
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


    //play 리시버,  액티비티의 상태를 러닝으로 바꿈
    private final BroadcastReceiver playReceiver = new BroadcastReceiver() {    //서비스에 버튼 -> 시작 누를때
        @Override
        public void onReceive(Context context, Intent intent) {
            //토글 버튼 -> 일시 정지로 변경
            if(state==STATE_STOP)
                timer.start();
            stateChange(STATE_RUNNING);
        }
    };

    //pause 리시버, 액티비티 상태를 pause로 바꿈
    private final BroadcastReceiver pauseReceiver = new BroadcastReceiver() {    //서비스에 버튼 -> 일시정지 누를때
        @Override
        public void onReceive(Context context, Intent intent) {
            //토글 버튼 -> 일시 정지로 변경
            stateChange(STATE_PAUSE);
        }
    };


    /* for font */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
