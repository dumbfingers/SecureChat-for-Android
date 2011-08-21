package com.yeyaxi.SecureChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 
 * @author Yaxi Ye
 * @version 1
 * 
 *	
 */

public class SecureChatActivity extends Activity {
	public Button startButton;
	public EditText phoneNum;
    public EditText sharedPwd;
    public RadioGroup rGroup;
    public RadioButton rAlice;
    public RadioButton rBob;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		startButton = (Button)findViewById(R.id.button1);
        phoneNum = (EditText)findViewById(R.id.editText1);
        sharedPwd = (EditText)findViewById(R.id.editText2);
        rGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        rAlice = (RadioButton)findViewById(R.id.radio0);
        rBob = (RadioButton)findViewById(R.id.radio1);
        //startButton.setOnClickListener(buttonListener);
        rGroup.setOnCheckedChangeListener(chkListener);
	}
	
	OnClickListener buttonListenerA = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == startButton) {
				Intent intent_Jpake = new Intent(SecureChatActivity.this, JPakeActivity.class);
				intent_Jpake.putExtra("phoneNumber", phoneNum.getText().toString());
				intent_Jpake.putExtra("secret", sharedPwd.getText().toString());
				intent_Jpake.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SecureChatActivity.this.startActivity(intent_Jpake);
			}
		}
		
	};
	
	OnClickListener buttonListenerB = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == startButton) {
				Intent intent_Jpake = new Intent(SecureChatActivity.this, JPakeActivityB.class);
				intent_Jpake.putExtra("phoneNumber", phoneNum.getText().toString());
				intent_Jpake.putExtra("secret", sharedPwd.getText().toString());
				intent_Jpake.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SecureChatActivity.this.startActivity(intent_Jpake);
			}
		}
		
	};
	
	private OnCheckedChangeListener chkListener = new RadioGroup.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == rAlice.getId()) {
				startButton.setOnClickListener(buttonListenerA);
			}
			else {
				startButton.setOnClickListener(buttonListenerB);
			}
		}
	};

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