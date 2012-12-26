package edu.xjtlu.mobilewebservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.xjtlu.mobilewebservices.history.HistoryList;
import edu.xjtlu.mobilewebservices.place.AlertDialogManager;
import edu.xjtlu.mobilewebservices.place.MainActivity;
import edu.xjtlu.mobilewebservices.textentry.TextEntry;
import edu.xjtlu.mobilewebservices.voice.VoiceEntry;

public class MainFunction extends Activity {
	EditText edittext;
	String code,type;
	// Connection detector class
    ConnectionDetector cd;
    // flag for Internet connection status
    Boolean isInternetPresent = false;


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

	@SuppressLint({
            "NewApi",  "NewApi"
    })
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main_function);
	    cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(MainFunction.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
	
		
		Button iv_barcode = (Button) findViewById(R.id.barcode_search_button);
		Button iv_places = (Button) findViewById(R.id.map_search_button);
		
		iv_barcode.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final CharSequence[] items ={"Barcode Scann","Voice Entry","Text Entry"};
                
                AlertDialog.Builder builder = new AlertDialog.Builder(MainFunction.this);
                builder.setTitle("Choose one way to search!");
                builder.setItems(items, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    switch(which){
                        case 0:
                            Intent intent_scan = new Intent();
                            intent_scan.setClass(MainFunction.this, CaptureActivity.class);
                            startActivity(intent_scan);
                            break;

                        case 1:
                            Intent intent_voice = new Intent();
                            intent_voice.setClass(MainFunction.this, VoiceEntry.class);
                            startActivity(intent_voice);
                            break;
                            
                        case 2:
                            Intent intent_text = new Intent();
                            intent_text.setClass(MainFunction.this, TextEntry.class);
                            startActivity(intent_text);
                    }
                  }
                            
                });
               
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
		
		iv_places.setOnClickListener(new View.OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                Intent intent = new Intent();
	                intent.setClass(MainFunction.this, MainActivity.class);
	                startActivity(intent);
	                
	            }
	        });
	
	}
	
    @Override 
    public boolean onOptionsItemSelected(MenuItem item)  
    {     
       switch (item.getItemId())  
       {             
             
          case R.id.menu_info:
              Uri uriUrl = Uri.parse("http://shenzhun.github.com/projects.html");
              Intent launchBrowser = new Intent(Intent.ACTION_VIEW,uriUrl);
              startActivity(launchBrowser); 
              return true;
              
          case R.id.menu_hitory:
              Intent intent_history= new Intent(this, HistoryList.class);             
              intent_history.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
              startActivity(intent_history); 
              return true;
          default:             
             return super.onOptionsItemSelected(item);     
       } 
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.function_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
}
