package com.hunterquant.praisethesun;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hunter on 6/3/16.
 */
public class ForecastFragment extends Fragment {

    private String json;

    private static final String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=14546,usa&mode=json&units=metric&cnt=7&APPID=8503bd5b939295e4c4464498068e0c6b";

    public ForecastFragment() {
        json = "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_forecast, container, false);
        String [] forcast = {
          " this", "is", "me"
        };

        List<String> forcastList = new ArrayList<String>(Arrays.asList(forcast));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                                                R.id.list_item_forecast_textview, forcastList);

        ListView listView = (ListView) root.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        return root;
    }


    private class RetrieveWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String className = RetrieveWeatherTask.class.getName();

        @Override
        protected Void doInBackground(Void... params) {
            retrieveWeatherData();
            return null;
        }

        private void retrieveWeatherData() {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(baseUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String curLine;
                while ((curLine = reader.readLine()) != null) {
                    buffer.append(curLine);
                }

                if (buffer.length() == 0) {
                    return;
                }

                json = buffer.toString();
            } catch (IOException ioe) {
                Log.e(className, "Error", ioe);
                return;
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        Log.e(className, "Error", ioe);
                    }
                }
            }
            return;
        }
    }


}
