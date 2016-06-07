package com.hunterquant.praisethesun;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hunter on 6/3/16.
 */
public class ForecastFragment extends Fragment {

    public static final String DETAILS = "DETAILS";

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String API_PARAM = "APPID";
    private static final String NUM_DAYS_PARAM = "cnt";
    private static final String UNIT_PARAM = "units";
    private static final String FORMAT_PARAM = "mode";
    private static final String QUERY_PARAM = "q";

    private static final String API_ID = "8503bd5b939295e4c4464498068e0c6b";
    private static final String FORMAT_TYPE = "json";
    private static final String NUM_DAYS = "7";
    private static final String UNIT_TYPE = "metric";

    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_forecast, container, false);

        forecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                                                R.id.list_item_forecast_textview);

        ListView listView = (ListView) root.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(DETAILS, forecastAdapter.getItem(position).toString());

                Intent intent = new Intent();
                intent.setClass(getActivity(), WeatherDetailsActivity.class);
                intent.putExtra(DETAILS, forecastAdapter.getItem(position).toString());
                startActivity(intent, bundle);
            }
        });
        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            RetrieveWeatherTask task = new RetrieveWeatherTask();
            task.execute("14546");
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RetrieveWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String CLASS_NAME = RetrieveWeatherTask.class.getName();


        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String weatherDataJson = retrieveWeatherData(params[0]);
            try {
                return getWeatherDataFromJson(weatherDataJson, 7);
            } catch (JSONException je) {
                Log.e(CLASS_NAME, "Error",  je);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                forecastAdapter.clear();
                List<String> forecasts = new ArrayList<String>(Arrays.asList(strings));
                forecastAdapter.addAll(forecasts);
            }
        }

        private String retrieveWeatherData(String postalCode) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            Uri fullUrl = getUrl(postalCode);
            String json = "";
            try {
                URL url = new URL(fullUrl.toString());

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return "";
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String curLine;
                while ((curLine = reader.readLine()) != null) {
                    buffer.append(curLine);
                }

                if (buffer.length() == 0) {
                    return "";
                }

                json = buffer.toString();
            } catch (IOException ioe) {
                Log.e(CLASS_NAME, "Error", ioe);
                return "";
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        Log.e(CLASS_NAME, "Error", ioe);
                    }
                }
            }
            return json;
        }

        private Uri getUrl(String postalCode) {
            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, postalCode)
                    .appendQueryParameter(FORMAT_PARAM, FORMAT_TYPE)
                    .appendQueryParameter(UNIT_PARAM, UNIT_TYPE)
                    .appendQueryParameter(NUM_DAYS_PARAM, NUM_DAYS)
                    .appendQueryParameter(API_PARAM, API_ID)
                    .build();
        }

        private String getReadableDateString(long time){

            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String formatHighLows(double high, double low) {

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {

                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime;

                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }
    }


}
