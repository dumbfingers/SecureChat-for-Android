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
import android.widget.Toast;
/**
 * JPakeActivityB - For Bob
 * @author Yaxi Ye
 *
 */
public class JPakeActivityB extends Activity{
    public ArrayList received = new ArrayList();
    private ArrayList sendBuffer = new ArrayList();
    private Button packetButton;
    
    private static final String SMS_SENT = "com.yeyaxi.SMS_SENT_ACTION";
	//For storing the other's uid
    private String uid;
	//For store the session Key
	public String sessionKey;
	//Initialize JPake
	final JPake jpake = new JPake();
	private String signerId;
	private String secret;
	private String phoneNum;
	private int flag;
	//For temp save the BigInteger in order to compute the session key
	private ArrayList sessionKeyBuffer = new ArrayList();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jpakeb);
		packetButton = (Button)findViewById(R.id.button1b);
		packetButton.setVisibility(packetButton.INVISIBLE);
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
        
        packetButton.setOnClickListener(l);
        
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
	    	
			try {
				jpake.step2((BigInteger)(jpake.step1Result.get(0)), (BigInteger)(received.get(0)), ((BigInteger)received.get(3)), (BigInteger)(jpake.step1Result.get(6)), jpake.GetPassWord(secret), signerId);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 7; i < 11; i++) {
				for (int j = 0; j < 4; j++) {
					sendBuffer.add(i, jpake.step2Result.get(j));
				}
			}
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
    
    private void sendJpake(ArrayList list) {
    	if (list.isEmpty() == false) {
    		//TODO Try to use "for" and "if" clause if managed to figure out how to handle the "received" notification.
    		for (int i = 0; i < list.size(); i++) {
				sendSMS(phoneNum, ((BigInteger)(list.get(i))).toString(16));
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

    	SmsManager sms = SmsManager.getDefault();
    	//Divide message into several parts
    	ArrayList <String> parts = sms.divideMessage(message);

    	for (String msg : parts) {
    		sms.sendTextMessage(phoneNum, null, msg, PendingIntent.getBroadcast(JPakeActivityB.this, 0, new Intent(SMS_SENT), 0), null);   	
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
    			
    			//
    			if (received.size() == 7) {
    				//Fire up a Thread
    				step1.start();
    			}
    			if ((received.size() == 5) && flag == 1) {
    				step2.start();
    			}
    		}
    		
    	}
    };
    
    Thread step1 = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(checkZKP1()) {
					//Enable the send button to send first packet of Bob.
					packetButton.setVisibility(packetButton.VISIBLE);
					//set flag
					flag = 1;
					//Clear received buffer in order to receive the next packet
					received.clear();
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    });
    
    Thread step2 = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (checkZKP2()) {
					//Get Alice's SessionKey from received buffer.
					//received.get(4);
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
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Compare the sessionKey
			String aliceKey = (String)(received.get(4));
			if (checkSessionKey(aliceKey, sessionKey)) {
				Toast.makeText(getBaseContext(), "Secure Session has been Successfully Established!", Toast.LENGTH_LONG).show();
				//send Bob's session key back to Alice
				sendSMS(phoneNum, sessionKey);
			}
		}
    	
    });
    
    /**
     * checkZKP1 - Method of ZKP checking in Step 1 of Jpake
     * @return true if checking passed, false for checking failed.
     * @throws NoSuchAlgorithmException
     */
    private boolean checkZKP1 () throws NoSuchAlgorithmException {
    	if (!received.isEmpty()) {
    		//check the ZKP
    		//Rip the Alice's g1, sigx1, g2, sigx2 from received buffer
    		//Cast from Object to Integer
    		BigInteger gx1 = (BigInteger)received.get(0);
    		BigInteger[] sigX1 = null;
    		sigX1[0] = (BigInteger)(received.get(1));
    		sigX1[1] = (BigInteger)(received.get(2));
    		BigInteger gx2 = (BigInteger)(received.get(3));
    		BigInteger[] sigX2 = null;
    		sigX2[0] = (BigInteger)(received.get(4));
    		sigX2[1] = (BigInteger)(received.get(5));
    		if (gx2.equals(BigInteger.ONE) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx1, sigX1, getUID()) || !jpake.verifyZKP(jpake.p, jpake.q, jpake.g, gx2, sigX2, getUID())) {
    			Log.e("SecureChat", "gx2 equals 1 or invalid sigX3 and sigX4 or sigX1 and sigX2");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP1 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP1 validation passed!", Toast.LENGTH_SHORT).show();
    			uid = (String)(received.get(6));
    			//Push gx4 or gx2 into sessionKeyBuffer
    			sessionKeyBuffer.add(0, gx2);
    			//Push x2 or x4 into sessionKeyBuffer
    			sessionKeyBuffer.add(1, (BigInteger) (jpake.step1Result.get(6)));
    			//Push (BigInteger)pwd into sessionKeyBuffer
    			sessionKeyBuffer.add(2, jpake.GetPassWord(secret));
    			//Push q into sessionKeyBuffer
    			sessionKeyBuffer.add(3, jpake.q);
    			//Push p into sessionKeyBuffer
    			sessionKeyBuffer.add(4, jpake.p);
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
    		//Rip gA, A, sigX2s from received buffer
    		BigInteger gA = (BigInteger)received.get(0);
    		BigInteger A = (BigInteger)received.get(1);
    		BigInteger[] sigX2s = null;
    		sigX2s[0] = (BigInteger)received.get(2);
    		sigX2s[1] = (BigInteger)received.get(3);
    		if (!jpake.verifyZKP(jpake.p, jpake.q, gA, A, sigX2s, uid)) {
    			Log.e("SecureChat", "Invalid sigX4s or sigX2s");
    			return false;
    		}
    		else {
    			Log.d("SecureChat", "ZKP2 check OK");
    			Toast.makeText(getApplicationContext(), "ZKP2 validation passed!", Toast.LENGTH_SHORT).show();
    			//Push B or A into sessionKeyBuffer
    			sessionKeyBuffer.add(5, A);
    			return true;
    		}
    	}
    	else {
    		Log.e("SecureChat", "Received buffer error.");
    		return false;
    	}
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

