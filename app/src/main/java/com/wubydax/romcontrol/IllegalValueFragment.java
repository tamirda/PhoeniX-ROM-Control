package com.wubydax.romcontrol;

/**
 * Created by Tamir on 08/04/2016.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;



public class IllegalValueFragment extends DialogFragment {
    private String mIllegalValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mIllegalBuilder = new AlertDialog.Builder(getActivity());
        mIllegalBuilder.setTitle(R.string.illegal_value_title);
        mIllegalBuilder.setMessage(String.format(getString(R.string.text_illegal_value), mIllegalValue));
        mIllegalBuilder.setNegativeButton(R.string.exit,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
        mIllegalBuilder.setPositiveButton(R.string.back_button_custom, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go back to custom dpi dialog upon positive button click
                CustomDpiFragment mCustom = new CustomDpiFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(mCustom, "custom popup");
                ft.commitAllowingStateLoss();

            }
        });

        mIllegalBuilder.create();
        Dialog mIllegalDialog = mIllegalBuilder.create();
        mIllegalDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        return mIllegalDialog;
    }
    public void mUpdateText (String inputValue){
        //recieva value from the EditText on the custom dpi dialog. this value will be uised in string format in the message of the dialog as an illegal value that was entered
        mIllegalValue=inputValue;
    }
}
