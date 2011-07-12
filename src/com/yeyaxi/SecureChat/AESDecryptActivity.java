package com.yeyaxi.SecureChat;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AESDecryptActivity extends Activity{
	
	private EditText CipherText;
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
		
		Decrypt.setOnClickListener(new OnClickListener() {
			public void onClick (View view) {
				AES aes = new AES();
				try {
					String cipherText = aes.AESDecrypt(SecretText.getText().toString(), CipherText.getText().toString());
					PlainMessage.setText(cipherText);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
	public class AES implements AESInterface {

		//TODO Add Buttons, EditText, etc
		//TODO Finish aesdecrypt.xml
		
		@Override
		public String AESEncrypt(String sKey, String PlainMsg)
				throws Exception {
			/**
			 * For Full Implementation,
			 * @see AESEncryptActivity
			 */
			return null;
		}

		@Override
		public String AESDecrypt(String sKey, String EncryptMsg)
				throws Exception {
			/*
			//Get bytes from the secret user has entered
			byte[] key = sKey.getBytes("UTF-8");
			//Generate the AES key from user entered secret
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			//Initial Cipher
			Cipher cipher = Cipher.getInstance("AES");
			//Launch Decrypt Procedure
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte [] plainText = Base64.decode(EncryptMsg.getBytes(), 0);
			cipher.doFinal(plainText);
			//Display the decrypted text
			return new String(plainText);
			*/
			byte[] rawKey = getRawKey(sKey.getBytes("UTF-8"));
			SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] plainText = Base64Decoded(EncryptMsg.getBytes());
			cipher.doFinal(plainText);
			return plainText.toString();
		}

		@Override
		public byte[] getRawKey(byte[] seed) throws Exception {
			//Initialize the KeyGenerator
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			//Init for 256bit AES key
			kgen.init(256, sr);;
			SecretKey secret = kgen.generateKey();
			//Get secret raw key
			byte[] rawKey = secret.getEncoded();
			return rawKey;
		}
/**
 * 
 * @param toBeDecoded
 * @return
 */
		public byte[] Base64Decoded(byte[] toBeDecoded) {
			byte[] decoded = Base64.decode(toBeDecoded, 0);
			return decoded;
		}
		
	}

}
