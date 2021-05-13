package com.gprs.srp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Locale;


public class Bottomsheetlanguagefragment extends BottomSheetDialogFragment {

    View view;
    ArrayList<String> lang, key;
    ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_language_layout, container, false);


        lang = new ArrayList<>();
        key = new ArrayList<>();
        listView = view.findViewById(R.id.list);

        lang.add("English");
        lang.add("हिन्दी");
        lang.add("தமிழ்");
        lang.add("తెలుగు");
        lang.add("ಕೆನಡಾ");
        lang.add("മലയാളം");
        lang.add("ਪੰਜਾਬੀ");
        lang.add("ગુજરાતી");
        lang.add("मराठी");
        lang.add("ଓଡିଆ");
        lang.add("বাংলা");

        key.add("en");
        key.add("hi");
        key.add("ta");
        key.add("te");
        key.add("kn");
        key.add("ml");
        key.add("pa");
        key.add("gu");
        key.add("mr");
        key.add("or");
        key.add("bn");


        languageadapter arrayAdapter = new languageadapter(lang, key, getActivity());
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref;
                SharedPreferences.Editor editor;
                pref = getActivity().getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode
                editor = pref.edit();
                setAppLocale(key.get(position));
                editor.putString("lang", key.get(position));
                editor.apply();
            }
        });

        return view;
    }

    private void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);

        dismiss();
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        getActivity().startActivity(getActivity().getIntent());
        getActivity().overridePendingTransition(0, 0);

    }

}