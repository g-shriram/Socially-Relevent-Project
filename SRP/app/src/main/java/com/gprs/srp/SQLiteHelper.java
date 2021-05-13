package com.gprs.srp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "Budget";



    public SQLiteHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String income_table="CREATE TABLE IF NOT EXISTS `speech` (`text` varchar(200) NOT NULL,`posscore` double NOT NULL,`negscore` double NOT NULL,`dat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(income_table);
        String image="CREATE TABLE IF NOT EXISTS `bill` (`image` varchar(100) NOT NULL,`dat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP);";
        db.execSQL(image);
        String item_table="CREATE TABLE IF NOT EXISTS `item` (" +
                " `name` varchar(24) NOT NULL," +
                "`cat` varchar(24) DEFAULT NULL" +
                ");";
        db.execSQL(item_table);
        String cat_table="CREATE TABLE IF NOT EXISTS `cat` (" +
                "  `cat` varchar(24) NOT NULL);";
        db.execSQL(cat_table);
        String daily_items="CREATE TABLE IF NOT EXISTS `dailyitems` (" +
                "  `item` varchar(24) NOT NULL," +
                "  `cat` varchar(24) NOT NULL," +
                "  `price` float NOT NULL," +
                "  `dat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        db.execSQL(daily_items);
        }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop books table if already exists
        db.execSQL("DROP TABLE IF EXISTS books");
        this.onCreate(db);
    }

    public void addText(SQLiteDatabase db,String text,double posscore,double negscore){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String curtime = df.format(new Date());

        ContentValues values = new ContentValues();
        values.put("text",text.toUpperCase());
        values.put("posscore",posscore);
        values.put("negscore",negscore);
        values.put("dat",curtime);
        db.insert("speech", null, values);
    }

    public void addexpense(SQLiteDatabase db,String item,String cat,float price){
        ContentValues values = new ContentValues();
        values.put("item",item.toUpperCase());
        values.put("cat",cat.toUpperCase());
        values.put("price",price);
        db.insert("dailyitems", null, values);
    }




    public Cursor speech_reportChart(SQLiteDatabase db) {

        String[] col = {"posscore", "negscore","dat"};
        Cursor c= db.rawQuery( "select avg(posscore),avg(negscore),dat from speech group by dat", null );
        return c;
    }

    public Cursor speech_reportChart1(SQLiteDatabase db) {

        String[] col = {"posscore", "negscore","dat"};
        Cursor c= db.rawQuery( "select count(text),avg(posscore),avg(negscore) from speech", null );
        return c;
    }

    public Cursor speech_reportChart2(SQLiteDatabase db) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String curtime = df.format(new Date());

        String[] col = {"posscore", "negscore","dat"};
        Cursor c= db.rawQuery( "select avg(posscore),avg(negscore) from speech", null );
        return c;
    }
    public void delete_income(SQLiteDatabase db, String s) {

        db.delete("income", "dat" + " = ?", new String[] { s });


    }

    public void delete_expense(SQLiteDatabase db, String s) {
        db.delete("dailyitems", "dat" + " = ?", new String[] { s });
    }

    public Cursor expense_report(SQLiteDatabase db) {
        String[] col = {"item", "cat", "price", "dat"};

        Cursor c= db.query("dailyitems", null, null, null, null, null, null);
        return c;
    }

    public Cursor income_report_date(SQLiteDatabase db, String from, String to) {
        String[] col = {"amt", "src", "dat"};
        Cursor c= db.query("income", null, "date(dat) BETWEEN ? AND ?", new String[] {
                from + " 00:00:00", to + " 23:59:59" }, null, null, null, null);
        return c;
    }

    public Cursor expense_report_date(SQLiteDatabase db, String from, String to) {
        String[] col = {"item", "cat", "price", "dat"};
        Cursor c= db.query("dailyitems", null, "date(dat) BETWEEN ? AND ?", new String[] {
                from + " 00:00:00", to + " 23:59:59" }, null, null, null, null);
        return c;
    }

    public Cursor income_report_date_cat(SQLiteDatabase db, String from, String to) {
        String[] col = {"amt", "src", "dat"};
        Cursor c= db.query("income", new String[]{"src","SUM(amt)"}, "date(dat) BETWEEN ? AND ?", new String[] {
                from + " 00:00:00", to + " 23:59:59" }, "src", null, null, null);
        return c;
    }

    public Cursor expense_report_date_cat(SQLiteDatabase db, String from, String to) {
        String[] col = {"item", "cat", "price", "dat"};
        Cursor c= db.query("dailyitems", new String[]{"cat","SUM(price)"}, "date(dat) BETWEEN ? AND ?", new String[] {
                from + " 00:00:00", to + " 23:59:59" }, "cat", null, null, null);
        return c;
    }

    public Cursor getitems(SQLiteDatabase db, String string, String from, String to) {
        Cursor c= db.query("dailyitems", new String[]{"item","SUM(price)"}, "cat = ? AND date(dat) BETWEEN ? AND ?", new String[] {string,
                from + " 00:00:00", to + " 23:59:59" }, "item,cat",null, null, null);
        return c;
    }

    public Cursor gettincome(SQLiteDatabase db) {
        Cursor c= db.query("income", new String[]{"SUM(amt)"}, null,null, null, null, null);
        return c;
    }

    public Cursor gettexpense(SQLiteDatabase db) {
        Cursor c= db.query("dailyitems", new String[]{"SUM(price)"}, null,null, null, null, null);
        return c;
    }

    public Cursor getmcat(SQLiteDatabase db) {
        Cursor c= db.query("dailyitems", new String[]{"cat","SUM(price)"}, null,null, "cat", null, "SUM(price) DESC","1");
        return c;
    }

    public Cursor getmitem(SQLiteDatabase db) {
        Cursor c= db.query("dailyitems", new String[]{"item","SUM(price)"}, null,null, "item", null, "SUM(price) DESC","1");
        return c;
    }

    public void addimage(SQLiteDatabase db, String image) {
        ContentValues values = new ContentValues();
        values.put("image",image);
        db.insert("bill", null, values);
    }

    public Cursor getimage(SQLiteDatabase db) {
        Cursor c= db.query("bill", null, null,null, null, null, "dat DESC",null);
        return c;
    }

    public void delete_image(SQLiteDatabase db, String s) {
        db.delete("bill", "dat" + " = ?", new String[] { s });
    }

    public void update_expense(SQLiteDatabase db, String toString, String toString1, String toString2, String s) {

        ContentValues values = new ContentValues();
        values.put("item",toString.toUpperCase() );
        values.put("cat",toString1.toUpperCase());
        values.put("price",toString2);


        int i = db.update("dailyitems", values, "dat = ?", new String[] { s });

    }

    public void update_income(SQLiteDatabase db, String toString, String toString1, String s) {
        ContentValues values = new ContentValues();
        values.put("amt",toString );
        values.put("src",toString1.toUpperCase());


        int i = db.update("income", values, "dat = ?", new String[] { s });
    }

}
