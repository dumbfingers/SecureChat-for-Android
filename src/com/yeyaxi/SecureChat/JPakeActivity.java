/**
 * 
 */
package com.yeyaxi.SecureChat;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * @author Yaxi Ye
 *
 */
public class JPakeActivity extends Activity {
    /** Called when the activity is first created. */
    public ArrayList received = new ArrayList();
    private ArrayList sendBuffer = new ArrayList();
    private Button packet1Button;
    
    private static final String SMS_SENT = "com.yeyaxi.SMS_SENT_ACTION";
    //private static final String SMS_DELIVERED = "com.yeyaxi.SMS_DELIVERED_ACTION";
	//Intent mSendIntent = new Intent().setAction(SMS_SENT);
	//Intent mDeliveryIntent = new Intent().setAction(SMS_DELIVERED);
	//private SmsBroadcastReceiver smsReceiver;
	//For store the receiver's uid
	private String uid;
	//For store the session Key
	public String sessionKey;
	//Initialize JPake
	final JPake jpake = new JPake();
	private String signerId;
	private String secret;
	private String phoneNum;
	//For temp save the BigInteger in order to compute the session key
	private ArrayList <BigInteger> sessionKeyBuffer = new ArrayList <BigInteger>();
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpake);
        //Register broadcast receiver for SMS send and delivered intents.
        registerReceiver(new BroadcastReceiver() {
        	@Override
        	public void onReceive(Context context, Intent intent) {
                String message = null;
                boolean error = true;
                switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "Message sent!";
                    error = false;
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Error.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Error: Radio off.";
                    break;
                }
        	}
        }, new IntentFilter(SMS_SENT));
        
        packet1Button = (Button)findViewById(R.id.button1);
        packet1Button.setOnClickListener(l);

    }
	
	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Get phone's IMEI
			signerId = getUID();
	    	Bundle extras = getIntent().getExtras();
	    	secret = extras.getString("secret");
	    	phoneNum = extras.getString("phoneNumber");
			try {
				jpake.step1(signerId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			//Buffer gx1/gx3, sigX1/sigX3, gx2/gx4, sigX2/sigX4
			for (int i = 0; i < 6; i++) {
				sendBuffer.add(i, jpake.step1Result.get(i));
			}
	    	//Buffer signerID
	    	sendBuffer.add(6, getUID());
			//Send the results of step1 via SMS to the receiver
	    	if (!sendBuffer.isEmpty()) {
	    		sendJpake(sendBuffer);
	    	}
	    	else {
	    		Log.e("SecureChat", "sendBuffer is Empty!");
	    		throw new NullPointerException("sendBuffer is Empty!");
	    	}
		}
		
	};
	
	
    public String getUID() {
        TelephonyManager tManager = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("SecureChat" , "DeviceID: " + tManager.getDeviceId());
        return tManager.getDeviceId();
    }
    
    public void onStart(){
    	super.onStart();

    	/*
    	try {
			checkZKP1();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	//Step 2 of JPake
    	try {
			if (checkZKP1()){
				try {
					sendBuffer.clear();
					/*
					 * step1Result.get(0) - gx1 or gx3
					 * step1Result.get(1) - 
					 
					jpake.step2((BigInteger)(jpake.step1Result.get(0)), (BigInteger)(received.get(0)), ((BigInteger)received.get(3)), (BigInteger)(jpake.step1Result.get(6)), jpake.GetPassWord(secret), signerId);
					//Push Results of Step2 into sendBuffer
					for (int i = 0; i < jpake.step2Result.size(); i++) {
						sendBuffer.add(i, jpake.step2Result.get(i));
					}
					if (!sendBuffer.isEmpty()) {
						sendJpake(sendBuffer);
					}
					else {
						Log.e("SecureChat", "sendBuffer is Empty!");
			    		throw new NullPointerException("sendBuffer is Empty!");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//Rip results from Step2
    	if (!jpake.step2Result.isEmpty()) {
    		try {
				checkZKP2();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	//Compute the Session Key
    	try {
			sessionKey = jpake.sessionKey((BigInteger)sessionKeyBuffer.get(0), (BigInteger)sessionKeyBuffer.get(1), 
					(BigInteger)sessionKeyBuffer.get(4), (BigInteger)sessionKeyBuffer.get(3), 
					(BigInteger)sessionKeyBuffer.get(5), (BigInteger)sessionKeyBuffer.get(2));
			Log.d("SecureChat", "Session Key: " + sessionKey);
			Toast.makeText(getApplicationContext(), "Session Key successfully Computed!", Toast.LENGTH_SHORT).show();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	*/
	}
    /**
     * checkZKP1 - Method of ZKP checking in Step 1 of Jpake
     * @return true if checking passed, false for checking failed.
     * @throws NoSuchAlgorithmException
     */
    private boolean checkZKP1 () throws NoSuchAlgorithmException {
    	if (!received.isEmpty()) {
    		//check the ZKP
    		//Rip the Bob's g3, sigx3, g4, sigx4 from received buffer
    		//Cast from Object to Integer
    		BigInteger gx3 = (BigInteger)received.get(0);
    		BigInteger[] sigX3 = null;
    		sigX3[0] = (BigInteger)(received.get(1));
    		sigX3[1] = (BigInteger)(received.get(2));
    		BigInteger gx4 = (BigInteger)(received.get(3));
    		BigInteger[] sigX4 = null;
    		sigX4[0] = (BigInteger)(received.get(4));
    		sigX4[1] = (BigInteger)(received.get(5));
    		if (gx4.equals(BigInteger.ONE) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx3, sigX3, getUID()) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx4, sigX4, getUID())) {
    			Log.e("SecureChat", "gx4 equals 1 or invalid sigX3 and sigX4 or sigX1 and sigX2");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP1 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP1 validation passed!", Toast.LENGTH_SHORT).show();
    			uid = (String)(received.get(11));
    			//Push gx4 or gx2 into sessionKeyBuffer
    			sessionKeyBuffer.add(0, gx4);
    			//Push x2 or x4 into sessionKeyBuffer
    			sessionKeyBuffer.add(1, (BigInteger) (jpake.step1Result.get(6)));
    			//Push (BigInteger)pwd into sessionKeyBuffer
    			sessionKeyBuffer.add(2, jpake.GetPassWord(secret));
    			//Push q into sessionKeyBuffer
    			sessionKeyBuffer.add(3, jpake.q);
    			//Push p into sessionKeyBuffer
    			sessionKeyBuffer.add(4, jpake.p);
    			
    			received.clear();
    			return true;
    		}
    	}
    	else {
    		Log.e("SecureChat", "Received buffer error.");
    		return false;
    	}
    	
    }
    /**
     * checkZKP2 - ZKP checking procedure of Jpake Step2	
     * @return true if checking passed, false if checking failed.
     * @throws NoSuchAlgorithmException
     */
    private boolean checkZKP2() throws NoSuchAlgorithmException {
    	if (!received.isEmpty()) {
    		//Check ZKP from Step2
    		//Rip gB, B, sigX4s from received buffer
    		BigInteger gB = (BigInteger)received.get(7);
    		BigInteger B = (BigInteger)received.get(8);
    		BigInteger[] sigX4s = null;
    		sigX4s[0] = (BigInteger)received.get(9);
    		sigX4s[1] = (BigInteger)received.get(10);
    		if (!jpake.verifyZKP(jpake.p, jpake.q, gB, B, sigX4s, uid)) {
    			Log.e("SecureChat", "Invalid sigX4s or sigX2s");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP2 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP2 validation passed!", Toast.LENGTH_SHORT).show();
    			//Push B or A into sessionKeyBuffer
    			sessionKeyBuffer.add(5, B);
    			received.clear();
    			return true;
    		}
    	}
    	else {
    		Log.e("SecureChat", "Received buffer error.");
    		return false;
    	}
    }
    /**
     * sendJpake - Method for passing the Jpake content to the sending procedure.
     * @param list - Jpake content wrapped with ArrayList
     */
    private void sendJpake(ArrayList list) {
    	if (list.isEmpty() == false) {
    		//TODO Try to use "for" and "if" clause if managed to figure out how to handle the "received" notification.
    		//for (int i = 0; i < list.size(); i++) {
        		//sendSMS(phoneNum, (String)list.get(i));
    		//}
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(0).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(1).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(2).toString());
    		//sendSMS(Constants.SMS_RECIPIENT, list.get(3).toString());
			switch(list.size()) {
				//Send Jpake in Step1
				case 7:
					for (int i = 0; i < 6; i++) {
						sendSMS(phoneNum, ((BigInteger)(list.get(i))).toString(16));
					}
					sendSMS(phoneNum, ((String)list.get(6)));
					//Send Jpake in Step2
				case 4:
					for (int i = 0; i < 4; i++) {
						sendSMS(phoneNum, ((BigInteger)(list.get(i))).toString(16));
					}
					
			}

    	}
    	else {
    		Log.e("SecureChat", "Send Buffer Empty, Check JPake computation!", new IllegalArgumentException ("Send Buffer Error"));
    		throw new IllegalArgumentException ("Send Buffer Empty");
    	}
    }
    /**
     * sendSMS - Method for sending SMS
     * @param phoneNum - SMS recipient
     * @param message - SMS content
     */
    private void sendSMS (String phoneNum, String message) {
    	//PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, AESEncryptActivity.class), 0);
    	//ArrayList <PendingIntent> sentIntents = new ArrayList <PendingIntent>();
    	//ArrayList <PendingIntent> deliveryIntents = new ArrayList <PendingIntent>();
    	SmsManager sms = SmsManager.getDefault();
    	//Divide message into several parts
    	ArrayList <String> parts = sms.divideMessage(message);
    	//Initialize intents
    	//for (int i = 0; i < parts.size(); i++) {
    		//sentIntents.add(PendingIntent.getBroadcast(this, 0, mSendIntent, 0));
    		//deliveryIntents.add(PendingIntent.getBroadcast(this, 0, mDeliveryIntent, 0));
    	//}
    	for (String msg : parts) {
    		sms.sendTextMessage(phoneNum, null, msg, PendingIntent.getBroadcast(JPakeActivity.this, 0, new Intent(SMS_SENT), 0), null);   	
    	}
    }
    

    


    public void onResume() {
    	super.onResume();
    	//Register Broadcast Receiver
    	/*
    	smsReceiver = new SmsBroadcastReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(SMS_SENT);
    	super.onResume();
    	//TODO Save received strings into ArrayList
    	int counter = 0;
		Bundle bundleReceiver = getIntent().getExtras();
		if (bundleReceiver != null) {
			String msg = bundleReceiver.getString("SMS");
			//Find if the receiver buffer contains, then save the received msg.
			for (counter = 0; counter < received.size(); counter ++) {
			    if (!received.contains(counter)) {
			        received.add(counter, msg);
					break;
			    }
			}
		}
		*/
    }
    public void onPause() {
    	//unregisterReceiver(smsReceiver);
    }
    public void onStop() {
    	super.onStop();
    	//Unregister the Receiver after Authentication procedure has finished. 
    	//Reference: http://stackoverflow.com/questions/6529276/android-how-to-unregister-a-receiver-created-in-the-manifest/6529365#6529365

    }
}

