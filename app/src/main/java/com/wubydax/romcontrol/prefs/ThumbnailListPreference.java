package com.wubydax.romcontrol.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wubydax.romcontrol.R;

import java.util.Arrays;

/**
 * Created by Anna Berkovitch on 28/10/2015.
 */
public class ThumbnailListPreference extends DialogPreference implements AdapterView.OnItemClickListener {
    private Context c;
    private Drawable[] thumbnailsArray;
    private Drawable thumbnailIcon;
    private CharSequence[] entriesList, entryValuesList;
    private String LOG_TAG = "ThumbnailPreference", entryDefault;
    private ImageView iconView;
    private int selectedPosition;


    public ThumbnailListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        TypedArray taStyled = c.obtainStyledAttributes(attrs, R.styleable.ThumbnailListPreference, 0, 0);
        entriesList = taStyled.getTextArray(R.styleable.ThumbnailListPreference_entryList);
        entryValuesList = taStyled.getTextArray(R.styleable.ThumbnailListPreference_entryValuesList);
        entryDefault = taStyled.getString(R.styleable.ThumbnailListPreference_entryDefault);
        int resId = taStyled.getResourceId(R.styleable.ThumbnailListPreference_drawableArray, 0);
        if (resId != 0) {
            TypedArray taRes = c.getResources().obtainTypedArray(resId);
            thumbnailsArray = new Drawable[taRes.length()];
            for (int i = 0; i < taRes.length(); i++) {
                thumbnailsArray[i] = taRes.getDrawable(i);
            }
        }
        taStyled.recycle();

        setDialogLayoutResource(R.layout.thumbnail_preference_dialog_view);
        setWidgetLayoutResource(R.layout.thumbnail_preference_icon);

    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if(entryDefault!=null && getPersistedString(null) ==null){
            persistString(entryDefault);
        }
        getThumbnailIcon();
        iconView = (ImageView) view.findViewById(R.id.thumbnailIcon);
        iconView.setImageDrawable(thumbnailIcon);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        selectedPosition = Arrays.asList(entryValuesList).indexOf(getPersistedString("0"));
        ListView lv = (ListView) view.findViewById(R.id.thumbnailListView);
        lv.setOnItemClickListener(this);
        lv.setFastScrollEnabled(true);
        lv.setFadingEdgeLength(1);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setScrollingCacheEnabled(false);
        ListAdapter adapter = new ListAdapter(c, entriesList, entryValuesList, thumbnailsArray, selectedPosition);
        lv.setAdapter(adapter);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.show();
        Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setVisibility(View.GONE);
        cancel.setTextColor(typedValue.data);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        if(entryDefault!=null) {
            return entryDefault;
        }else{
            return null;
        }
    }

    public CharSequence[] getEntries(){
        return entriesList;
    }

    public CharSequence[] getEntryValues(){
        return entryValuesList;
    }

    public String getDefaultEntry(){
        return entryDefault;
    }

    public Drawable[] getDrawableArray(){
        return thumbnailsArray;
    }

    public void setEntries(CharSequence[] entries){
        entriesList = entries;
    }

    public void setEntryValues(CharSequence[] entryValues){
        entryValuesList = entryValues;
    }

    public void setDrawableArray(Drawable[] drawableArray){
        thumbnailsArray = drawableArray;
    }

    public void setEntryDefault(String defaultEntry){
        entryDefault = defaultEntry;
    }

    public Drawable getThumbnailIcon() {
        String value = getPersistedString(null);
        int index = Arrays.asList(entryValuesList).indexOf(value);
        if (value != null) {
            thumbnailIcon = thumbnailsArray[index];

        } else {
            thumbnailIcon = null;
        }
        return thumbnailIcon;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        persistString(entryValuesList[i].toString());
        thumbnailIcon = thumbnailsArray[i];
        selectedPosition = i;
        getDialog().dismiss();
        iconView.setImageDrawable(thumbnailIcon);

    }

    private static class ListAdapter extends BaseAdapter {
        Context c;
        CharSequence[] entries, values;
        Drawable[] thumbnails;
        int selectedPosition;

        public ListAdapter(Context context, CharSequence[] entries, CharSequence[] values, Drawable[] thumbnails, int selectedPosition) {
            c = context;
            this.entries = entries;
            this.values = values;
            this.thumbnails = thumbnails;
            this.selectedPosition = selectedPosition;

        }

        @Override
        public int getCount() {
            if (entries != null) {
                return entries.length;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private class ViewHolder {
            RadioButton rb;
            TextView tv;
            ImageView iv;

            public ViewHolder(View v) {
                rb = (RadioButton) v.findViewById(R.id.thumbnailRadioButton);
                tv = (TextView) v.findViewById(R.id.thumbnailText);
                iv = (ImageView) v.findViewById(R.id.thumbnailImage);
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;

            if (view == null) {
                LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.thumbnail_item_view, viewGroup, false);
                vh = new ViewHolder(view);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();

            }
            vh.tv.setText(entries[i]);
            vh.iv.setImageDrawable(thumbnails[i]);
            vh.rb.setChecked(i==selectedPosition);
            return view;
        }
    }
}
