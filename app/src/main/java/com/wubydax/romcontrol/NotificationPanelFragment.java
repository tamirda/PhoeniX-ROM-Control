package com.wubydax.romcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class NotificationPanelFragment extends PreferenceFragment {
    HandlePreferenceFragments hpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hpf = new HandlePreferenceFragments(getActivity(), this, "notification_panel_prefs");

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
        getPreferenceManager().findPreference("enable_data_view").setEnabled(false);
        getPreferenceManager().findPreference("hide_header_mod_clock_second").setEnabled(false);
        getPreferenceManager().findPreference("carrier_btn_color").setEnabled(false);
        getPreferenceManager().findPreference("toggle_sec_text_color").setEnabled(false);
        getPreferenceManager().findPreference("toggle_buttons_background_color").setEnabled(false);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 46:
                String key = "notification_panel_bg";
                Uri uri = data.getData();
                Settings.System.putString(getActivity().getContentResolver(), key, uri.toString());
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Drawable icon = new BitmapDrawable(getActivity().getResources(), bitmap);
                    findPreference(key).setIcon(icon);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
