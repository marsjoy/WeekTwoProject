package com.marswilliams.com.fakenews.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.marswilliams.com.fakenews.R;
import com.marswilliams.com.fakenews.databinding.FragmentSettingsBinding;
import com.marswilliams.com.fakenews.models.Settings;
import com.marswilliams.com.fakenews.utils.SharedPreferenceUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mars_williams on 9/22/17.
 */

public class SettingsDialogFragment extends DialogFragment implements View.OnClickListener{

    public final static String TAG = "SettingsDialogFragment";
    public final static int DATE_REQUEST = 0;
    private FragmentSettingsBinding mFragmentSettingsBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings,container,false);
        return mFragmentSettingsBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentSettingsBinding.etBeginDate.setOnClickListener(this);
        mFragmentSettingsBinding.etSortOrder.setOnClickListener(this);
        mFragmentSettingsBinding.btnSave.setOnClickListener(this);
        mFragmentSettingsBinding.btnCancel.setOnClickListener(this);
        setDataInFragment();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setDataInFragment() {
        Settings settings = SharedPreferenceUtils.getAllSettings(getActivity());
        mFragmentSettingsBinding.etBeginDate.setText(settings.getBeginDate());
        mFragmentSettingsBinding.etSortOrder.setText(settings.getSortOrder());
        mFragmentSettingsBinding.cbArts.setChecked(settings.isArtsChecked());
        mFragmentSettingsBinding.cbFashionStyle.setChecked(settings.isFashionChecked());
        mFragmentSettingsBinding.cbSports.setChecked(settings.isSportsChecked());
        mFragmentSettingsBinding.switchChromeTab.setChecked(settings.isChromeTab());
    }

    private void openPopUpMenu(final EditText view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_item_newest:
                        view.setText(R.string.label_newest);
                        break;
                    case R.id.menu_item_oldest:
                        view.setText(R.string.label_oldest);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    private void saveDataToPreferences() {
        SharedPreferences sharedPref = SharedPreferenceUtils.getSharePreferences(getActivity());
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString(getString(R.string.key_begin_date),
                mFragmentSettingsBinding.etBeginDate.getText().toString());
        editor.putString(getString(R.string.key_sort_order),
                mFragmentSettingsBinding.etSortOrder.getText().toString());
        editor.putBoolean(getString(R.string.key_checkbox_arts),
                mFragmentSettingsBinding.cbArts.isChecked());
        editor.putBoolean(getString(R.string.key_checkbox_fashion),
                mFragmentSettingsBinding.cbFashionStyle.isChecked());
        editor.putBoolean(getString(R.string.key_checkbox_sports),
                mFragmentSettingsBinding.cbSports.isChecked());
        editor.putBoolean(getString(R.string.key_chrome_tab),mFragmentSettingsBinding.switchChromeTab.isChecked());
        editor.apply();

    }

    public void onResume() {
        super.onResume();
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getDialog().getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == DATE_REQUEST) {
            int day = data.getExtras().getInt("day");
            int month = data.getExtras().getInt("month");
            int year = data.getExtras().getInt("year");
            Log.d(TAG,"Day selected is--> "+day);
            Log.d(TAG,"Month selected is--> "+month);
            Log.d(TAG,"Year selected is--> "+year);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);
            DateFormat dateFormat = SimpleDateFormat.getDateInstance();
            String date = dateFormat.format(calendar.getTime());
            Log.d(TAG,"Date is--> "+date);
            mFragmentSettingsBinding.etBeginDate.setText(date);

        }
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.et_begin_date:
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTargetFragment(SettingsDialogFragment.this,DATE_REQUEST);
                datePickerFragment.show(getActivity().getSupportFragmentManager(),DatePickerFragment.TAG);
                break;
            case R.id.et_sort_order:
                openPopUpMenu(mFragmentSettingsBinding.etSortOrder);
                break;
            case R.id.btn_save:
                saveDataToPreferences();
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;

        }
    }
}
