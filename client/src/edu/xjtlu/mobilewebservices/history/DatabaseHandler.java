package edu.xjtlu.mobilewebservices.history;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	//database version
	private static final int DATABASE_VERSION = 1;
    
	//database name
	private static final String DATABASE_NAME ="BarcodeManager.db";
	
	//table name
	private static final String TABLE_BARCODE = "barcodes";
	
	//table columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_BARCODE_NUMBER ="barcode_number";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//create tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BARCODE_TABLE = "CREATE TABLE "+ TABLE_BARCODE + "(" + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ KEY_NAME + " TEXT,"+ KEY_BARCODE_NUMBER + " TEXT);";
	    db.execSQL(CREATE_BARCODE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARCODE);
		onCreate(db);
	}
	
	public void addBarcode(Barcode barcode){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, barcode.getName());
		values.put(KEY_BARCODE_NUMBER, barcode.getBarcodeNumber());
		
		//inserting row
		db.insert(TABLE_BARCODE, null, values);
		
		//closing database connection
		db.close();
	}
	
	   //Getting single barcode
	   public Barcode getBarcode(int id){
		   SQLiteDatabase db = this.getReadableDatabase();
		   
		   Cursor cursor = db.query(TABLE_BARCODE,new String[]{KEY_ID,KEY_NAME,KEY_BARCODE_NUMBER}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
		
		   if(cursor != null){
			   cursor.moveToFirst();
		   }
		   
		   Barcode barcode = new Barcode(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
		   return barcode;
	   }
	   
	   //Getting All barcode
	   public List<Barcode> getAllBarcode(){
		   SQLiteDatabase db = this.getWritableDatabase();
		   List<Barcode> barcodeList = new ArrayList<Barcode>();
		   String selectQuery = "SELECT * FROM " + TABLE_BARCODE;
		  
		   Cursor cursor = db.rawQuery(selectQuery, null);
		   
		   //looping through all rows and adding to list
		   if (cursor.moveToFirst()){
			   do{
				   Barcode barcode = new Barcode();
				   barcode.setID(Integer.parseInt(cursor.getString(0)));
				   barcode.setname(cursor.getString(1));
				   barcode.setBarcodeNumber(cursor.getString(2));
				   
				   //adding barcode to list
				   barcodeList.add(barcode);
			   }while(cursor.moveToNext());
			   
		   }
		return barcodeList;
		   
	   }
	   
	   //updating single barcode
	   public int updateBarcode(Barcode barcode){
		   SQLiteDatabase db = this.getWritableDatabase();
		   
		   ContentValues values = new ContentValues();
		   values.put(KEY_NAME, barcode.getName());
		   values.put(KEY_BARCODE_NUMBER, barcode.getBarcodeNumber());
		   
		   //updating row
		   return db.update(TABLE_BARCODE, values, KEY_ID + " = ? ",  new String[] { String.valueOf(barcode.getID()) });
	   }
	   
	   public void deleteBarcode(Barcode barcode){
		   SQLiteDatabase db = this.getWritableDatabase();
		   db.delete(TABLE_BARCODE, KEY_ID + " = ?", new String[] { String.valueOf(barcode.getID())});
		   db.close();
	   }
	   
	   //Getting barcode count
	   public int getBarcodeCount(){
		   SQLiteDatabase db = this.getReadableDatabase();
		   String countQuery = "SELECT * FROM " + TABLE_BARCODE;
		   
		   Cursor cursor = db.rawQuery(countQuery, null);
		   cursor.close();
		   
		return cursor.getCount();
		   
	   }

}
