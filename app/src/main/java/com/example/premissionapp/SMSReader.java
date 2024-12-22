package com.example.premissionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.Telephony;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.premissionapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SMSReader {
    private static final int READ_SMS_PERMISSION_CODE = 123;
    private final MainActivity activity;

    public SMSReader(MainActivity activity) {
        this.activity = activity;
    }

    // Check if we have SMS permission
    public boolean checkSMSPermission() {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permission
    public void requestSMSPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_SMS},
                READ_SMS_PERMISSION_CODE
        );
    }

    // Read all SMS messages
    public List<SMSMessage> readAllSMS() {
        List<SMSMessage> smsList = new ArrayList<>();

        if (!checkSMSPermission()) {
            requestSMSPermission();
            return smsList;
        }

        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    new String[]{
                            Telephony.Sms.ADDRESS,
                            Telephony.Sms.BODY,
                            Telephony.Sms.DATE
                    },
                    null,
                    null,
                    Telephony.Sms.DEFAULT_SORT_ORDER
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));

                    smsList.add(new SMSMessage(address, body, date));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return smsList;
    }

    // Check if any SMS exists from specific sender (returns boolean)
    public boolean hasSMSFromSender(String senderNumber) {
        if (!checkSMSPermission()) {
            requestSMSPermission();
            return false;
        }

        Cursor cursor = null;
        try {
            String selection = Telephony.Sms.ADDRESS + " = ?";
            String[] selectionArgs = {senderNumber};

            cursor = activity.getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    new String[]{Telephony.Sms.ADDRESS},
                    selection,
                    selectionArgs,
                    null
            );

            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Get all SMS messages from specific sender
    public List<SMSMessage> getSMSFromSender(String senderNumber) {
        List<SMSMessage> smsList = new ArrayList<>();

        if (!checkSMSPermission()) {
            requestSMSPermission();
            return smsList;
        }

        Cursor cursor = null;
        try {
            String selection = Telephony.Sms.ADDRESS + " = ?";
            String[] selectionArgs = {senderNumber};

            cursor = activity.getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    new String[]{
                            Telephony.Sms.ADDRESS,
                            Telephony.Sms.BODY,
                            Telephony.Sms.DATE
                    },
                    selection,
                    selectionArgs,
                    Telephony.Sms.DEFAULT_SORT_ORDER
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));

                    smsList.add(new SMSMessage(address, body, date));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return smsList;
    }
}

// SMS Message class
class SMSMessage {
    private final String address;
    private final String body;
    private final long date;

    public SMSMessage(String address, String body, long date) {
        this.address = address;
        this.body = body;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }
}