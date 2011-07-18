package com.yeyaxi.SecureChat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
/**
 * 
 * @author Yaxi Ye
 * @version 1
 * @see AES Encryption
 *	
 */

public class SecureChatActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

    public boolean onCreateOptionsMenu(Menu menu) {
    	popMenu(menu);
    	return (super.onCreateOptionsMenu(menu));
    	
    }
    public boolean onOptionsItemSelected(MenuItem item) {
    	return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

	private void popMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Encrypt");
		menu.add(Menu.NONE, 1, Menu.NONE, "Decrypt");
	}
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent startEncrypt = new Intent(this, AESEncryptActivity.class);
			startActivity(startEncrypt);
			return (true);
		case 1:
			//TODO Implement Menu options, when selected Decrypt in Menu
			Intent startDecrypt = new Intent(this, AESDecryptActivity.class);
			startDecrypt.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(startDecrypt);
			return (true);
		}
		return false;
		
	}

}