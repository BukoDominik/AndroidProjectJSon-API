package com.example.android.quakereport;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

/**
 * Created by D3B3st on 2/28/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            // Inflate nasz adapter na podstawie earthquake_list_item - widoku ktory chcemy uzywac
            // w naszym adapterze
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }
        // Zapisujemy bierzace zdarzenie
        Earthquake currentEarthquake = getItem(position);
        // zapisujemy view o nazwie magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        // Przypisujemy wartosc danego zdarzenia metoda getMagnitude do Stringa
        String formattedMagnitude = formatMagnitude(currentEarthquake.getMagnitude());
        // Ustawiamy tekst naszego View wartoscia ze Stringa formattedMagnitude
        magnitudeView.setText(formattedMagnitude);


        // Zapisujemy View o nazwie location i near
        TextView locationView = (TextView) listItemView.findViewById(R.id.location);
        TextView nearbyView = (TextView) listItemView.findViewById(R.id.near);
        // Analogicznie do magnitude ustalamy stringa z nazwa lokacji
        String wholeLocation = currentEarthquake.getLocation();
        // Pobieramy dane potrzebne do modyfikacji naszego Stringa z nazwa
        int numberOfChars = wholeLocation.length();
        int numberOf = wholeLocation.indexOf("of");
        // Log pomocniczy pozwalajacy sprawdzic czy String zostal odpowiednio0 zmotyfikowany i liczbne
        // elementow w liscie
        Log.d(TAG, "getView: "+numberOf+":Liczba na ktorym jest of" + numberOfChars);
        // Modyfikujemy nasze dane tak zeby rozbic je na 2 czesci - glowne miejsce wystepowania
        // oraz gdzie dokladnie wystepilo zdarzenie
        String onlyLocation = wholeLocation.substring(numberOf+3);
        String onlyNearby = wholeLocation.substring(0,numberOf+3);


        // Korzystajac z klasy GradientDrawable pobieramy aktualny kolor backgroundu w xml
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // wywolujemy funkcje getMagnitudeColor ze switcha napisanego ponizej (zmienia on kolor
        // kolka w zaleznosci od intenzywnosci trzesienia Ziemii)
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // ustawiamy kolor kolka z folderu drawable/magnitude_circle
        magnitudeCircle.setColor(magnitudeColor);

        // W szczegolnych przypadkach gdy nie bylo podane gdzie dokladnie znajdowalo sie trzesienie
        // Trzeba bylo zamienic drugiego stringa ktorego rozbilismy wczesniej na tekst "Near the"
        if (numberOf != -1){locationView.setText(onlyLocation);}
        else  {locationView.setText(currentEarthquake.getLocation());}

        if (numberOf !=-1) nearbyView.setText(onlyNearby);
        else {nearbyView.setText("Near the ");}

        // Analogicznie do magnitude i lokacji postepujemy z czasem
        Date dateObject = new Date(currentEarthquake.getDate());

        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        //zeby uzyskac odpowiedni format daty korzystamy z metody formatDate napisanej ponizej
        String formattedDate = formatDate(dateObject);
        // ustawiamy nasza zformatowana date
        dateView.setText(formattedDate);
        // Tak samo jak date formatujemy czas metoda napisana ponizej
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        String formattedTime = formatTime(dateObject);

        timeView.setText(formattedTime);
        // zwracamy nasz zmieniony listItemView w adapterze
        return listItemView;

    }

    // metoda formatujaca wyswietlanie magnitude w formacie dziesietnym
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }
    //metoda formatujaca date
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
    // metoda formatujaca czas
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
    // switch zmieniajacy kolory naszego kolka w ktorym sa dane dotyczace trzesienia
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
