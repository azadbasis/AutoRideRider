package org.autoride.autoride.SMSAuth.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.autoride.autoride.utils.Operation;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        String myPhoneNumber = Operation.getSMS(context, "myPhoneNumber");
        String authorizeCode = "verification code " + Operation.getSMS(context, "authorizeCode");
        final Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        if (pdus != null) {
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody().toString();
                String currentsms = smsMessage.getMessageBody().split("")[0];
                Log.d("currentItems", currentsms);
                Intent in = new Intent("SmsMessage.intent.MAIN").putExtra("get_msg", body);
                context.sendBroadcast(in);
                //LocalBroadcastManager.getInstance(context).sendBroadcast(in);
            }
        }

      /*  Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody().toString();
            String currentsms = smsMessage.getDisplayMessageBody().split("")[1];
            Log.d("currentItems", currentsms);
          *//*  String verifiedPhoneNumber = PhoneNumberUtils.compare(sender, myPhoneNumber) ? myPhoneNumber : "";

            if (verifiedPhoneNumber.length() > 0&&authorizeCode.length() > 0 && String.valueOf(authorizeCode).equals(body)) {

              //  Operation.saveSMS(context, "verifiedPhoneNumber", myPhoneNumber);
                Operation.saveSMS(context, "message", body);

                Intent intentone = new Intent(context.getApplicationContext(), UserActivity.class);
                intentone.putExtra("RIDER-PHONE-NUMBER", myPhoneNumber);
                intentone.putExtra("message", body);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          //      context.startActivity(intentone);
                Toast.makeText(context, "Your number verified", Toast.LENGTH_SHORT).show();
            }*//*

            Intent in = new Intent("SmsMessage.intent.MAIN").putExtra("get_msg", sender + ":" + body);
            context.sendBroadcast(in);
          *//*  Intent myIntent = new Intent("otp");
            myIntent.putExtra("message",body);
            LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);*//*

        }*/
    }
}