package com.example.brandon.udacitysunshine;

/**
 * Created by brandon on 7/16/14.
 */

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

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    // Listing all variables for reference within the PlaceholderFragment Class
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Declaring String array to represent data
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Weds -Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 65/56",
                "Sat - HELP TRAPPED IN WEATHERSTATION - 60/51",
                "Sun - Sunny - 80/68"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this fragment's parent activity
                R.layout.list_item_forecast, // ID of list item layout
                R.id.list_item_forecast_textview, // ID of the TextView to populate
                weekForecast); //Forecast data

        // Make sure rootView is initialize before the ListView which will reference it
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get the reference to the ListView and attach this list adapter to it.
        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(mForecastAdapter);

        return rootView;

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try{
                // Construct the URL for the OpenWeatherMap Query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweather.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();


                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
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
                    // Stream was empty. No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
