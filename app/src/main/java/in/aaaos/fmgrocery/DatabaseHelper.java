package in.aaaos.fmgrocery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public  static final String DATABASE_NAME="Fmgrocery.db";
    public  static final String TABLE_NAME="Cart";
    public  static final String COL_2="TITLE";
    public  static final String COL_3="IMAGE";
    public  static final String COL_4="QTY";
    public  static final String COL_5="IND_ID";
    public  static final String COL_6="AMOUNT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,TITLE TEXT,IMAGE TEXT,QTY TEXT,IND_ID TEXT,AMOUNT TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists  "+TABLE_NAME);
    }
    public boolean inserdatacart(String title, String image, String qty,String indi_id,String amount) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,image);
        contentValues.put(COL_4,qty);
        contentValues.put(COL_5,indi_id);
        contentValues.put(COL_6,amount);
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public Cursor getIndividualData(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE ID = " + id, null);
        Log.d("val", String.valueOf(res));
        return res;
    }
    public Cursor updateval(String qty, String  id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res= db.rawQuery("update "+TABLE_NAME+" SET QTY = "+"'"+qty+"'"+" WHERE ID = "+"'"+id+"'",null);
        return res;
    }
    public Cursor updatepri(String pri, String  id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res= db.rawQuery("update "+TABLE_NAME+" SET AMOUNT = "+"'"+pri+"'"+" WHERE ID = "+"'"+id+"'",null);
        return res;
    }
    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public void del(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    public Integer deleteData(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(TABLE_NAME,"ID=?",new String[] {id});
    }
}
