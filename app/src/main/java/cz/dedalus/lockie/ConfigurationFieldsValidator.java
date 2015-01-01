package cz.dedalus.lockie;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigurationFieldsValidator {
    MainActivity mActivity;

    public ConfigurationFieldsValidator(MainActivity activity) {
        mActivity = activity;
    }

    public boolean isValid() {
        String pin = mActivity.pinEditText.getText().toString();
        if (pin.isEmpty()) {
            showMessage(R.string.pin_cannot_be_empty);
            return false;
        }

        String preferencesPin = mActivity.getPinFromPreferences();
        if (pin.equals(preferencesPin)) {
            return true;
        }
        EditText confirmPinEditText = (EditText) mActivity.findViewById(R.id.confirmPinEditText);
        String pinConfirmation = confirmPinEditText.getText().toString();
        if (!pin.equals(pinConfirmation)) {
            showMessage(R.string.pin_does_not_match_confirmation);
            return false;
        }

        if (preferencesPin == null) {
            return true;
        }
        EditText oldPinEditText = (EditText) mActivity.findViewById(R.id.oldPinEditText);
        String oldPin = oldPinEditText.getText().toString();
        if (!preferencesPin.equals(oldPin)) {
            showMessage(R.string.old_pin_incorrect);
            return false;
        }
        return true;
    }

    private void showMessage(int stringId) {
        Toast.makeText(mActivity, mActivity.getString(stringId), Toast.LENGTH_SHORT).show();
    }
}
