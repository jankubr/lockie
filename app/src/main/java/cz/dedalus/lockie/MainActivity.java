package cz.dedalus.lockie;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashSet;


public class MainActivity extends ActionBarActivity {
    final int REQUEST_CODE_ENABLE_DEVICE_ADMIN = 1;
    final static String PREFERENCES_FILE_NAME = "preferences";
    final static String SSIDS_PREFERENCES_KEY = "SSIDS";
    final static String PIN_PREFERENCES_KEY = "PIN";
    EditText pinEditText;
    EditText ssidsEditText;
    RelativeLayout confirmPinFieldsLayout;
    RelativeLayout oldPinFieldsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillPin();
        fillSsids();
        setUpExtraPinFieldsToggler();

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName componentName = new ComponentName(this, LockieDeviceAdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_DEVICE_ADMIN);
    }

    public void saveConfiguration(View v) {
        ConfigurationFieldsValidator validator = new ConfigurationFieldsValidator(this);
        if (!validator.isValid()) {
            return;
        }

        ConfigurationSaver saver = new ConfigurationSaver(this);
        saver.save();

        hideExtraPinFields();
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

    private void setUpExtraPinFieldsToggler() {
        confirmPinFieldsLayout = (RelativeLayout) findViewById(R.id.confirmPinFields);
        oldPinFieldsLayout = (RelativeLayout) findViewById(R.id.oldPinFields);
        pinEditText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String currentPin = getPinFromPreferences();
                if (pinEditText.getText().toString().equals(currentPin)) {
                    hideExtraPinFields();
                } else {
                    confirmPinFieldsLayout.setVisibility(View.VISIBLE);
                    if (currentPin != null && !currentPin.isEmpty()) {
                        oldPinFieldsLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    private void hideExtraPinFields() {
        confirmPinFieldsLayout.setVisibility(View.GONE);
        oldPinFieldsLayout.setVisibility(View.GONE);
        ((EditText) findViewById(R.id.confirmPinEditText)).setText("");
        ((EditText) findViewById(R.id.oldPinEditText)).setText("");
    }

    public String getPinFromPreferences() {
        return getPreferences().getString(MainActivity.PIN_PREFERENCES_KEY, null);
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
