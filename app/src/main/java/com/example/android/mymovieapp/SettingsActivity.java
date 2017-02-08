package com.example.android.mymovieapp;

        import android.app.Activity;
        import android.app.Application;
        import android.content.Intent;
        import android.os.Bundle;
        import android.preference.ListPreference;
        import android.preference.Preference;
        import android.preference.PreferenceActivity;
        import android.preference.PreferenceFragment;
        import android.preference.PreferenceManager;
        import android.content.SharedPreferences;
        import android.util.Log;
        import android.view.KeyEvent;

/**
 * Created by gunnaringi on 2017-02-07.
 */

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setResult(Activity.RESULT_OK);
        finish();
    }
}