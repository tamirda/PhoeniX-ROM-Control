package com.wubydax.romcontrol;

/**
 * Created by Tamir on 08/04/2016.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class SetDpiFragment extends DialogFragment {

    public SetDpiFragment() {

    }

    String dpi = "";
    List<String> values;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        values = Arrays.asList(getActivity().getResources().getStringArray(R.array.dpi_dialog_values));

        // retrieve lcd density from get.prop - it will update displayed after reboot.. is not reading from build.prop

        File file = new File("/system/build.prop");
        if (file.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (line.contains("ro.sf.lcd_density")) {
                        dpi = line.substring(line.indexOf("=") + 1, line.length());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        // we build title pushing inside value from buffer reader (dpi string)


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        View selectDPIActivityView = LayoutInflater.from(getActivity()).inflate(R.layout.dpilist, null);
        final ListView listView = (ListView) selectDPIActivityView.findViewById(R.id.DPIlistView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_single_choice, getResources().getStringArray(R.array.dpi_dialog_items));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setItemChecked(getIndex(dpi), true);

        builder.setTitle(String.format(res.getString(R.string.show_current_dpi), dpi))
                .setView(selectDPIActivityView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = listView.getCheckedItemPosition();
                        String value = getValue(selectedPosition);
                        if (selectedPosition != 5) {
                            try {
                                Command applyLive = new Command(0, "wm density " + value);
                                RootTools.getShell(true).add(applyLive);
                                Command applyToBuild = new Command(0, "busybox mount -o remount,rw /system", "cd /system", "sed -i '/ro.sf.lcd_density/c\\ ro.sf.lcd_density=" + value + "' build.prop");
                                RootTools.getShell(true).add(applyToBuild);
                                Toast.makeText(getActivity().getApplicationContext(), "Please reboot your device now.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (TimeoutException | RootDeniedException | IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                            CustomDpiFragment mCustomDpiFragment = new CustomDpiFragment();
                            FragmentTransaction mCustomTransaction = getFragmentManager().beginTransaction();
                            mCustomTransaction.add(mCustomDpiFragment, "custom dpi");
                            mCustomTransaction.commitAllowingStateLoss();

                        }
                    }

                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })

                .setNeutralButton(R.string.reboot_device, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getRebootAction("reboot");
                        dialog.dismiss();

                    }
                })

                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            //upon back pressed activity finishes. this is the first dialog. we cannot use ondetach
                            dialog.dismiss();
                        }
                        return false;

                    }
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.setCanceledOnTouchOutside(false); //has to be false or else will hit invisible activity blocking ui. cannot use ondetach
        return dialog;
    }


    private int getIndex(String dpi) {
        return values.indexOf(dpi) != -1 ? values.indexOf(dpi) : 5;
    }

    private String getValue(int position) {
        return position != 5 ? values.get(position) : null;
    }


    private void getRebootAction(String command) {
        Command c = new Command(0, command);
        try {
            RootTools.getShell(true).add(c);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }

}