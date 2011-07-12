package com.yeyaxi.SecureChat;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AESEncryptActivity extends Activity {

	private TextView PlainTextView;
	public EditText PlainMessage;
	private TextView SecretTextView;
	public EditText SecretText;
	public Button EncryptButton;
	private TextView EncryptTextView;
	public TextView EncryptedMessage;
	public Button SendButton;
	
    public class AES implements AESInterface {
		public String AESEncrypt(String sKey, String PlainMsg)
				throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			//Try use some Android based alert dialog to catch this exception.
			if (sKey == null) {
				throw new IllegalArgumentException ("NULL Secret NOT ALLOWED!");
			}
			/*
			//Get bytes from the secret user has entered.
			byte[] key = sKey.getBytes("UTF-8");
			//Hash the secret key into 256 bit (32 bytes)
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			key = sha256.digest(key);
			
			Log.d("SecureChat", key.toString());
			//Generate the AES key from user entered secret
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES"); 
			//Initial Cipher
			Cipher cipher = Cipher.getInstance("AES");
			//Launch Encrypt Procedure
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] cipherText = PlainMsg.getBytes("UTF-8");
			cipher.doFinal(cipherText);
			//Display Base64 Encoded CipherText
			String cipherTextBase64 = Base64.encodeToString(cipherText, 0);
			//EncryptedMessage.setText(cipherTextBase64);
			return cipherTextBase64;
			*/
			
			//Test for New method
			
			
			//First Initialize KeyGenerator
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			//sKey is the user entered secret key
			sr.setSeed(sKey.getBytes("UTF-8"));
			kgen.init(256, sr);
			SecretKey secret = kgen.generateKey();
			//Get secret raw key from user entered
			byte[] rawKey = secret.getEncoded();
			//Encrypt start
			SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] cipherText = cipher.doFinal(PlainMsg.getBytes("UTF-8"));
			String cipherTextBase64 = Base64.encodeToString(cipherText, 0);
			return cipherTextBase64;
		}

		@Override
		public String AESDecrypt(String SecretKey, String EncryptMsg)
				throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			/**
			 * For Implementation,
			 * @see AESDecryptActivity
			 */
			//Get bytes from the secret user has entered
			//byte[] key = SecretKey.getBytes("UTF-8");
			//Generate the AES key from user entered secret
			//SecretKeySpec sKey = new SecretKeySpec(key, "AES");
			//Initial Cipher
			//Cipher cipher = Cipher.getInstance("AES");
			//Launch Decrypt Procedure
			//cipher.init(Cipher.DECRYPT_MODE, sKey);
			//byte [] plainText = Base64.decode(EncryptMsg.getBytes(), 0);
			//cipher.doFinal(plainText);
			//Display the decrypted text
			//return new String(plainText);
			return null;
		}
    	
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        //super.onCreate(savedInstanceState);
    	super.onCreate(icicle);
        setContentView(R.layout.aesencrypt);
        PlainTextView = (TextView) findViewById(R.id.textView1);
        PlainMessage = (EditText) findViewById(R.id.PlainTxt);
        SecretTextView = (TextView) findViewById(R.id.textView2);
        SecretText = (EditText)	findViewById(R.id.SecretTxt);
        EncryptButton = (Button) findViewById(R.id.EncryptButton);
        EncryptTextView = (TextView) findViewById(R.id.textView3);
        EncryptedMessage = (TextView) findViewById(R.id.EncryptMsg);
        SendButton = (Button) findViewById(R.id.SendButton);
        //Set Encrypt Button's event
        EncryptButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		AES aes = new AES();
        		try {
					String plainTxt = aes.AESEncrypt(SecretText.getText().toString(), PlainMessage.getText().toString());
        			EncryptedMessage.setText(plainTxt);
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
    }
    public boolean onCreatOptionsMenu (Menu menu) {
		popMenu(menu);
    	return (super.onCreateOptionsMenu(menu));
    }
	private void popMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Home");
		menu.add(Menu.NONE, 0, Menu.NONE, "Decrypt");
	}
}