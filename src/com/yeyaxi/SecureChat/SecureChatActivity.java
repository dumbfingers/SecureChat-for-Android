package com.yeyaxi.SecureChat;

import android.app.Activity;
import android.content.Intent;
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
			startActivity(new Intent(this, AESEncryptActivity.class));
			return (true);
		case 1:
			//TODO Implement Menu options, when selected Decrypt in Menu
			startActivity(new Intent(this, AESDecryptActivity.class));
			return (true);
		}
		return false;
		
	}
}