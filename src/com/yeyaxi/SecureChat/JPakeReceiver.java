package com.yeyaxi.SecureChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
/**
 * JPakeReceiver - SMS Receiver for JPake
 * @author Yaxi Ye
 *
 */
public class JPakeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage[] messages = new SmsMessage[pdus.length];
		String body = "";
		for (int i = 0; i < pdus.length; i++) {
			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
		}
		SmsMessage sms = messages[0];
		try {
			if (messages.length == 1 || sms.isReplace()) {
				body = sms.getDisplayMessageBody();
				
			}
			else {
				StringBuilder bodyText = new StringBuilder();
				for (int i = 0; i < messages.length; i++) {
					bodyText.append(messages[i].getMessageBody());
				}
				body = bodyText.toString();
			}
		}
		catch (Exception e) {
			
		}
		Intent intent_Jpake = new Intent(context, JPakeActivity.class);
		intent_Jpake.putExtra("SMS", body);
		context.startActivity(intent_Jpake);

		
	}
	

}
