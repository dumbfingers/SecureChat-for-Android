package com.yeyaxi.SecureChat;
import java.io.IOException;
import java.security.InvalidKeyException;
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
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AESEncryptActivity extends Activity {

	//private TextView PlainTextView;
	public EditText PlainMessage;
	//private TextView SecretTextView;
	public EditText SecretText;
	public Button EncryptButton;
	//private TextView EncryptTextView;
	public TextView EncryptedMessage;
	public Button SendButton;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        //super.onCreate(savedInstanceState);
    	super.onCreate(icicle);
        setContentView(R.layout.aesencrypt);
        //PlainTextView = (TextView) findViewById(R.id.textView1);
        PlainMessage = (EditText) findViewById(R.id.PlainTxt);
        //SecretTextView = (TextView) findViewById(R.id.textView2);
        SecretText = (EditText)	findViewById(R.id.SecretTxt);
        EncryptButton = (Button) findViewById(R.id.EncryptButton);
        //EncryptTextView = (TextView) findViewById(R.id.textView3);
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
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        });
        SendButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		//TODO Implement the Send function via SMS.
        		sendSMS(Constants.SMS_RECIPIENT,EncryptedMessage.getText().toString());
        	}
        });
    }
    
    private void sendSMS (String phoneNum, String message) {
    	PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, AESEncryptActivity.class), 0);
    	SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNum, null, message, pi, null);   	
    }
    
    public class AES implements AESInterface {
		public String AESEncrypt(String sKey, String PlainMsg)
				throws Exception {
			//Try use some Android based alert dialog to catch this exception.
			if (sKey == null) {
				Log.e("SecureChat", "IllegalArgumentException Catched");
				throw new IllegalArgumentException ("NULL Secret NOT ALLOWED!");
			}			
			
			byte[] rawKey = getRawKey(sKey.getBytes("UTF-8"));
			//byte[] rawKey = getRawKey(sKey.getBytes());
			//Encrypt start
			SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] cipherText = cipher.doFinal(PlainMsg.getBytes("UTF-8"));
			//byte[] cipherText = cipher.doFinal(PlainMsg.getBytes());
			//String cipherTextBase64 = Base64.encodeToString(cipherText, 0);
			//return cipherTextBase64;
			return Base64Encoded(cipherText);			
		}

		@Override
		public String AESDecrypt(String SecretKey, String EncryptMsg)
				throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			/**
			 * For Implementation,
			 * @see AESDecryptActivity
			 */
			return null;
		}

		@Override
		public byte[] getRawKey(byte[] seed) throws Exception {
			//Initialize the KeyGenerator
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			//Init for 256bit AES key
			kgen.init(Constants.AES_KEY_SIZE, sr);;
			SecretKey secret = kgen.generateKey();
			//Get secret raw key
			byte[] rawKey = secret.getEncoded();
			return rawKey;
		}

		public String Base64Encoded(byte[] toBeEncoded) {
			String encoded = Base64.encodeToString(toBeEncoded, 0);
			return encoded;
		}
    	
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