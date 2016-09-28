package kr.co.yapp.campusrecorder.Data;

/**
 * Created by home on 2015-02-13.
 */
public class RecFile extends listItem {
    //
    private int rId;
    private String recDate;    //녹음 날짜
    private int recTime;    //녹음 시간
    private double size;       //용량
    private String rName;    //파일명
    private String path;    //파일 패스
    private String sName;

    public RecFile(){
        type = 1;
    }

    public int getType() {
        return type;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public int getRId() {
        return rId;
    }

    public void setRId(int rId) {
        this.rId = rId;
    }

    public String getRecDate() {
        return recDate;
    }

    public void setRecDate(String recDate) {
        this.recDate = recDate;
    }

    public int getRecTime() {
        return recTime;
    }

    public void setRecTime(int recTime) {
        this.recTime = recTime;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
