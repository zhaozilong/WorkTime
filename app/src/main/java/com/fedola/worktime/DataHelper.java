package com.fedola.worktime; /**
 * Created by fedola on 15/3/1.
 */
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASENAME = "workTimeCal"; //数据库名称
    private static final int DATABASEVERSION = 1; //数据库版本
    private static final String TABLENAME = "workKind"; //数据表的名称

    public  DataHelper(Context context){
        super(context,DATABASENAME,null,DATABASEVERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table workKind" +
                "(workId integer primary key autoincrement," +
                "name text,unit integer," +
                "timeSalary integer," +
                "trainFees integer," +
                "nightStart time," +
                "nightEnd time);\n");
        db.execSQL("create table workDay( " +
                "workId integer," +
                "workDay date," +
                "startTime time,endTime time," +
                "rest1Time time,rest1End time,rest2Time time," +
                "rest2End time,rest3Time Time,rest3End time," +
                "primary key(workId,workDay)\n" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "drop table if exists workKind";
        db.execSQL(sql);
        sql = "drop table if exists workDay";
        db.execSQL(sql);
        this.onCreate(db);

    }
}
