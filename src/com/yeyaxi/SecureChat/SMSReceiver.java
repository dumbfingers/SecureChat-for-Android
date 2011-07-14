package com.yeyaxi.SecureChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle b = intent.getExtras();
		SmsMessage[] msgs = null;
		String str = "";
		if (b != null)
		{
			//Retrieve the SMS message received
			Object[] pdus = (Object[]) b.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				//str += "SMS from" + msgs[i].getOriginatingAddress();
				//str += ":";
				str += msgs[i].getMessageBody().toString();
				//str += "\n";
			}
			//TODO Display the received message directly into the decryptActivity
			//AESDecryptActivity a = new AESDecryptActivity();
			//a.CipherText.append(str);
			//Toast.makeText(bundle, str, Toast.LENGTH_SHORT).show();
			Intent intentMsg = new Intent(context, AESDecryptActivity.class);
			Bundle bundleReceiver = new Bundle();
			bundleReceiver.putString("msg", str);
			intentMsg.putExtras(bundleReceiver);
			//Seemed no need to add NEW TASK FLAG here
			context.startActivity(intentMsg);
		}		
	}

}
