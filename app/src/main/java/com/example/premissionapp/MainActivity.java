package com.example.premissionapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {
    private SMSReader smsReader;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private boolean canLogin = false;

    // Global variable for returnValue
    private static boolean returnValue = false;

    // Buttons
    private MaterialButton loginButton;
    private MaterialButton checkPremissionButton;

    // Icons
    private static ShapeableImageView main_SIV_StatusIconSMSFromAfeka;
    private static ShapeableImageView main_SIV_StatusIconWIFI;
    private static ShapeableImageView main_SIV_StatusIconBrightness;
    private static ShapeableImageView main_SIV_StatusIconMinVolume;
    private static ShapeableImageView main_SIV_PhoneCharging;
    private static ShapeableImageView main_SIV_StatusIconBatteryOver40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted
                        Toast.makeText(MainActivity.this,
                                "SMS permission granted",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission denied
                        Toast.makeText(MainActivity.this,
                                "SMS permission is required",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        findViews();
        initViews();
        // Request SMS permission immediately when activity is created
        requestPermissionLauncher.launch(Manifest.permission.READ_SMS);
    }

    private void initViews() {
        loginButton.setOnClickListener(v -> moveToNextScreen());
        checkPremissionButton.setOnClickListener(v -> checkPermissions());
        smsReader = new SMSReader(this);
    }

    private void checkPermissions() {
        // First check if we have SMS permission
        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean wifi = isConnectedToWIFI(this);
        boolean brightness = isBrightnessMax(this);
        boolean batteryAbove40 = isBatteryAbove40(this);
        boolean volume = isVolumeMin(this);
        boolean charging = isCharging(this);
        boolean messageFromAfeka = isMessageFromAfeka();

        // If we have permission, proceed with all checks
        if(wifi && brightness && batteryAbove40 && volume && charging && messageFromAfeka){
            canLogin = true;
            loginButton.setBackgroundColor(ContextCompat.getColor(loginButton.getContext(), R.color.green));
        }
    }

    private boolean isMessageFromAfeka() {
        boolean hasSMS = smsReader.hasSMSFromSender("Afeka");
        updateIconStatus(hasSMS, main_SIV_StatusIconSMSFromAfeka);
        return hasSMS;
    }

    public static boolean isBatteryAbove40(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            returnValue = batteryLevel > 40; // Update global returnValue
        }
        updateIconStatus(returnValue, main_SIV_StatusIconBatteryOver40);
        return returnValue;
    }

    private boolean isVolumeMin(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            returnValue = (currentVolume == 0); // Update global returnValue
        }
        updateIconStatus(returnValue, main_SIV_StatusIconMinVolume);
        return returnValue;
    }

    private boolean isBrightnessMax(Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            returnValue = (brightness == 255); // Update global returnValue
        } catch (Settings.SettingNotFoundException e) {
            returnValue = false; // Explicitly set returnValue to false in case of exception
        }
        updateIconStatus(returnValue, main_SIV_StatusIconBrightness);
        return returnValue;
    }

    private static boolean isConnectedToWIFI(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities =
                        connectivityManager.getNetworkCapabilities(activeNetwork);
                if (networkCapabilities != null) {
                    returnValue = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI); // Update global returnValue
                }
            }
        }
        updateIconStatus(returnValue, main_SIV_StatusIconWIFI);
        return returnValue;
    }

    private boolean isCharging(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            int status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            returnValue = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL); // Update global returnValue
        }
        updateIconStatus(returnValue, main_SIV_PhoneCharging);
        return returnValue;
    }

    private void moveToNextScreen() {
        if(canLogin){
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Not All Permissions Are met ", Toast.LENGTH_SHORT).show();
        }
    }

    private static void updateIconStatus(boolean condition, ShapeableImageView imageView) {
        if (condition) {
            imageView.setImageResource(R.drawable.check_icon); // V icon
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // X icon
        }
    }

    private void findViews() {
        loginButton = findViewById(R.id.main_BTN_Login);
        checkPremissionButton = findViewById(R.id.main_BTN_checkPremissions);
        main_SIV_StatusIconSMSFromAfeka = findViewById(R.id.main_SIV_StatusIconSMSFromAfeka);
        main_SIV_StatusIconWIFI = findViewById(R.id.main_SIV_StatusIconWIFI);
        main_SIV_StatusIconBrightness = findViewById(R.id.main_SIV_StatusIconBrightness);
        main_SIV_StatusIconMinVolume = findViewById(R.id.main_SIV_StatusIconMinVolume);
        main_SIV_PhoneCharging = findViewById(R.id.main_SIV_PhoneCharging);
        main_SIV_StatusIconBatteryOver40 = findViewById(R.id.main_SIV_StatusIconBatteryOver40);
    }
}
