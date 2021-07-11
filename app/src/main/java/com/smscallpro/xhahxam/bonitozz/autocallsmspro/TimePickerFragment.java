package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;




public class TimePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        int hours=  ProcessData.calendarMain.get(Calendar.HOUR_OF_DAY);
        int minutes=ProcessData.calendarMain.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(),hours,minutes, DateFormat.is24HourFormat(getActivity()));
    }

}
