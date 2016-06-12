package com.wubydax.romcontrol;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


public class StatusBarFragment extends PreferenceFragment {
    HandlePreferenceFragments hpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hpf = new HandlePreferenceFragments(getActivity(), this, "status_bar_prefs");

        if(!isProKeyInstalled(getActivity().getApplicationContext())) disablePreferences();

    }

    public boolean isProKeyInstalled(Context context){
        boolean isInstalled;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pInfo1 = packageManager.getPackageInfo("", PackageManager.GET_SIGNATURES);
            PackageInfo pInfo2 = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            isInstalled = pInfo1.signatures[0].toCharsString().equals(pInfo2.signatures[0].toCharsString());
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
            e.printStackTrace();
        }
        return isInstalled;
    }

    public void disablePreferences(){
        getPreferenceManager().findPreference("battery_text_color").setEnabled(false);
        getPreferenceManager().findPreference("enable_battery_text_color").setEnabled(false);
        getPreferenceManager().findPreference("statusbar_clock_date_position").setEnabled(false);
        getPreferenceManager().findPreference("clock_use_second").setEnabled(false);
        getPreferenceManager().findPreference("sysbar_lock_button_color").setEnabled(false);

    }


    @Override
    public void onResume() {
        super.onResume();
        hpf.onResumeFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        hpf.onPauseFragment();
    }


}
