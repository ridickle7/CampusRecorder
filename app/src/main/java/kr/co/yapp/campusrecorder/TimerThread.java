package kr.co.yapp.campusrecorder;

import java.util.logging.Handler;

/**
 * Created by Administrator on 2015-04-01.
 */
public class TimerThread extends Thread{

    private int i;
    private boolean isRun = false;
    private  Handler handler;
    public TimerThread(boolean isRun, Handler handler){
        this.isRun = true;
        this.handler = handler;
        i = 0;
    }

    public void stopThread(){
        isRun=false;
    }
    public void runThread(){
        isRun=true;
    }

    @Override
    public void run() {
        while (isRun){
            
        }
    }
}
