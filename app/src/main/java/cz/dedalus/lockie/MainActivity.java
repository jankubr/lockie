package cz.dedalus.lockie;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;


public class MainActivity extends ActionBarActivity {
    final int REQUEST_CODE_ENABLE_DEVICE_ADMIN = 1;
    final static String PREFERENCES_FILE_NAME = "preferences";
    final static String SSIDS_PREFERENCES_KEY = "SSIDS";
    final static String PIN_PREFERENCES_KEY = "PIN";
    EditText pinEditText;
    EditText ssidsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillPin();
        fillSsids();

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName componentName = new ComponentName(this, LockieDeviceAdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_DEVICE_ADMIN);
    }

    public void saveConfiguration(View v) {
        String ssids = ssidsEditText.getText().toString();
        Log.d("TAG", "ssids: " + ssids);
        HashSet<String> ssidsSet = new HashSet<String>();
        for (String s: ssids.split("\n")) {
            ssidsSet.add(s);
        }

        String pin = pinEditText.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(SSIDS_PREFERENCES_KEY, ssidsSet);
        if (pin.length() > 0) {
            editor.putString(PIN_PREFERENCES_KEY, pin);
        } else {
            editor.remove(PIN_PREFERENCES_KEY);
        }
        editor.commit();

        Toast.makeText(this, getString(R.string.configuration_saved), Toast.LENGTH_SHORT).show();
    }

    private void fillPin() {
        pinEditText = (EditText) findViewById(R.id.pinEditText);
        pinEditText.setText(getPreferences().getString(PIN_PREFERENCES_KEY, null));
    }

    private void fillSsids() {

        HashSet<String> ssidsSet = (HashSet<String>) getPreferences().getStringSet(SSIDS_PREFERENCES_KEY, new HashSet<String>());
        String ssidsString = "";
        for (String s: ssidsSet) {
            ssidsString += s + "\n";
        }
        ssidsEditText = (EditText) findViewById(R.id.ssids);
        ssidsEditText.setText(ssidsString);
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
