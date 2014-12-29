package cz.dedalus.lockie;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;

public class WifiChangeReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context c, Intent intent) {
        context = c;
        boolean lockEnabled = false;
        boolean lockDisabled = false;
        boolean connecting = false;
        String ssid = "";
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssid = wifiInfo.getSSID();
            if (ssidConfiguredForNoPassword(ssid)) {
                if (disableLock()) {
                    lockDisabled = true;
                }
            } else {
                if (enableLock()) {
                    lockEnabled = true;
                }
            }
        } else if (info != null && info.isConnectedOrConnecting()) {
            connecting = true;
        }
        else {
            if (enableLock()) {
                lockEnabled = true;
            }
        }

        if (lockEnabled) {
            showLockEnabledMessage();
        } else if (lockDisabled) {
            showLockDisabledMessage(ssid);
        } else if (!connecting) {
            showErrorToast();
        }
    }

    private boolean ssidConfiguredForNoPassword(String ssid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
        HashSet<String> ssids = (HashSet<String>) sharedPreferences.getStringSet(MainActivity.SSIDS_PREFERENCES_KEY,
                new HashSet<String>());
        ssid = ssid.replace("\"", "");
        return ssids.contains(ssid);
    }

    private boolean disableLock() {
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
       return devicePolicyManager.resetPassword("", 0);
    }

    private boolean enableLock() {
        String pin = getPinFromPreferences();
        if (pin == null) {
            return false;
        } else {
            DevicePolicyManager devicePolicyManager =
                    (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            return devicePolicyManager.resetPassword(pin, 0);
        }
    }

    private String getPinFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(MainActivity.PIN_PREFERENCES_KEY, null);
    }

    private void showLockEnabledMessage() {
        showToast(context.getString(R.string.enabled_screen_lock));
    }

    private void showLockDisabledMessage(String ssid) {
        String message = context.getString(R.string.connected_to);
        message += " " + ssid + ", ";
        message += context.getString(R.string.disabled_screen_lock);
        showToast(message);
    }

    private void showErrorToast() {
        showToast(context.getString(R.string.could_not_change_lock));
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}