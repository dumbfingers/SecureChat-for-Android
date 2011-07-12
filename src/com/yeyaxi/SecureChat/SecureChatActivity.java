package com.yeyaxi.SecureChat;

//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Intent;
//import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.util.Base64;
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
			//TODO Implement Menu options, when selected Encrypt in Menu
			startActivity(new Intent(this, AESEncryptActivity.class));
			return (true);
		case 1:
			//TODO Implement Menu options, when selected Decrypt in Menu
			return (true);
		}
		return false;
		
	}
}