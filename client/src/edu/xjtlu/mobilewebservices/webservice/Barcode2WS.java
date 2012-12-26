package edu.xjtlu.mobilewebservices.webservice;

import java.io.IOException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import edu.xjtlu.mobilewebservices.R;
import edu.xjtlu.mobilewebservices.history.HistoryList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class Barcode2WS extends ListActivity {
	private static final String URL = "http://10.7.4.118:8080/server4WS/services/WS4Products?wsdl";
	private static final String NAMESPACE = "http://mobilewebservices.xjtlu.edu";
	private static final String METHOD_NAME = "productsQuery";
	private static final String SOAP_ACTION =METHOD_NAME+NAMESPACE;
	private String code, result_list;
	private ArrayList<String> info;
	private Object result;
    ArrayAdapter<String> arrayAdapter;
	
	Handler myHandler = new Handler() {  
         public void handleMessage(Message msg) {   
              switch (msg.what) {   
                  case 0:
                      arrayAdapter.notifyDataSetChanged();
                      setListAdapter(arrayAdapter);
                      Log.v("update arrayAdapter!", "------------");
                      //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,info));
                      break;
                      
                      default:
                          break;
              }   
         }   
    };  

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Bundle bundle = this.getIntent().getExtras();
		code = bundle.getString("key_search_item");
		
		info = new ArrayList<String>();
		info.add("Searching Item:"+code);
	    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,info);
	    //setListAdapter(arrayAdapter);
	    
		new Thread(new Runnable()	
		{
	           @Override
	            public void run() {
	               
	                //call web service for searching with methods name and parameter
	                //request methods and parameter of web service 
	                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
	                request.addProperty("search_item",code);
	                Log.v("SoapObject", "Ok----SoapObject----");
	                
	                //define the envelope of sending message
	                final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); 
	                envelope.bodyOut = request;
	                Log.v("SoapSerializationEnvelope", "Ok----SoapSerializationEnvelope----");
	                 

	                //construct sending object
	                final HttpTransportSE htSE = new HttpTransportSE(URL);
	                htSE.debug=true;
	                Log.v("HttpTransportSE", "Ok----HttpTransportSE----");
	                
	                try {
	                    htSE.call(SOAP_ACTION, envelope);
	                    Log.v("htSE.call", "Ok----htSE.call()----");
	                } catch (IOException e) {
	                    e.printStackTrace();
	                } catch (XmlPullParserException e) {
	                    e.printStackTrace();
	                }
	                
	                try {
	                    if(envelope.getResponse()!=null){
	                        Log.v("envelope.getResponse()", "Ok----envelope.getResponse()----");
	                        
	                        //get the response of web service
	                        result = envelope.getResponse();
	                        

	                      //parse the response message
	                        String st=result.toString();
	                        result_list =st;
	                        //result_list=st.substring(1, st.length()-1);
	                        //int size = result_list.split(",").length;
	                        String [] sResult=st.split(";");
	                        int size =sResult.length;
	                        if (size == 1){
	                            info.add("can not find results!");
	                        }else
	                        {
	                            for(int i=0;i<size;i++){
	                                if(i%2 == 0)
	                                info.add("shop:"+sResult[i]);
	                                else
	                                info.add("price:"+sResult[i]);
	                            }
	                        }
	                        Log.v("envelope.getResponse()", st);
	                        
	                        myHandler.sendEmptyMessage(0);
	                    }
	                } catch (SoapFault e) {
	                    e.printStackTrace();
	                }  
	            }
		    
		}).start();
		

	}
			
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.save_history, menu);
         return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	int item_id=item.getItemId();
	
	switch(item_id){

	//put response message into bundle and send to next activity
		case R.id.save_history:{
			Intent intent_result =new Intent(Barcode2WS.this,HistoryList.class);
			Bundle bundle_result =new Bundle();
			bundle_result.putString("product_info", result_list);
			bundle_result.putString("key_search_item", code);
			intent_result.putExtras(bundle_result);
			startActivity(intent_result);
		}
	}
	return true;
    }
   }     
