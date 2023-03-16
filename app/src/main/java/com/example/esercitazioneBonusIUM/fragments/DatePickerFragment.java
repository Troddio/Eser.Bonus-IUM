package com.example.esercitazioneBonusIUM.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.esercitazioneBonusIUM.RegisterActivity;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private Calendar date;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatePicker datePicker = new DatePicker(getActivity());
        date = Calendar.getInstance();

        return new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                date.set(Calendar.YEAR, datePicker.getYear());
                                date.set(Calendar.MONTH, datePicker.getMonth());
                                date.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

                                ((RegisterActivity)getActivity()).doPositiveClick(date);
                            }
                        }
                )
                .setView(datePicker)
                .create();
    }
}
