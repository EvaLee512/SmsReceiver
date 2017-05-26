package com.example.evalee.smsreceiver.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.evalee.smsreceiver.activitys.permission.PermissionActivity;


public class MainActivity extends PermissionActivity {
    SMSReceiver receiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        receiver = new SMSReceiver();
        registerReceiver(receiver,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "MainActivity_Eva";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"intent.getAction() = "+intent.getAction());
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            abortBroadcast();;
            StringBuilder str = new StringBuilder();
            Bundle bundle = intent.getExtras();

            if(bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i=0;i<pdus.length;i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for(SmsMessage message :messages){
                    str.append("短信来源:");
                    str.append(message.getDisplayOriginatingAddress());
                    str.append("\n*****************短信内容*********************");
                    str.append(message.getDisplayMessageBody());
                    Log.i(TAG,"str = "+str);
                }
            }
        }
    }
}
