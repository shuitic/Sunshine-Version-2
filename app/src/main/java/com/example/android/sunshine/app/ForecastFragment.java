package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.android.sunshine.app.R;

/**
 * Created by asai_s on 2016/07/07.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForcastAdapter = null;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.layout.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 78/48",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 65/56",
                "Sat - HELP TRAPPED IN WEATHERSTATION - 60/51",
                "Sun - Sunny - 88/68"
        };

        List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));
        mForcastAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);
        ListView forecastListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(mForcastAdapter);

        return rootView;
    }



    public class FetchWeatherTask extends AsyncTask<String, Void, String> {
        private final String TAG = "FetchWeatherTask";
        @Override
        protected String doInBackground(String... postCode) {
            if (postCode.length == 0 || postCode[0] == null) {
                return null;
            }
            Uri.Builder urlBuilder = new Uri.Builder();
            urlBuilder.scheme("http");
            urlBuilder.authority("api.openweathermap.org");
            urlBuilder.appendPath("/data/2.5/forecast/daily");
            String MODE = "mode";
            String MODE_VALUE = "json";
            String UNITS = "units";
            String UNITS_VALUE = "metric";
            String CNT = "cnt";
            String CNT_VALUE = "7";
            String Q = "q";
            String APPID = "APPID";
            String APPID_VALUE = "b06c2bd7516a2ee82bbc38d6a7fb3f35";

            urlBuilder.appendQueryParameter(MODE, MODE_VALUE);
            urlBuilder.appendQueryParameter(UNITS, UNITS_VALUE);
            urlBuilder.appendQueryParameter(CNT, CNT_VALUE);
            urlBuilder.appendQueryParameter(Q, postCode[0]);
            urlBuilder.appendQueryParameter(APPID, APPID_VALUE);

            Log.i(TAG, urlBuilder.toString());
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL(urlBuilder.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.i(TAG, forecastJsonStr);
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String forecastJsonStr) {
            super.onPostExecute(forecastJsonStr);
        }
    }

}
