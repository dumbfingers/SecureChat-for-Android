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
    private ArrayList<String> sendBuffer = new ArrayList<String> ();
    private Button packet1Button;
    private Button packet2Button;
    
    private static final String SMS_SENT = "com.yeyaxi.SMS_SENT_ACTION";

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
	private int flag;

	
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
        packet2Button = (Button)findViewById(R.id.button2);
        packet1Button.setOnClickListener(l);
        //send packet 2 Button created invisibly.
        packet2Button.setVisibility(packet2Button.INVISIBLE);
        IntentFilter filterSms = new IntentFilter();
        filterSms.addAction("Message");
        
        registerReceiver(smsReceiver, filterSms);

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
	    		sendMessageThread.start();
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
    		BigInteger gx3 = new BigInteger((String)received.get(0));
    		BigInteger[] sigX3 = null;
    		sigX3[0] = new BigInteger((String)received.get(1));
    		sigX3[1] = new BigInteger((String)received.get(2));
    		BigInteger gx4 = new BigInteger((String)received.get(3));
    		BigInteger[] sigX4 = null;
    		sigX4[0] = new BigInteger((String)received.get(4));
    		sigX4[1] = new BigInteger((String)received.get(5));
    		if (gx4.equals(BigInteger.ONE) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx3, sigX3, getUID()) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx4, sigX4, getUID())) {
    			Log.e("SecureChat", "gx4 equals 1 or invalid sigX3 and sigX4 or sigX1 and sigX2");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP1 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP1 validation passed!", Toast.LENGTH_SHORT).show();
    			uid = (String)(received.get(6));
    			//Push gx4 or gx2 into sessionKeyBuffer
    			sessionKeyBuffer.add(0, gx4);
    			//Push x2 or x4 into sessionKeyBuffer
    			sessionKeyBuffer.add(1, new BigInteger((String)jpake.step1Result.get(6)));
    			//Push (BigInteger)pwd into sessionKeyBuffer
    			sessionKeyBuffer.add(2, jpake.GetPassWord(secret));
    			//Push q into sessionKeyBuffer
    			sessionKeyBuffer.add(3, jpake.q);
    			//Push p into sessionKeyBuffer
    			sessionKeyBuffer.add(4, jpake.p);
    			
    			//received.clear();
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
    		BigInteger gB = new BigInteger((String)received.get(7));
    		BigInteger B = new BigInteger((String)received.get(8));
    		BigInteger[] sigX4s = null;
    		sigX4s[0] = new BigInteger((String)received.get(9));
    		sigX4s[1] = new BigInteger((String)received.get(10));
    		if (!jpake.verifyZKP(jpake.p, jpake.q, gB, B, sigX4s, uid)) {
    			Log.e("SecureChat", "Invalid sigX4s or sigX2s");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP2 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP2 validation passed!", Toast.LENGTH_SHORT).show();
    			//Push B or A into sessionKeyBuffer
    			sessionKeyBuffer.add(5, B);
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
//    private void sendJpake(ArrayList list) {
//    	if (list.isEmpty() == false) {
//    		//TODO Try to use "for" and "if" clause if managed to figure out how to handle the "received" notification.
//
//    		
//    	}
//    	else {
//    		Log.e("SecureChat", "Send Buffer Empty, Check JPake computation!", new IllegalArgumentException ("Send Buffer Error"));
//    		throw new IllegalArgumentException ("Send Buffer Empty");
//    	}
//    }
    /**
     * sendSMS - Method for sending SMS
     * @param phoneNum - SMS recipient
     * @param message - SMS content
     */
    private void sendSMS (String phoneNum, String message) {

    	SmsManager sms = SmsManager.getDefault();
    	//Divide message into several parts
    	ArrayList <String> parts = sms.divideMessage(message);

    	for (String msg : parts) {
    		sms.sendTextMessage(phoneNum, null, msg, PendingIntent.getBroadcast(JPakeActivity.this, 0, new Intent(SMS_SENT), 0), null);   	
    	}
    }
    

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals("Message")) {
    			String msg = intent.getStringExtra("SMS");
    			Log.d("SecureChat", "SMS " + msg);
    			received.add(received.size(), msg);
    			Log.d("SecureChat", "JPakeActivity, Received " + received.size());
    			Toast.makeText(context, "Received " + received.size() + " packet(s) in total.", Toast.LENGTH_SHORT).show();
    			//Log.d("SecureChat", "Content " + received.get(0));
    			if (received.size() == 11) {
    				//Fire up a Thread
    				handlePackets.start();
    			}
    			if ((flag == 1) && (received.size() == 1)) {
    				String key = (String)(received.get(0));
    				if(checkSessionKey(key, sessionKey)) {
    					Toast.makeText(getBaseContext(), "Secure Connection Established!", Toast.LENGTH_LONG).show();
    				}
    			}
    		}
    		
    	}
    };
    
    private Thread sendMessageThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!sendBuffer.isEmpty()) {
				//sendJpake(sendBuffer);
				for (String msg:sendBuffer) {
					sendSMS(phoneNum, msg);
					
					try {
						Thread.sleep(Constants.SEND_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else {
				Log.e("SecureChat", "sendBuffer is Empty!");
				throw new NullPointerException("sendBuffer is Empty!");
			}
		}
    	
    });
    

    //Thread for onReceive method to process Packet sent from Bob and take on the JPake
    private Thread handlePackets = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(checkZKP1()) {
			    	//Step 2 of JPake
					try {
						Log.d("SecureChat", "SendBuffer will be cleared! Size before clear: " + sendBuffer.size());
						sendBuffer.clear();
						Log.d("SecureChat", "SendBuffer Cleared!");
						/*
						 * step1Result.get(0) - gx1 or gx3
						 * step1Result.get(1) -
						 */ 

						jpake.step2(new BigInteger((String)jpake.step1Result.get(0)), new BigInteger((String)received.get(0)), new BigInteger((String)received.get(3)), new BigInteger((String)jpake.step1Result.get(6)), jpake.GetPassWord(secret), signerId);

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
					if(checkZKP2()) {
						//Push Results of Step2 into sendBuffer
						for (int i = 0; i < jpake.step2Result.size(); i++) {
							sendBuffer.add(i, jpake.step2Result.get(i));
						}
					}
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
				Toast.makeText(getBaseContext(), "Session Key successfully Computed!", Toast.LENGTH_SHORT).show();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendBuffer.add(4, sessionKey);
			if (!sendBuffer.isEmpty()) {
				sendMessageThread.start();
			}
			else {
				Log.e("SecureChat", "sendBuffer is Empty!");
				throw new NullPointerException("sendBuffer is Empty!");
			}
			//Set flag
			flag = 1;
			received.clear();
			//Set send packet 2 button to be visible.
			//packet2Button.setVisibility(packet2Button.VISIBLE);
		}
    });
    	
    
    public void onResume() {
    	super.onResume();
    	//Register Broadcast Receiver
    	/*
    	smsReceiver = new SmsBroadcastReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(SMS_SENT);
    	super.onResume();
    	*/
    	//TODO Save received strings into ArrayList
//    	String msg = null;
//		Bundle bundleReceiver = getIntent().getExtras();
//		if (bundleReceiver != null) {
//			msg = bundleReceiver.getString("SMS");
//			//Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
//			//Find if the receiver buffer contains, then save the received msg.
//			for (int counter = 0; counter < 11; counter ++) {
//			    if (!received.contains(counter)) {
//			        received.add(counter, msg);
//					break;
//			    }
//			}
//			//TODO While received contains enough data, launch checkZKP1()
//			try {
//				checkZKP1();
//			} catch (NoSuchAlgorithmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
    	//onResume does not moniter received buffer. Method below won't work
//    	if (received.size() == 3) {
//    		Log.d("SecureChat", "onResume Triggered SUCCESSFULLY!");
//    	}
		
		
    	
    }
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(smsReceiver);
    }
    public void onStop() {
    	super.onStop();
    	//Unregister the Receiver after Authentication procedure has finished. 
    	//Reference: http://stackoverflow.com/questions/6529276/android-how-to-unregister-a-receiver-created-in-the-manifest/6529365#6529365
    	handlePackets.stop();
    }
    
    private boolean checkSessionKey(String key1, String key2) {
		
    	if (key1.equals(key2)) {
    		return true;
    	}
    	else {
    	
    		return false;
    	}
    }
}

