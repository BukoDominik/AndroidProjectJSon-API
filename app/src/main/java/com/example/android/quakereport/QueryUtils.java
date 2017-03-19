package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.input;
import static android.content.ContentValues.TAG;
import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;
import static com.example.android.quakereport.R.id.location;

/**
 * Metody do obslugi JSona.
 */
public final class QueryUtils {

    /** Stworzenie Stringa na dane z odpowiedzi */
    private static final String SAMPLE_JSON_RESPONSE =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    private QueryUtils() {
    }


    public static List<Earthquake> extractEarthquakes(String earthquakeJSON) {
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Pusta ArreyList na nasze Eartquake
        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            // Uzyskujemy dane ktorych porzadamy z odpowiedzi serwera
            JSONObject baseJSONresponse = new JSONObject(earthquakeJSON);
            // Uzyskujemy JSONArrey o nazwie "features"
            JSONArray earthquakeArrey = baseJSONresponse.getJSONArray("features");
            // dla kazdego zdarzenia w JSONArrey tworzymy obiekt z klasy Earthquake

            for(int i=0; i < earthquakeArrey.length(); i++){
                JSONObject currentEarthquake = earthquakeArrey.getJSONObject(i);
                // Otwieramy sciezke properties zawierajace interesujace nas dane
                JSONObject properties =  currentEarthquake.getJSONObject("properties");
                // zapisujemy wlasnosc magnitude
                double magnitude = properties.getDouble("mag");
                // zapisujemy link do zdarzenia
                String url = properties.getString("url");
                // zapisujemy lokacje zdarzenia
                String location = properties.getString("place");
                //zapisujemy czas zdarzenia
                Long time = properties.getLong("time");
                // Tworzymy nowe obiekty klasy Earthquake konstruktorem

                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                // Dodajemy obiekty do ArrayList

                earthquakes.add(earthquake);


            }



        } catch (JSONException e) {
            // W razie wyskoczenia JSONExeption powyzej w try () aplikacja sie nie posypie
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Zwracamy nasza ArrayList
        return earthquakes;
    }
    // Metoda createURL tworzy nowy obiekt klasy URL zadanym parametrem
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;}
        /**
         * Żądanie https dla danego url, sprawdze mozliwosc polaczenia internetowego i poprawnosc
         * zadanego url
         */
    private static String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            // Jezeli url jest pusty nie podejmuje dalszych dzialań.
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // W razie sukcesu przechodzi dalej(response code 200),
                // unstanawia inputStream i zapisuje go w jsonResponse
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // Zamykanie inputStreama czasem wyrzuca IOException co zostalo uwzglednionwe
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        // Tworzy nowy obiekt klasy InputStreamReader do utworzenia Stringa z odpowiedzia serwera z
        // inputStreama uzyskanego wczesniej
        private static String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    /**
     * Metoda publiczna ze wzgledu na wykorzystanie jej w EarthquakeActiwity.java
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        // Tworzy obiekt URL metoda createURL() stworzona wczesniej
        URL url = createUrl(requestUrl);

        // Uzyskuje inputStreamy i polaczenie url metoda makeHTTPRequest
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Metoda extractEarthquakes tworzy ArrayList ktora adapterem wyswietlimy w naszych view
        List<Earthquake> earthquakes = extractEarthquakes(jsonResponse);

        // Zwraca nasza ArrayList
        return earthquakes;
    }

}