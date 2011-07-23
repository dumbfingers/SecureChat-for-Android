package com.yeyaxi.SecureChat;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
        		Encrypt aes = new Encrypt();
        		try {
					String plainTxt = aes.AESEncrypt(SecretText.getText().toString(), PlainMessage.getText().toString());
        			EncryptedMessage.setText(plainTxt);
        			//EncryptedMessage.setTextIsSelectable(true); It'll be worked since API Lv.11
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
    
	private void popMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Home");
		menu.add(Menu.NONE, 0, Menu.NONE, "Decrypt");
	}
    
    public class Encrypt {
    	
		public String AESEncrypt(String seed, String plainMsg)
				throws Exception {
			/*
			//Try use some Android based alert dialog to catch this exception.
			if (seed == null) {
				Log.e("SecureChat", "IllegalArgumentException Catched");
				throw new IllegalArgumentException ("NULL Secret NOT ALLOWED!");
			}
			*/
			
			//Encrypt start
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = encrypt(rawKey, plainMsg.getBytes());
			//return toHex(result);
			return Base64.encodeToString(result, 0);
		}
		private byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(plain);
			return encrypted;
		}

		public byte[] getRawKey(byte[] seed) throws Exception {
			
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			//Init for 256bit AES key
			kgen.init(Constants.AES_KEY_SIZE, sr);;
			SecretKey secret = kgen.generateKey();
			//Get secret raw key
			byte[] raw = secret.getEncoded();
			return raw;
			
		}

		
		//Hex Mode
	    public String toHex(String txt) {
	        return toHex(txt.getBytes());
	    }
	    public String fromHex(String hex) {
	        return new String(toByte(hex));
	    }

	    public byte[] toByte(String hexString) {
	        int len = hexString.length()/2;
	        byte[] result = new byte[len];
	        for (int i = 0; i < len; i++)
	            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
	        return result;
	    }

	    public String toHex(byte[] buf) {
	        if (buf == null)
	            return "";
	        StringBuffer result = new StringBuffer(2*buf.length);
	        for (int i = 0; i < buf.length; i++) {
	            appendHex(result, buf[i]);
	        }
	        return result.toString();
	    }
	    private final String HEX = "0123456789ABCDEF";
	    private void appendHex(StringBuffer sb, byte b) {
	        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	    }
    	
    }
    
    public boolean onCreatOptionsMenu (Menu menu) {
		popMenu(menu);
    	return (super.onCreateOptionsMenu(menu));
    }

}