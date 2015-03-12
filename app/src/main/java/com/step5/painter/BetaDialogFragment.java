package com.step5.painter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class BetaDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View vw = inflater.inflate(R.layout.beta_dialog, null);
        builder.setView(vw);
        builder.setNegativeButton("Cancel", null);

        Dialog alertDialog = builder.create();
        alertDialog.show();

        /*
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        alertDialog.getWindow().setLayout((int)(size.x * 0.9), (int)(size.y * 0.9));
        */

        return alertDialog;
    }
}
