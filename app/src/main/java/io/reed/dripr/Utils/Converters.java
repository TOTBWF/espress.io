package io.reed.dripr.Utils;

import android.widget.EditText;

/**
 * Created by reed on 12/5/15.
 */
public class Converters {
    public static double convertEditToDouble(EditText edit) {
        try {
            return Double.parseDouble(edit.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
