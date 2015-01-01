package cz.dedalus.lockie;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;


public class ConfigurationSaver {
    MainActivity mActivity;

    public ConfigurationSaver(MainActivity activity) {
        mActivity = activity;
    }

    public void save() {
        String ssids = mActivity.ssidsEditText.getText().toString();
        Log.d("TAG", "ssids: " + ssids);
        HashSet<String> ssidsSet = new HashSet<String>();
        for (String s: ssids.split("\n")) {
            ssidsSet.add(s);
        }

        String pin = mActivity.pinEditText.getText().toString();
        SharedPreferences sharedPref = mActivity.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(MainActivity.SSIDS_PREFERENCES_KEY, ssidsSet);
        if (pin.length() > 0) {
            editor.putString(MainActivity.PIN_PREFERENCES_KEY, pin);
        } else {
            editor.remove(MainActivity.PIN_PREFERENCES_KEY);
        }
        editor.commit();

        Toast.makeText(mActivity, mActivity.getString(R.string.configuration_saved), Toast.LENGTH_SHORT).show();
    }

}
