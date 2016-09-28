package kr.co.yapp.campusrecorder.Data;

/**
 * Created by Administrator on 2015-10-30.
 */
public class SectionItem extends listItem {
    private String title;
    private int itemNumber;

    public SectionItem(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }
}
