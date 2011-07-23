package com.yeyaxi.SecureChat;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AESDecryptActivity extends Activity{
	
	public EditText CipherText;
	private EditText SecretText;
	private TextView PlainMessage;
	private Button Decrypt;
	
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.aesdecrypt);
		
		CipherText = (EditText) findViewById(R.id.editText1);
		SecretText = (EditText) findViewById(R.id.editText2);
		PlainMessage = (TextView) findViewById(R.id.textView3);
		Decrypt = (Button) findViewById(R.id.button1);
	}
	public void onStart() {
		super.onStart();
		Decrypt.setOnClickListener(new OnClickListener() {
			public void onClick (View view) {
				Decrypt aes = new Decrypt();
				try {
					String cipherText = aes.AESDecrypt(SecretText.getText().toString(), CipherText.getText().toString());
					PlainMessage.setText(cipherText);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});		
	}
	//For receiving SMS (Value passed from SMSReceiver)
	public void onResume() {
		super.onResume();
		Bundle bundleReceiver = getIntent().getExtras();
		if (bundleReceiver != null) {
			String msg = bundleReceiver.getString("SMS");
			CipherText.setText(msg);
		}
		
	}

	public class Decrypt {

		public String AESDecrypt(String sKey, String EncryptMsg)
				throws Exception {			
			//Old Method
			//byte[] rawKey = getRawKey(sKey.getBytes("UTF-8"));
			byte[] rawKey = getRawKey(sKey.getBytes());
			SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] cipherTxt = EncryptMsg.getBytes(); 
			byte[] plainText = Base64.decode(cipherTxt,0);			
			cipher.doFinal(plainText);
			return new String(plainText);
			
			/*New Method
			byte[] salt = getSalt();
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
			KeySpec spec = new PBEKeySpec(sKey.toCharArray(), salt, 1024, 256);	
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			//byte[] bCipherText = Base64Decoded(EncryptMsg);
			//Hex
			byte[] bCipherText = toByte(EncryptMsg);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			cipher.doFinal(bCipherText);
			return new String(bCipherText);
			*/
		}
		
		/*private byte[] getSalt() throws NoSuchAlgorithmException {
			//Mark for old key method
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
			
			
			/*New key method with some salt
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] ransalt = new byte[20];
			random.nextBytes(ransalt);
			return ransalt;
			
		}*/

		public byte[] getRawKey(byte[] seed) throws Exception {
			//Old Method
			//Initialize the KeyGenerator
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			//Init for 256bit AES key
			kgen.init(Constants.AES_KEY_SIZE, sr);
			SecretKey secret = kgen.generateKey();
			//Get secret raw key
			byte[] rawKey = secret.getEncoded();
			return rawKey;
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
	

}

