package com.wubydax.romcontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class CustomDpiFragment extends DialogFragment {

    public CustomDpiFragment() {

    }

    EditText mEdit;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialoginput, null);
        mEdit = (EditText) view.findViewById(R.id.custom_dpi);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer x = Integer.valueOf(String.valueOf(mEdit.getText()));
                try {
                    Log.v("EditText", mEdit.getText().toString());
                    //we check if the value is in range and create/override the previous out script
                    //if not it will close activity and delete previous out script if present

                    if ((x) >= 360 && (x) <= 480) {
                        try {
                            Command applyLive = new Command(0, "wm density " + mEdit.getText() );
                            RootTools.getShell(true).add(applyLive);
                            Command applyToBuild = new Command(0, "busybox mount -o remount,rw /system", "cd /system", "sed -i '/ro.sf.lcd_density/c\\ro.sf.lcd_density=" + mEdit.getText() + "' build.prop");
                            RootTools.getShell(true).add(applyToBuild);
                            Toast.makeText(getActivity().getApplicationContext(), "Please reboot your device now.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } catch (TimeoutException | RootDeniedException | IOException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getActivity().getApplicationContext(), mEdit.getText(), Toast.LENGTH_LONG).show();
                    } else {
                        IllegalValueFragment mIllegalFragment = new IllegalValueFragment();
                        //passing the entered value as set dpi value for the illegal dialog message string
                        mIllegalFragment.mUpdateText(mEdit.getText().toString());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(mIllegalFragment, "illegal value fragment");
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();

                    }
                } catch (Exception e) {
                    //Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        builder.setNeutralButton(R.string.back_to_preset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SetDpiFragment mSetDpiFragment = new SetDpiFragment();
                FragmentTransaction mBackToSetDpi = getFragmentManager().beginTransaction();
                mBackToSetDpi.add(mSetDpiFragment, "dpiset");
                mBackToSetDpi.commit();
            }
        });


        builder.create();
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.setCanceledOnTouchOutside(false);
        //showing keyboard upon creating dialog
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }


}