package kr.co.yapp.campusrecorder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import kr.co.yapp.campusrecorder.Activity.RecordActivity;
import kr.co.yapp.campusrecorder.Data.DBAdapter;
import kr.co.yapp.campusrecorder.Data.RecFile;

/**
 * 녹음을 하는 실질적 객체
 * Created by BH on 2015-02-13.
 */
public class RecordService extends Service {
    //상태 상수
    public final static int STATE_PREV = 0;     //녹음 시작 전
    public final static int STATE_RECORDING = 1;    //녹음 중
    public final static int STATE_PAUSE = 2;        // 일시 정지 중

    public final static int RECORD_AUDIO = 0;

    //인텐트 상수
    public static final String ACTION_PREV = "xxx.yyy.zzz.STATE_PREV";
    public static final String ACTION_PREV_STOP = "xxx.yyy.zzz.STATE_PREV_STOP";
    public static final String ACTION_RECORDING = "xxx.yyy.zzz.STATE_RECORDING";
    public static final String ACTION_PAUSE = "xxx.yyy.zzz.STATE_PAUSE";
    public static final String ACTION_STOP = "xxx.yyy.zzz.STATE_STOP";


    RemoteViews contentView;

    //플로팅 버튼 위치 상수

    private String outputFile = null;       //최종 출력 파일 패스
    private MediaRecorder myAudioRecorder;  // 안드로이드 녹음 객체
    private ArrayList<String> outputFileList;   // 임시 저장 파일 리스트
    private String sname;       //과목 명
    private String rname;       //녹음 파일 명
    private DBAdapter dba;      //디비
    int count = 0;                //재생 카운트
    private Context ctx = this;

    private int state = STATE_PREV; // 초기 상태

    //for chathead
    WindowManager windowManager;        //오버레이 버튼을 위한 윈도우 메니져

    boolean mHasDoubleClicked = false;
    long lastPressTime;
    private Boolean _enable = true;
    ImageView btnA, test;


    private boolean serviceWillBeDismissed;
    float mTouchX = 0, mTouchY = 0;
    int mViewX = 0, mViewY = 0;
    // 패널 전체 크기
    private double displayHeight;
    private double displayWidth;

    //버튼의 위치 저장하는 파라미터 객체
    WindowManager.LayoutParams buttonParam;


    private int nowSection = 6;
    int start;
    int end;

