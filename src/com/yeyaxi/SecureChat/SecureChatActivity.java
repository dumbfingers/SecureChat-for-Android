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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Base64;
/**
 * 
 * @author Yaxi Ye
 * @version 1
 * @see AES Encryption
 *	
 */

public class SecureChatActivity extends Activity {
	private TextView PlainTextView;
	public EditText PlainMessage;
	private TextView SecretTextView;
	public EditText SecretText;
	public Button EncryptButton;
	private TextView EncryptTextView;
	public TextView EncryptedMessage;
	public Button SendButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PlainTextView = (TextView) findViewById(R.id.textView1);
        PlainMessage = (EditText) findViewById(R.id.PlainTxt);
        SecretTextView = (TextView) findViewById(R.id.textView2);
        SecretText = (EditText)	findViewById(R.id.SecretTxt);
        EncryptButton = (Button) findViewById(R.id.EncryptButton);
        EncryptTextView = (TextView) findViewById(R.id.textView3);
        EncryptedMessage = (TextView) findViewById(R.id.EncryptMsg);
        SendButton = (Button) findViewById(R.id.SendButton);
        //Set Encrypt Button's event
        EncryptButton.setOnClickListener(EncryptListener);
    }
    private OnClickListener EncryptListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AES aes = new AES();
			try {
				EncryptedMessage.setText(aes.AESEnrypt(SecretText.toString(), PlainMessage.toString()));
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
    	
    };
    
    public class AES implements AESInterface {
		@Override
		public String AESEnrypt(String SecretKey, String PlainMsg)
				throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			// TODO Auto-generated method stub
			//Try use some Android based alert dialog to catch this exception.
			if (SecretKey == null) {
				throw new IllegalArgumentException ("NULL Secret NOT ALLOWED!");
			}
			//Get bytes from the secret user has entered.
			byte[] key = SecretKey.getBytes();
			//Generate the AES key from user entered secret
			SecretKeySpec sKey = new SecretKeySpec(key, "AES"); 
			//Initial Cipher
			Cipher cipher = Cipher.getInstance("AES");
			//Launch Encrypt Procedure
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			byte[] cipherText = PlainMsg.getBytes();
			cipher.doFinal(cipherText);
			//Display Base64 Encoded CipherText
			String cipherTextBase64 = Base64.encodeToString(cipherText, 0);
			//EncryptedMessage.setText(cipherTextBase64);
			// TODO return Base64 encoded cipherText
			return cipherTextBase64;
		}

		@Override
		public String AESDecrypt(String SecretKey, String EncryptMsg)
				throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			// TODO Auto-generated method stub
			//Get bytes from the secret user has entered
			byte[] key = SecretKey.getBytes();
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