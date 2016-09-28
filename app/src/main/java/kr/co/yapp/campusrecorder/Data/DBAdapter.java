package kr.co.yapp.campusrecorder.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by home on 2015-02-13.
 */
public class DBAdapter {

    private Context ctx;
    public DBAdapter(Context ctx){
        this.ctx = ctx;
    }

    //DB column(녹음 파일)
    //private static final String RECFILE_SID = "sId";
    private static final String RECFILE_RID = "rId";
    private static final String RECFILE_RECDATE = "recDate";
    private static final String RECFILE_RECTIME = "recTime";
    private static final String RECFILE_SIZE = "size";
    private static final String RECFILE_RNAME = "rName";
    private static final String RECFILE_SNAME = "sName";
    private static final String RECFILE_PATH = "path";

    //DB column(과목)
  //  private static final String SUBJECT_SID = "sId";
   // private static final String SUBJECT_SNAME = "sName";

    //디비를 처음 생성하고, 버전이 달라질 시 재생성!!
    private DatabasHelper DBHelper;

    private SQLiteDatabase sqlite;

    //DB 파일 명 - 폰에 이 이름으로 저장됨
    private static final String DATABASE_NAME = "mijung.db";

    //테이블 명들
    private static final String MAIN_TABLE = "RECFILE";
   // private static final String SUBJECT_TABLE = "SUBJECT";

    //Create문 스키마 작성
    private static final String MAIN_DATABASE_CREATE =  "CREATE TABLE "+MAIN_TABLE+" ( "+RECFILE_RID+" integer primary key autoincrement, "+
            RECFILE_RECDATE+" text not null, "+RECFILE_RECTIME+" integer not null, "
            + RECFILE_SIZE + " REAL not null, " + RECFILE_RNAME+" text null, "+ RECFILE_SNAME+" text null, "+ RECFILE_PATH + " text null);";

  //  private static final String SUBJECT_DATABASE_CREATE = "CREATE TABLE " + SUBJECT_TABLE+ " ( "+SUBJECT_SID+" integer primary key autoincrement, "+
 //           SUBJECT_SNAME+ " text not null);";

    //데이터베이스 스키마가 변경 될 경우 수정
    private static final int MAIN_DATABASE_VERSION = 3;



    //MAIN DB HELPER
    private class DatabasHelper extends SQLiteOpenHelper{

        public DatabasHelper(Context context) {
            super(context,DATABASE_NAME,null,MAIN_DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(MAIN_DATABASE_CREATE);
         //   db.execSQL(SUBJECT_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS RECFILE");
          //  db.execSQL("DROP TABLE IF EXISTS SUBJECT");

            onCreate(db);
        }
    }

    //디비를 열고 닫는 메소드
    public DBAdapter open() throws SQLException{
        DBHelper = new DatabasHelper(ctx);
        sqlite = DBHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        DBHelper.close();
    }

    //쿼리 SQL문	(쿼리함수를 쓰거나, 직접 SQL을 작성하거나)
    public Cursor getAllRecFile(){
        //Select * From MAIN_TABLE(recFile)
        return sqlite.query(MAIN_TABLE, null, null, null, null, null, null);
    }

    public RecFile getRecFile(int rId){
        RecFile recFile = new RecFile();

        //하나 가져오는 거
        Cursor cursor = sqlite.rawQuery("SELECT * FROM RECFILE WHERE rId = " + rId + ";",null);
        cursor.moveToFirst();
        Log.d("tag",cursor.getCount()+"");

       // recFile.setSId(cursor.getInt(0));
        recFile.setRId(cursor.getInt(0));
        recFile.setRecDate(cursor.getString(1));
        recFile.setRecTime(cursor.getInt(2));
        recFile.setSize(cursor.getInt(3));
        recFile.setrName(cursor.getString(4));
        recFile.setsName(cursor.getString(5));
        recFile.setPath(cursor.getString(6));

        return recFile;
    }

    //RecFile insert문
    public boolean addRecFile(RecFile recFile){
        ContentValues cv = new ContentValues();

        cv.put(RECFILE_RECDATE, recFile.getRecDate());
        cv.put(RECFILE_RECTIME, recFile.getRecTime());
        cv.put(RECFILE_SIZE, recFile.getSize());
        cv.put(RECFILE_RNAME, recFile.getrName());
        cv.put(RECFILE_SNAME, recFile.getsName());
        cv.put(RECFILE_PATH, recFile.getPath());

        sqlite.insert(MAIN_TABLE, null, cv);
        return true;
    }


    //나머지는 다 SQL문
    public Cursor getRecFileLatest(){
        return sqlite.rawQuery("SELECT SID FROM RECFILE WHERE SID = (SELECT MAX(SID) FROM RECFILE);", null);
    }

/*    //subject insert문
    public void insertSubject(String sName){
        ContentValues cv = new ContentValues();
       // cv.put(SUBJECT_SID, sId);
        cv.put(SUBJECT_SNAME, sName);
      //  Log.d("INSERT", sId + "");
       long a= sqlite.insert(SUBJECT_TABLE, null, cv);
        Toast.makeText(ctx,a+"",Toast.LENGTH_SHORT).show();
        Log.d("SQL",MAIN_DATABASE_CREATE);
    }*/
    public Cursor getAllDate(){
    return sqlite.rawQuery("SELECT DISTINCT RECDATE FROM RECFILE; ",null);

    }

    public Cursor getSubject(int rId){
        return sqlite.rawQuery("SELECT * FROM RECFILE WHERE rId = "+rId+";",null);

    }
    public Cursor getAllSubject(){
        return sqlite.rawQuery("SELECT DISTINCT SNAME FROM RECFILE WHERE SNAME NOT IN ('분류없음','전체');",null);
    }

    public Cursor getAllItem(){
        return sqlite.rawQuery("SELECT * FROM RECFILE order by " + RECFILE_RECDATE + " desc;",null);
    }

    public void addSubject(String subject){
        ContentValues cv = new ContentValues();
        cv.put(RECFILE_SNAME,subject);
        sqlite.insert(MAIN_TABLE,null,cv);
    }
/*   public void subjectDelete(int sId){
        sqlite.execSQL("DELETE FROM SUBJECT WHERE sId = "+sId+";");
    }*/

    public Cursor getRecByDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        //"2014-05-10
        String strDate = formatter.format(date).toString();
        Log.d("strDate",strDate);
        return sqlite.rawQuery("SELECT * FROM RECFILE WHERE recDate" +
                " like '" +strDate+ "';",null);
    }

    public Cursor getRecBySearch(String str){
        return sqlite.rawQuery("SELECT * FROM RECFILE WHERE rName" + " like '%" +str +"%' order by " + RECFILE_RECDATE + " desc;",null);
    }

    public Cursor getRecBySub(String subject)
    {
        return sqlite.rawQuery("SELECT * FROM RECFILE WHERE sNAME like '"+subject+"' order by " + RECFILE_RECDATE + " desc;",null);
    }
    public void deleteRecFile(int rId) {
        sqlite.execSQL("DELETE FROM RECFILE WHERE rId = " + rId + ";");
    }

    public void updateCategory(int rid,String sName){
        sqlite.execSQL("UPDATE RECFILE SET SNAME='"+sName+"' where rid="+rid);
    }
    public void updateName(int rid,String rName){
        sqlite.execSQL("UPDATE RECFILE SET RNAME='"+rName+"' where rid="+rid);
    }

}