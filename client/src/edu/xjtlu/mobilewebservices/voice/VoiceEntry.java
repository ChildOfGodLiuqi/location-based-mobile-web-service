package edu.xjtlu.mobilewebservices.voice;

import java.util.ArrayList;

import edu.xjtlu.mobilewebservices.MainFunction;
import edu.xjtlu.mobilewebservices.R;
import edu.xjtlu.mobilewebservices.webservice.Barcode2WS;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class VoiceEntry extends Activity {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 4321;
    EditText edittext;
    String code,type;

    @SuppressLint({
            "NewApi",  "NewApi"
    })
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.voiceentry);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    
        ImageView voice_button = (ImageView) findViewById(R.id.voice_search_button);
        Button btn =(Button) findViewById(R.id.nextstep);
        edittext =(EditText) findViewById(R.id.et1); 
        
        //initial voice recognizer function with click button
        voice_button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try
                {
                  //start Voice Recognizer through Intent 
                 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  
                 
                 //setting languages of Voice Recognizer 
                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                 
                 //notification of starting voice recognizer
                 intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start talk!");
                 
                 //start voice recognizer 
                 startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                }
                catch(ActivityNotFoundException  e) {
                //cannot find device
                Toast.makeText(VoiceEntry.this, "ActivityNotFoundException ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if("".equals(edittext.getText().toString().trim())) {
                Toast.makeText(VoiceEntry.this, "Pls input your search item!", Toast.LENGTH_SHORT).show();
             }else
             {
                 Intent intent = new Intent();
                 Bundle bundle= new Bundle();
                 bundle.putString("key_search_item", edittext.getText().toString());
                 intent.putExtras(bundle);
                 intent.setClass(VoiceEntry.this, Barcode2WS.class);
                 startActivity(intent); 
             }
           }
        });
    }
    
    // return onActivityResult when finished
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        //judge if the voice recognize really execute 
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode==RESULT_OK){
            //get char from voice recognizer
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
        
                final CharSequence[] items =new CharSequence[result.size()];
                for (int i =0; i< result.size();i++){
                    items[i]= result.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Voice Result");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                            edittext.setText(items[item]);
                    }   
                });
                builder.create().show();
            super.onActivityResult(requestCode, resultCode, data);  
        }
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

