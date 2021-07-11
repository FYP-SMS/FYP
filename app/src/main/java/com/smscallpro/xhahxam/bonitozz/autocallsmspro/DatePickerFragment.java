package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;




public class DatePickerFragment extends DialogFragment {


    public DatePickerFragment(){


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        int day=ProcessData.calendarMain.get(Calendar.DAY_OF_MONTH);
        int month=ProcessData.calendarMain.get(Calendar.MONTH);
        int year=ProcessData.calendarMain.get(Calendar.YEAR);


        return new DatePickerDialog(Objects.requireNonNull(getActivity()),(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
    }
}
