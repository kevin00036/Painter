package com.step5.painter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.*;

public class AlphaDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View vw = inflater.inflate(R.layout.alpha_dialog, null);
        builder.setView(vw);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // FIRE ZE MISSILES!
                MainActivity act = (MainActivity)(getActivity());
                ColorPicker pk = (ColorPicker) vw.findViewById(R.id.picker);
                int color = pk.getColor();
                act.mv.setColor(color);
            }
        });
        builder.setNegativeButton("Cancel", null);
        // Create the AlertDialog object and return it
        Dialog alertDialog = builder.create();
        alertDialog.show();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        alertDialog.getWindow().setLayout((int)(size.x * 0.9), (int)(size.y * 0.9));

        ColorPicker picker = (ColorPicker) alertDialog.findViewById(R.id.picker);
        SVBar svBar = (SVBar) alertDialog.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) alertDialog.findViewById(R.id.opacitybar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        return alertDialog;
    }
}
