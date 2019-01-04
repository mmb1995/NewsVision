package com.example.android.newsvision.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.newsvision.R;

import java.util.Calendar;

public class SearchFragment extends Fragment
                implements DatePickerDialog.OnDateSetListener{
    private EditText mSearchBar;
    private Button mSearchButton;

    // Views related to the starting date selected
    private Button mStartDateButton;
    private TextView mStartDateTextView;

    // Views related to the ending date selected
    private Button mEndDateButton;
    private TextView mEndDateTextView;

    private OnSearchListener mCallback;

    private String mBeginDate;
    private String mEndDate;

    private static final String BEGIN_DATE_PICKER = "begin date";
    private static final String END_DATE_PICKER = "end date";

    private String mType;

    /**
     * Creates a new instance of the SearchFragment
     */
    public SearchFragment() {}

    /**
     * This interface must be implemented by the fragments parent class to handle search events
     */
    public interface OnSearchListener {
        public void onSearch(String searchTerm, String beginDate, String endDate);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Gets reference to Search button
        mSearchButton = (Button) rootView.findViewById(R.id.search_button);

        // Gets Reference to search bar
        mSearchBar = (EditText) rootView.findViewById(R.id.search_bar);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchButtonClicked();
            }
        });

        // Get reference to start date picker button
        mStartDateButton = (Button) rootView.findViewById(R.id.start_date_button);

        // Get reference to date display text view
        mStartDateTextView= (TextView) rootView.findViewById(R.id.starting_date_tv);

        // Sets onClickListener for the Date picker Button
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = BEGIN_DATE_PICKER;
                setUpDatePickerFragment(view, BEGIN_DATE_PICKER);

            }
        });

        // Get reference to end date button
        mEndDateButton = (Button) rootView.findViewById(R.id.end_date_button);

        // Get reference to end date display TextView
        mEndDateTextView = (TextView) rootView.findViewById(R.id.end_date_tv);

        // Sets onClickListener for end date button
        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = END_DATE_PICKER;
                setUpDatePickerFragment(view, END_DATE_PICKER);
            }
        });

        displayDefaultDates();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnSearchListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSearchListener");
        }
    }

    private void displayDefaultDates() {
        Calendar calendar = Calendar.getInstance();

        // Sets up current date
        mEndDate = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) +
                "-" + calendar.get(Calendar.DAY_OF_MONTH);

        // Displays default ending date
        mEndDateTextView.setText(mEndDate);

        // Sets up default beginning date
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        mBeginDate = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) +
                "-" + calendar.get(Calendar.DAY_OF_MONTH);

        // Displays default beginning date
        mStartDateTextView.setText(mBeginDate);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String displayDate = "" + year + "-" + (month + 1) + "-" + day;
        if (mType.equals(BEGIN_DATE_PICKER)) {
            mStartDateTextView.setText(displayDate);
            mBeginDate = displayDate;
        } else {
            mEndDateTextView.setText(displayDate);
            mEndDate = displayDate;
        }
    }

    private void setUpDatePickerFragment(View v, String type) {
        DatePickerFragment dateFrag = new DatePickerFragment();
        dateFrag.setCallback(this);
        dateFrag.show(getFragmentManager(), type);
    }

    /**
     * Formats the given String to be consistent with the New York times Api
     * @param date the date to be formated
     * @return a String representing a date consistent with the New York Times Api requirements
     */
    private String formatDateForNewYorkTimes(String date) {
        String formattedDate = "";
        String[] dateArray = date.split("-");
        for (int i = 0; i < dateArray.length; i ++) {
            String datePart = dateArray[i];
            if (datePart.length() < 2) {
                datePart = "0" + datePart;
            }
            formattedDate += datePart;
        }
        return formattedDate;
    }


    /**
     * Gets the users query and passes it back to the main activity
     */
    private void onSearchButtonClicked() {
        if (mCallback != null && mSearchBar.getText() != null) {

            // Gets the Users search query
            final String searchTerm = mSearchBar.getText().toString();

            // Gets Formatted dates
            String startDate = formatDateForNewYorkTimes(mBeginDate);
            String endDate = formatDateForNewYorkTimes(mEndDate);

            // Gets formatted dates or uses default values
            mCallback.onSearch(searchTerm, startDate, endDate);
        }
    }

}
