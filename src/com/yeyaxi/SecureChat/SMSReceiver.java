package com.yeyaxi.SecureChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage msgs[] = new SmsMessage[messages.length];
		String msgString = "";
		for (int n = 0; n < messages.length; n++) {
			msgs[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			msgString = msgs[n].getMessageBody().toString();
		}
		Intent startDecrypt = new Intent(context, AESDecryptActivity.class);
		startDecrypt.putExtra("SMS", msgString);
		//startActivity(startDecrypt);
		startDecrypt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startDecrypt);
	}
}