    //현재 상태에 따른 메소드 실행
    //바인더
    IRecordAidlInterface.Stub mBinder = new IRecordAidlInterface.Stub() {

        @Override
        public int getServiceState() throws RemoteException {
            return state;
        }


        @Override
        public void play() throws RemoteException {

            sendBroadcast(new Intent("play"));      //액티비티로 브로드캐스트 날림, 시작됬다고 알림
            createNoti("녹음 중입니다..", STATE_RECORDING);


            count += 1; //파일 넘버 1 증가
            outputFile = Environment.getExternalStorageDirectory().
                    getAbsolutePath() + "/myrecording"; //임시 파일 저장 경로

            String nowFile = outputFile + count + ".mp4"; // 파일 명 (no) .mp4

            outputFileList.add(nowFile);    //임시파일 리스트에 파일 경로 추가

            myAudioRecorder = new MediaRecorder();  //미디어 레코더 생성
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //마이크를 통해 녹음 시작
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //저장 방식 MPEG4
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioEncodingBitRate(320000);
            myAudioRecorder.setAudioSamplingRate(44100);
            myAudioRecorder.setOutputFile(nowFile); //출력 파일 지정

            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();        //녹음 시작
                //         SuperToast.create(getApplicationContext(), "Recording started", SuperToast.Duration.VERY_SHORT,
                //                 Style.getStyle(Style.RED, SuperToast.Animations.FLYIN)).show();

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            state = STATE_RECORDING; //녹음 중 상태로 바꿈

        }

        @Override
        public void stop() throws RemoteException {
            if (state == STATE_PREV) {     //
                //녹음 시작안한 상태에서 정지 버튼 누르기
                stopSelf();
                return;
            } else if (state == STATE_PAUSE) {
                //일시 정지 상태일 때,
            } else {
                // 재생 중 정지 버튼을 눌렀을 때 = 정상 작동
                //카운트 초기화
                //레코더 중지

                try {
                    myAudioRecorder.stop();
                    // FileObserver here
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    sendBroadcast(new Intent("xyz"));   //액티비티로 스탑 리시버 전송
                    stopSelf();
                    Toast.makeText(getApplicationContext(), "녹음 실패", Toast.LENGTH_SHORT).show();
                }
                myAudioRecorder.reset();
                myAudioRecorder.release();
                myAudioRecorder = null;
            }

            //여기 서부턴 일시정지중 -> 정지, 재생중 -> 정지 랑 동일
            count = 0;
            //stop.setEnabled(false);
            //play.setEnabled(true);
            try {
                append(outputFileList);     //현재 임시 파일 리스트에 있는 파일들을 하나로 합침( 최종 결과파일)
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Append Error!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            //프래그먼트를 띄우게 됨.. 여기서 수정 필요할듯
        /*RecordDialogFragment dialogFragment = RecordDialogFragment.newInstance();
        dialogFragment.setStyle(R.style.Theme_Dialog_Transparent,R.style.Theme_Dialog_Transparent);
        dialogFragment.show(getFragmentManager(),"TAG");
    */

            double fileSize = 0;
            String name = rname;
            String sub = sname;

            Date date = new Date();
            Log.d("DayTest", date.getYear() + " " + date.getMonth() + " " + date.getDate());


            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");


            String strDate = sim.format(date).toString();
            String rName = sub + "_" + name + "_" + strDate + "-" + date.getHours() + "-" + date.getMinutes();
            //Toast.makeText(getActivity().getApplicationContext(),strDate,Toast.LENGTH_SHORT).show();

            //file1 -> file2 로 복사
            String outPath = Environment.getExternalStorageDirectory() + "/output.mp4";
            File file = new File(outPath);
            String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + rName + ".mp4";
            Log.d("PATH_TEST", getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());

            SuperToast superToast = new SuperToast(getApplicationContext(), Style.getStyle(Style.RED));
            superToast.setDuration(SuperToast.Duration.SHORT);
            superToast.setText(rName + ".mp4 파일이 저장되었습니다.");
            superToast.setIcon(R.drawable.ic_launcher, SuperToast.IconPosition.LEFT);
            superToast.show();


            MediaPlayer mp = new MediaPlayer();
            try {
                //재생 시간 구하기
                FileInputStream fs;
                FileDescriptor fd;
                fs = new FileInputStream(file);
                fd = fs.getFD();
                mp.setDataSource(fd);
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int duration = mp.getDuration();
            duration /= 1000;
            mp.release();

            File file2 = new File(path);
            fileSize = file.length();   //파일 크기 구하기(Byte)
            double fileMb = fileSize / (1024 * 1024);
            fileMb = Double.parseDouble(String.format("%.3f", fileMb));


            String info = strDate + " " + duration + " " + fileMb + "MB";
            Log.d("duration", info);
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(file2);
                int readcount = 0;
                byte[] buffer = new byte[1024];
                while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                    newfos.write(buffer, 0, readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //file1 -> file2 로 복사(END)
            file.delete();

            RecFile recFile = new RecFile();
            recFile.setrName(name);
            recFile.setRecDate(strDate);
            recFile.setRecTime(duration);
            recFile.setsName(sub);
            recFile.setPath(path);
            recFile.setSize((int) fileSize);

            dba.addRecFile(recFile);

            //  Toast.makeText(getActivity().getApplicationContext(),c.getCount()+"",Toast.LENGTH_SHORT).show();

            sendBroadcast(new Intent("xyz"));   //액티비티로 스탑 리시버 전송
            stopSelf();

        }

        @Override
        public void pause() throws RemoteException {
            sendBroadcast(new Intent("pause"));      //액티비티로 브로드캐스트 날림, 일시정지            Toast.makeText(getApplicationContext(),"pause",Toast.LENGTH_SHORT).show();
            createNoti("일시정지", STATE_PAUSE);
            myAudioRecorder.stop();     //현재 녹음 중인 파일 종료 (임시파일)
            myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;
            state = STATE_PAUSE;
        }

        /**
         *  액티비티 상태 변환시 사용
         * @param state 0=pause ,1 =run
         * @throws RemoteException
         */

        @Override
        public void stateChange(int state) throws RemoteException {
            if (state == 0) {  //onPause 호출시

            } else {

            }

        }

        @Override
        public void init(String sName, String rName) throws RemoteException {
            sname = sName;
            rname = rName;
            if (rname.equals("")) {
                rname = "제목없음";
            }
        }
    };


    /**
     * mp4를 하나로 만들어서 저장하는 메소드
     * 파일 패스 리스트를 받아서 합쳐서 outputfile 에 패스를 담음
     *
     * @param list 하나로 합칠 파일 리스트
     * @throws IOException
     */
    public void append(List<String> list) throws IOException {
        // String f1 = Environment.getExternalStorageDirectory() + "/video1.mp4";
        //String f2 = Environment.getExternalStorageDirectory()+ "/video2.mp4";

        Movie[] inMovies;
        inMovies = new Movie[list.size()];
        for (int i = 0; i < list.size(); i++) {
            inMovies[i] = MovieCreator.build(list.get(i));
        }

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(result);

        RandomAccessFile ram = new RandomAccessFile(String.format(Environment.getExternalStorageDirectory() + "/output.mp4"), "rw");
        //최종적으로 output.mp4 라는 파일로 다 합친 파일을 저장하게 됨
        FileChannel fc = ram.getChannel();
        out.writeContainer(fc);
        ram.close();
        fc.close();
    }


    //AIDL 사용하기 위한 onBind
    //처음 바인드시 인텐트에서 sName과 rName을 받아서 세팅함
    @Override
    public IBinder onBind(Intent intent) {
        rname = intent.getStringExtra("rName");
        sname = intent.getStringExtra("sName");
        return mBinder;
    }

    NotificationCompat.Builder mBuilder;
    Notification noti;

    public void createNoti(String text, int flag) throws RemoteException {
        this.stopForeground(true);
        Intent notiIntent = new Intent(this, RecordActivity.class);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setTicker("CampusRecorder");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setNumber(10);
        mBuilder.setContentTitle("CampusRecorder");
        mBuilder.setContentText("text");

        mBuilder.setContentIntent(pendingIntent);


        noti = mBuilder.build();


        contentView = new RemoteViews(getPackageName(), R.layout.noti_layout);
        contentView.setTextViewText(R.id.title, text);
        //contentView.setOnClickPendingIntent(R.id.button, pendingIntent);

        //녹음 전 상태 0
        if (flag == STATE_PREV) {
            Intent inten_play = new Intent(ACTION_PREV);
            Intent intent_stop = new Intent(ACTION_PREV_STOP);
            contentView.setInt(R.id.btn_noti_play, "setBackgroundResource", R.drawable.play_btn);
            contentView.setOnClickPendingIntent(R.id.btn_noti_play, PendingIntent.getService(this, 0, inten_play, PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.btn_noti_stop, PendingIntent.getService(this, 0, intent_stop, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        //녹음 중인 상태 1
        else if (flag == STATE_RECORDING) {
            Intent intent_play = new Intent(ACTION_RECORDING);
            Intent intent_stop = new Intent(ACTION_STOP);
            contentView.setInt(R.id.btn_noti_play, "setBackgroundResource", R.drawable.pause_btb);
            contentView.setOnClickPendingIntent(R.id.btn_noti_play, PendingIntent.getService(this, 0, intent_play, PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.btn_noti_stop, PendingIntent.getService(this, 0, intent_stop, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        //녹음 일시정지 상태 2
        else if (flag == STATE_PAUSE) {
            Intent intent_play = new Intent(ACTION_PAUSE);
            Intent intent_stop = new Intent(ACTION_STOP);
            contentView.setInt(R.id.btn_noti_play, "setBackgroundResource", R.drawable.play_btn);
            contentView.setOnClickPendingIntent(R.id.btn_noti_play, PendingIntent.getService(this, 0, intent_play, PendingIntent.FLAG_UPDATE_CURRENT));
            contentView.setOnClickPendingIntent(R.id.btn_noti_stop, PendingIntent.getService(this, 0, intent_stop, PendingIntent.FLAG_UPDATE_CURRENT));
        }


        noti.contentView = contentView;


        this.startForeground(3452, noti);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        dba = new DBAdapter(getApplicationContext());
        dba = dba.open();
        outputFileList = new ArrayList<String>();
        try {
            createNoti(RecordApplication.fileName, STATE_PREV);
            //foreground 설정*/
            //주석
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    if (action.equals(ACTION_PREV)) {
                        mBinder.play();
                    } else if (action.equals(ACTION_PREV_STOP)) {
                        Toast.makeText(getApplicationContext(), "녹음한 내용이 없습니다.", Toast.LENGTH_LONG);
                    } else if (action.equals(ACTION_PAUSE)) {
                        mBinder.play();
                    } else if (action.equals(ACTION_RECORDING)) {
                        mBinder.pause();
                    } else if (action.equals(ACTION_STOP)) {
                        mBinder.stop();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RecordApplication.fileName = "";
    }


}

