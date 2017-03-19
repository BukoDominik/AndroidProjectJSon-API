/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;


public class EarthquakeActivity extends AppCompatActivity {


    private EarthquakeAdapter mAdapter;

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    // zapytanie do serwera
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tworyzmz obiekt task naszej stworzonej klasy EarthquakeAsyncTask dziedziczacej z AsyncTask
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        // Z klasy Shared prefferences pobieramy wartosci wprowadzone przez uzytkownika
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        // Otwieramy nasz Request
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Za pomoca uriBuildera przemieniamy String zgodnie z ustawieniamy uzytkownika
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        // Zamieniami uriBuilder na Stringa
        String finalWithPreferences = uriBuilder.toString();
        // na rzecz stworzonego obiektu task wywolujemy metode execute (metoda z klasy AsyncTask)
        task.execute(finalWithPreferences);
        // ustawiamy widok na domyslny widok adaptera
        setContentView(R.layout.earthquake_activity);

        // Znajdujemy widok o nazwie list i zapisujemy go w earthquakeListView, zmieniamy typ danych
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Tworzymy nowy adapted z naszej klasy EarthquakeAdapter majacego pusta liste w swoich danych
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Podpinamy adapter mAdapter pod earthquakeListView
        earthquakeListView.setAdapter(mAdapter);

        // Podpinamy onClickListener do naszego earthquakeListView
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Otrzymujemy pozycje obecnego obiektu na ktorym zostal wywolany Listener podczas
                // klikniecia
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Tworzymy nowy obiekt klasy URI ktory pobiera aktualny Url danego obiektu (wywoluje
                // go z klasy Earthquake)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Tworzymy nowy intent na podstawie przypisanego Uri
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Uruchamia nasz stworzony Intent
                startActivity(websiteIntent);
            }

        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Klasa EarthquakeAsyncClass stworzona do obslugi odpowiedzi https parametry to <(Nasz string),
    // (Pusta funkcja),(Lista naszych zdarzen)//

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        // wymagane do obslugi internetowego Intentu -> stworzenie metody pozwalajacej na
        // obsluzenie zapytania internegowego w backgroundzie. Obiekt klasy List przyjmujacy
        // obiekty klasy Earthquake, wywoluje metode fetchEarthquakeData znajdujaca sie w
        // QueryUtilis.
        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Earthquake> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Earthquake> data) {
            // Czyscimy adapter z poprzednich danych
            mAdapter.clear();

            // Updateujemy ListView tylko w przypadku kiedy data nie jest pusta i nie ma wartosci null
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}
