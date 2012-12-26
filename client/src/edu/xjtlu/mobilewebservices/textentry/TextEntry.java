package edu.xjtlu.mobilewebservices.textentry;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import edu.xjtlu.mobilewebservices.MainFunction;
import edu.xjtlu.mobilewebservices.R;
import edu.xjtlu.mobilewebservices.webservice.Barcode2WS;

public class TextEntry extends Activity {
    EditText edittext;
    String code,type;

    @SuppressLint({
            "NewApi",  "NewApi"
    })
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.textentry);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        
        ImageView btn =(ImageView) findViewById(R.id.search_button);
        edittext =(EditText) findViewById(R.id.et1); 
        
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if("".equals(edittext.getText().toString().trim())) {
                Toast.makeText(TextEntry.this, "Pls input your search item!", Toast.LENGTH_SHORT).show();
             }else
             {
                 Intent intent = new Intent();
                 Bundle bundle= new Bundle();
                 bundle.putString("key_search_item", edittext.getText().toString());
                 intent.putExtras(bundle);
                 intent.setClass(TextEntry.this, Barcode2WS.class);
                 startActivity(intent); 
             }
           }
        });
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
