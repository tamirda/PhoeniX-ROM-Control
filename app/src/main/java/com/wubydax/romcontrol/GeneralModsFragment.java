package com.wubydax.romcontrol;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class GeneralModsFragment extends PreferenceFragment {
    HandlePreferenceFragments hpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hpf = new HandlePreferenceFragments(getActivity(), this, "general_mods_prefs");

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
        getPreferenceManager().findPreference("carrier_color").setEnabled(false);
        getPreferenceManager().findPreference("scaling_toggle").setEnabled(false);
        getPreferenceManager().findPreference("recents_blur_view").setEnabled(false);
        getPreferenceManager().findPreference("enable_icon_colors").setEnabled(false);
        getPreferenceManager().findPreference("settings_layout_num").setEnabled(false);
        getPreferenceManager().findPreference("settings_fav_columns").setEnabled(false);
        getPreferenceManager().findPreference("kg_date_color").setEnabled(false);

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
