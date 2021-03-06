package com.wubydax.romcontrol;

/*      Created by Roberto Mariani and Anna Berkovitch, 2015
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionMenu;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class MainViewActivity extends AppCompatActivity
        implements NavigationDrawerCallbacks, View.OnClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private FloatingActionMenu mFloatingActionMenu;
    private static final String PREFS_NAME = "WelcomeMSG";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*Calling theme selector class to set theme upon start activity*/
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        //Getting root privileges upon first boot or if was not yet given su
        CheckSu suPrompt = new CheckSu();
        suPrompt.execute();

        // populate the navigation drawer

        AlertDialog.Builder welmsg = new AlertDialog.Builder(MainViewActivity.this);
        LayoutInflater welmsgInflater = LayoutInflater.from(MainViewActivity.this);
        View welmsgLayout = welmsgInflater.inflate(R.layout.welcome_msg, null);
        final CheckBox dontShowAgain = (CheckBox) welmsgLayout.findViewById(R.id.skipwelmsg);
        welmsg.setView(welmsgLayout);
        welmsg.setTitle("Welcome to PhoeniX ROM Control!");
        welmsg.setMessage(Html.fromHtml("Thank you for installing PhoeniX ROM! <br />" +
                "  <br />" +
                "This application will help you customize PhoeniX ROM as you like. <br />" +
                "  <br />" +
                "Some options are available to Pro users only. <br />" +
                "  <br />" +
                "Please consider buying Pro version as a small donation to the developer. <br />" +
                "  <br />" +
                "Thank you for understanding and using PhoeniX ROM! <br />" +
                "  <br />" +
                "tamirda <br />"));
        welmsg.setPositiveButton("Upgrade to Pro", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                if (dontShowAgain.isChecked())  checkBoxResult = "checked";
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("skipMessage", checkBoxResult);
                // Commit the edits!
                editor.commit();
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.tdroms.phoenixromcontrolprokey")));
                return;
            } });

        welmsg.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                if (dontShowAgain.isChecked())  checkBoxResult = "checked";
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("skipMessage", checkBoxResult);
                // Commit the edits!
                editor.commit();
                return;
            } });
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");
        Dialog alertDialog = welmsg.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        if (!skipMessage.equalsIgnoreCase("checked") ) alertDialog.show();

    }



    //Creates a list of NavItem objects to retrieve elements for the Navigation Drawer list of choices
    public List<NavItem> getMenu() {
        List<com.wubydax.romcontrol.NavItem> items = new ArrayList<>();
        /*String array of item names is located in strings.xml under name nav_drawer_items
        * If you wish to add more items you need to:
        * 1. Add item to nav_drawer_items array
        * 2. Add a valid material design icon/image to dir drawable
        * 3. Add that image ID to the integer array below (int[] mIcons
        * 4. The POSITION of your new item in the string array MUST CORRESPOND to the position of your image in the integer array mIcons
        * 5. Create new PreferenceFragment or your own fragment or a method that you would like to invoke when a user clicks on your new item
        * 6. Continue down this file to a method onNavigationDrawerItemSelected(int position) - next method
        * 7. Add an action based on position. Remember that positions in array are beginning at 0. So if your item is number 6 in array, it will have a position of 5... etc
        * 8. You need to add same items to the int array in NavigationDrawerFragment, which has the same method*/
        String[] mTitles = getResources().getStringArray(R.array.nav_drawer_items);
        int[] mIcons = {R.drawable.ic_about,
                R.drawable.ic_status,
                R.drawable.ic_notification_panel,
                R.drawable.ic_sound,
                R.drawable.ic_general_framework,
                R.drawable.ic_buttons_display,
                R.drawable.ic_utillity,
                R.drawable.ic_ui_mods,
                R.drawable.ic_dpi,
                R.drawable.ic_play};
        for (int i = 0; i < mTitles.length && i < mIcons.length; i++) {
            com.wubydax.romcontrol.NavItem current = new com.wubydax.romcontrol.NavItem();
            current.setText(mTitles[i]);
            current.setDrawable(mIcons[i]);
            items.add(current);
        }

        return items;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        /* update the main content by replacing fragments
        * See more detailed instructions on the thread or in annotations to the previous method*/

        setTitle(getMenu().get(position).getText());
        switch (position) {
            case 0:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new PhoenixRomInfoFragment()).commitAllowingStateLoss();
                break;
            case 1:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new StatusBarFragment()).commitAllowingStateLoss();
                break;
            case 2:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new NotificationPanelFragment()).commitAllowingStateLoss();
                break;
            case 3:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new SoundNotificationsFragment()).commitAllowingStateLoss();
                break;
            case 4:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new GeneralModsFragment()).commitAllowingStateLoss();
                break;
            case 5:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new ButtonsDisplayFragment()).commitAllowingStateLoss();
                break;
            case 6:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new UtilityFragment()).commitAllowingStateLoss();
                break;
            case 7:
                showThemeChooserDialog();
                break;
            case 8:
                showDPIChooserDialog();
                break;
            case 9:
                DonateViaPlay();
                break;

        }

    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else {
            if (mFloatingActionMenu.isOpened()) {
                mFloatingActionMenu.close(true);
                } else

                    super.onBackPressed();
            }
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_view, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ic_reboot_menu) {
            showHideRebootMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*Handling onClick event for the Reboot Menu (round Action Buttons array)
    * For now we handle them under su, later on, since app is intended to be a system app,
    * we will add PowerManager for items: Reboot, Reboot recovery and Reboot Download*/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        final FloatingActionMenu menu1 = (FloatingActionMenu) findViewById(R.id.reboot_menu);
        switch (id) {
            /*Handles the onClick event for the semi transparent white overlay
            * Once clicked, we consider it a click outside the Reboot Menu and it invokes methos showHideRebootMenu()*/
            case R.id.reboot_menu:
                menu1.close(true);
                break;
            case R.id.action_reboot:
                getRebootAction("reboot");
                menu1.close(true);
                break;
            case R.id.action_reboot_hotboot:
                getRebootAction("busybox killall system_server");
                menu1.close(true);
                break;
            case R.id.action_reboot_recovery:
                getRebootAction("reboot recovery");
                menu1.close(true);
                break;
            case R.id.action_reboot_bl:
                getRebootAction("reboot download");
                menu1.close(true);
                break;
            case R.id.action_reboot_systemUI:
                getRebootAction("pkill com.android.systemui");
                menu1.close(true);
                break;
            case R.id.action_reboot_launcher:
                getRebootAction("pkill com.sec.android.app.launcher");
                menu1.close(true);
                break;
            case R.id.action_reboot_incallui:
                getRebootAction("pkill com.android.incallui");
                menu1.close(true);
                break;
        }


    }

    //Gets string for shell command to activate reboot menu items, using stericson RootTools lib
    private void getRebootAction(String command) {
        Command c = new Command(0, command);
        try {
            RootTools.getShell(true).add(c);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }

    //Initializes the reboot menu as arrray of views, finds by id and sets animations and onClickListener to each in a loop


    //Show/Hide reboot menu with animation depending on the view's visibility
    public void showHideRebootMenu() {
        if (mFloatingActionMenu.isOpened()) {
            mFloatingActionMenu.close(true);
        } else {
            mFloatingActionMenu.open(true);
        }

    }

    private void DonateViaPayPal() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/1EyAowV"));
        startActivity(browserIntent);
    }

    private void DonateViaPlay() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tdroms.phoenixromcontrolprokey"));
        startActivity(browserIntent);
    }



    //Activates a chosen theme based on single choice list dialog, which opens upon selecting item at position 4 in nav drawer list
    private void showThemeChooserDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        Adapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item_single_choice, getResources().getStringArray(R.array.theme_items));
        b.setTitle(getString(R.string.theme_chooser_dialog_title))
                .setSingleChoiceItems((ListAdapter) adapter, PreferenceManager.getDefaultSharedPreferences(this).getInt("theme_prefs", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Invokes method initTheme(int) - next method based on chosen theme
                        initTheme(which);
                    }
                })
        ;
        AlertDialog d = b.create();
        d.show();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);

        Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
        cancel.setTextColor(typedValue.data);
        Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setTextColor(typedValue.data);
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ListView lv = d.getListView();
        int paddingTop = Math.round(this.getResources().getDimension(R.dimen.dialog_listView_top_padding));
        int paddingBottom = Math.round(this.getResources().getDimension(R.dimen.dialog_listView_bottom_padding));
        lv.setPadding(0, paddingTop, 0, paddingBottom);
    }


    private void showDPIChooserDialog() {
        SetDpiFragment mPresetDPI = new SetDpiFragment();
        FragmentTransaction mPresetDialogTransaction = getFragmentManager().beginTransaction();
        mPresetDialogTransaction.add(mPresetDPI, "dpichange");
        mPresetDialogTransaction.commitAllowingStateLoss();
    }


    /*Writes the chosen position integer (in theme chooser dialog) into common shared preferences.
    * Based on that integer (currently 0 or 1), a helper class ThemeSelectorUtility (which is called at the very beginning of onCreate)
    * then reads that integer when it's instantiated and sets the theme for the activity.
    * The activity is them rebooted, overriding pending transitions, to make the theme switch seemless.*/
    private void initTheme(int i) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("theme_prefs", i).commit();
        finish();
        this.overridePendingTransition(0, R.animator.fadeout);
        startActivity(new Intent(this, MainViewActivity.class));
        this.overridePendingTransition(R.animator.fadein, 0);

    }

    //Asynchronous class to ask for su rights at the beginning of the activity. If the root rights have been denied or the device is not rooted, the app will not run.
    public class CheckSu extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog mProgressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainViewActivity.this);
            mProgressDialog.setMessage(getString(R.string.gaining_root));
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //Accessing the ability of the device to get root and the ability of app to achieve su privileges.
            if (RootTools.isAccessGiven()) {
                //Calling the helper class HandleScripts to copy scripts to the files folder and chmod 755.
                //Scripts can be then accessed and executed using script#scriptname key for PreferenceScreen in PreferenceFragments
                HandleScripts hs = new HandleScripts(MainViewActivity.this);
                hs.copyAssetFolder();
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            //If the device is not rooted or su has been denied the app will not run.
            //A dialog will be shown announcing that with a single button, upon clicking which the activity will finish.
            if (!result) {
                //If no su access detected, throw and alert dialog with single button that will finish the activity
                AlertDialog.Builder mNoSuBuilder = new AlertDialog.Builder(MainViewActivity.this);
                mNoSuBuilder.setTitle(R.string.missing_su_title);
                mNoSuBuilder.setMessage(R.string.missing_su);
                mNoSuBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                mNoSuBuilder.create();
                Dialog mNoSu = mNoSuBuilder.create();
                mNoSu.show();


            } else {
                //Provided the su privileges have been established, we run the activity as usual, beginning with setting content view
                setContentView(R.layout.activity_main_view);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(toolbar);

                mNavigationDrawerFragment = (NavigationDrawerFragment)
                        getFragmentManager().findFragmentById(R.id.fragment_drawer);

                // Set up the drawer. Look in NavigationDrawerFragment for more details
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar, MainViewActivity.this);
                mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.reboot_menu);

//                try {
//                    remountSystem("rw");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }


        }
    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        try {
//            remountSystem("rw");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            remountSystem("ro");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            remountSystem("ro");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //To use this method when the user interacts with the app, you need to remove the outcommenting from all the previous methods and a from the onPostExecute of async task CheckSu
    @SuppressWarnings("unused")
    private void remountSystem(String mount) throws Exception {
        String system = "/system";
        String mounted = RootTools.getMountedAs(system);
        boolean isMountedRW = mounted.equals("rw");
        if (isMountedRW && mount.equals("ro")) {
            RootTools.remount(system, "ro");
        } else if (!isMountedRW && mount.equals("rw")) {
            RootTools.remount(system, "rw");
        }
    }
}
