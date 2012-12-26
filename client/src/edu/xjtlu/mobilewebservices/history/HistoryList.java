package edu.xjtlu.mobilewebservices.history;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import edu.xjtlu.mobilewebservices.MainFunction;

@SuppressLint({
        "NewApi", "NewApi"
})
public class HistoryList extends ListActivity{
	ArrayList<String> al;
	String search_item,search_type,result;

	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
	      ActionBar actionBar = getActionBar();
	      actionBar.setDisplayHomeAsUpEnabled(true);
		 
		ListAdapter adapter = createAdapter();
	    setListAdapter(adapter);
	}
	
	protected ListAdapter createAdapter(){
		    DatabaseHandler db = new DatabaseHandler(this);
			
		    try{
		    	Bundle bundle = this.getIntent().getExtras();
		    	result = bundle.getString("product_info");
		    	search_item = bundle.getString("key_search_item");
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    if (result != null ){
		    	//Inserting bar code
		 
				Log.d("Insert:", "Inserting ...");
				db.addBarcode(new Barcode(result,search_item));
		    }
			
		Log.d("Reading:", "Reading all barcode ...");
		List<Barcode> barcode = db.getAllBarcode();

		 al = new ArrayList<String>();
		for(Barcode bc : barcode){
			String log = "Id: "+bc.getID()+" ;"+bc.getName()+" ;"+ bc.getBarcodeNumber();
		    al.add(log);
			Log.d("Name", log);
		}
		
		String [] historyList = new String[al.size()];
		for (int i =0;i< al.size();i++){
			historyList[i] =al.get(i);
		}
		
		ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,historyList);
		return adapter;
		
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)  
	{     
	   switch (item.getItemId())  
	   {         
	      case android.R.id.home:             
	         Intent intent = new Intent(this, MainFunction.class);             
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	         startActivity(intent);             
	         return true;         
	      default:             
	         return super.onOptionsItemSelected(item);     
	   } 
	} 
	                                      
	
}
