package kr.co.yapp.campusrecorder;

/**
 * Created by Administrator on 2015-04-02.
 */
public class TimerTime {

    int hour;
    int min;
    int sec;

    int time;

    public TimerTime(int time) {
        this.time = time;
    }

    public void setTime(int time) {
        this.time = time;
        operation(time);
    }

    private void operation(int time){
        hour = time/3600;
        min =  (time-hour*3600)/60;
        sec= time-(hour*3600)-(min*60);
    }

    @Override
    public String toString() {
        String hourStr;
        String minStr;
        String secStr;
        if(hour<10){
            hourStr = "0"+hour;
        }else{
            hourStr = ""+hour;
        }
        if(min<10){
            minStr = "0"+min;
        }else{
            minStr = ""+min;
        }
        if(sec<10){
            secStr = "0"+sec;
        }else{
            secStr = ""+sec;
        }
        return hourStr+":"+minStr+":"+secStr;

    }
}
