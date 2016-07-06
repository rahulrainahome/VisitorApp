package visitor.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

import visitor.app.utils.Constants;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    Context ctxt;
    public SmsBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ctxt = context;
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get("pdus");
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();


                if (!TextUtils.isEmpty(smsBody) && !TextUtils.isEmpty(address)) {
                    if ((smsBody.contains(Constants.ACTIVATION_VALIDATION_MSG)) || (address.contains(Constants.ACTIVATION_VALICATION_NUMBER))) {

                        Toast.makeText(context, "VisitorApp Activated by SMS.", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor se = context.getSharedPreferences(Constants.SMS_ACTIVE, 0).edit();
                        se.putString("ACTIVE","YES");
                        se.commit();

                    }
                }
            }
        }
    }




}
