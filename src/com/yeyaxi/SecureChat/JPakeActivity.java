/**
 * 
 */
package com.yeyaxi.SecureChat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.EditText;


/**
 * @author Yaxi Ye
 *
 */
public class JPakeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jpake);
        //Context context = new Context();
        //tManager.getDeviceId();
        EditText id = (EditText)findViewById(R.id.editText1);
        EditText sharedPwd = (EditText)findViewById(R.id.editText2);
    }
    public String getUID() {
        TelephonyManager tManager = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }
    
    public void onStart(){
    	super.onStart();
    	JPake jpake = new JPake();

		//Get phone's IMEI
    	String signerId = jpake.GetSignerId();
		//JPake step1
		try {
			jpake.step1(signerId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	String GX1 = jpake.step1Result.get(0).toString();
    	
    	
    }
}

