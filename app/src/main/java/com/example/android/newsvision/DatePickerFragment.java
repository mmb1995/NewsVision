package com.example.android.newsvision;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_TYPE = "type";

    private static final String BEGIN_DATE_PICKER = "begin date";
    private static final String END_DATE_PICKER = "end date";

    private String mType;

    private DatePickerDialog.OnDateSetListener mCallback;

    /**
     * Constructor for empty DatePickerFragment
     */
    public DatePickerFragment() {}

   public static DatePickerFragment newInstance(String type) {
        DatePickerFragment dateFrag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        return dateFrag;
   }



    /**
     * Sets the OnDateSetListener callback for the DatePickerDialog
     * @param callback
     */
    public void setCallback(DatePickerDialog.OnDateSetListener callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Sets the type of DatePickerFragment
        //mType = getArguments().getString(ARG_TYPE);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), mCallback,
                year, month, day);
    }

    public String getType() {
        return mType;
    }

}
