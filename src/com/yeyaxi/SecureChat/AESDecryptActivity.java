package com.yeyaxi.SecureChat;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.util.Base64;

public class AESDecryptActivity extends Activity{
	public class AES implements AESInterface {

		//TODO Add Buttons, EditText, etc
		//TODO Finish aesdecrypt.xml
		
		@Override
		public String AESEncrypt(String SecretKey, String PlainMsg)
				throws Exception {
			/**
			 * For Implementation,
			 * @see AESEncryptActivity
			 */
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String AESDecrypt(String SecretKey, String EncryptMsg)
				throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			//Get bytes from the secret user has entered
			byte[] key = SecretKey.getBytes("UTF-8");
			//Generate the AES key from user entered secret
			SecretKeySpec sKey = new SecretKeySpec(key, "AES");
			//Initial Cipher
			Cipher cipher = Cipher.getInstance("AES");
			//Launch Decrypt Procedure
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			byte [] plainText = Base64.decode(EncryptMsg.getBytes(), 0);
			cipher.doFinal(plainText);
			//Display the decrypted text
			return new String(plainText);
		}
		
	}

}
