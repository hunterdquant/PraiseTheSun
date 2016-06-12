package com.hunterquant.praisethesun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WeatherDetailsActivity extends ActionBarActivity {

    public static final String DETAILS = "DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherDetailsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_map) {
            openPreferredLocationMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationMap() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String loc = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_default_location));

        Uri geoLoc = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", loc).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(geoLoc);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("TEST", "Couldn't get " + loc + ", no map ");
        }
    }

    public static class WeatherDetailsFragment extends Fragment {

        public final String HASHTAG = " #PraiseTheSun!";

        static String weatherDetails = null;

        public WeatherDetailsFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_weather_details, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(DETAILS)) {
                weatherDetails = intent.getStringExtra(DETAILS);
                ((TextView) rootView.findViewById(R.id.weatherDetails)).setText(weatherDetails);
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail_menu, menu);
            MenuItem item = menu.findItem(R.id.menu_item_share);
            ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareIntent());
            }
        }

        private Intent createShareIntent() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,  weatherDetails + HASHTAG);
            return intent;

        }
    }
}
