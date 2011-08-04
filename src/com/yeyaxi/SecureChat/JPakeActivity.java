/**
 * 
 */
package com.yeyaxi.SecureChat;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


/**
 * @author Yaxi Ye
 *
 */
public class JPakeActivity extends Activity {
    /** Called when the activity is first created. */
    public ArrayList received = new ArrayList();
    private ArrayList <String> sendBuffer = new ArrayList <String> ();
    private Button startButton;
    public EditText id;
    public EditText sharedPwd;
    private static final String SMS_SENT = "SMS_SENT_ACTION";
    private static final String SMS_DELIVERED = "SMS_DELIVERED_ACTION";
	Intent mSendIntent = new Intent(SMS_SENT);
	Intent mDeliveryIntent = new Intent(SMS_DELIVERED);
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpake);
        //Context context = new Context();
        //tManager.getDeviceId();
        id = (EditText)findViewById(R.id.editText1);
        sharedPwd = (EditText)findViewById(R.id.editText2);
        startButton = (Button)findViewById(R.id.button1);
    }
    public String getUID() {
        TelephonyManager tManager = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("SecureChat" , "DeviceID: " + tManager.getDeviceId());
        return tManager.getDeviceId();
    }
    
    public void onStart(){
    	super.onStart();
    	final JPake jpake = new JPake();

		//Get phone's IMEI
    	String signerId = jpake.GetSignerId();
		//JPake step1
		try {
			jpake.step1(signerId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Send the results of step1 via SMS to the receiver
		    	String GX1 = jpake.step1Result.get(0).toString();
		    	String SIGX1 = jpake.step1Result.get(1).toString();
		    	String GX2 = jpake.step1Result.get(2).toString();
		    	String SIGX2 = jpake.step1Result.get(3).toString();
		    	sendBuffer.add(0, GX1);
		    	sendBuffer.add(1, SIGX1);
		    	sendBuffer.add(2, GX2);
		    	sendBuffer.add(3, SIGX2);
		    	sendJpake(sendBuffer);
			}
		});
		
    	
    	
    }
    
    private void sendJpake(ArrayList list) {
    	if (list.isEmpty() == false) {
    		//TODO Try to use "for" clause if managed to figure out how to handle the "received" notification.
    		for (int i = 0; i < list.size(); i++) {
        		sendSMS(Constants.SMS_RECIPIENT, list.get(i).toString());
    		}
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(0).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(1).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(2).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(3).toString());

    	}
    	else {
    		Log.e("SecureChat", "Send Buffer Empty, Check JPake computation!", new IllegalArgumentException ("Send Buffer Error"));
    		throw new IllegalArgumentException ("Send Buffer Empty");
    	}
    }
    
    private void sendSMS (String phoneNum, String message) {
    	//PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, AESEncryptActivity.class), 0);
    	ArrayList <PendingIntent> sentIntents = new ArrayList <PendingIntent>();
    	ArrayList <PendingIntent> deliveryIntents = new ArrayList <PendingIntent>();
    	SmsManager sms = SmsManager.getDefault();
    	//Divide message into several parts
    	ArrayList <String> parts = sms.divideMessage(message);
    	//Initialize intents
    	for (int i = 0; i < parts.size(); i++) {
    		sentIntents.add(PendingIntent.getBroadcast(this, 0, mSendIntent, 0));
    		deliveryIntents.add(PendingIntent.getBroadcast(this, 0, mDeliveryIntent, 0));
    	}
		sms.sendMultipartTextMessage(phoneNum, null, parts, sentIntents, deliveryIntents);   	
    }
    
    private static class SmsBroadcastReceiver extends BroadcastReceiver {
    	

		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
		
    	
    }
    
    public void onResume() {
    	super.onResume();
    	//TODO Save received strings into ArrayList
		Bundle bundleReceiver = getIntent().getExtras();
		if (bundleReceiver != null) {
			String msg = bundleReceiver.getString("SMS");
			int i = 0;
			if (!received.contains(i)){
				++i;
				received.add(i, msg);
			}
		}
    }
    public void onPause() {
    	
    }
    public void onStop() {
    	super.onStop();
    	//Unregister the Receiver after Authentication procedure has finished. 
    	//Reference: http://stackoverflow.com/questions/6529276/android-how-to-unregister-a-receiver-created-in-the-manifest/6529365#6529365

    }
}

