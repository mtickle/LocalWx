package com.southernhills.localwx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Random;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateConditions();

    }

    public void manuallyUpdateConditions(View view) {
        updateConditions();
    }

    public void openForecast(View view) {

        String forecastURL;
        TextView txtForecastLink = (TextView) findViewById(R.id.txtForecastLink);
        forecastURL = txtForecastLink.getText().toString();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(forecastURL));
        startActivity(browserIntent);

    }

    private void updateConditions() {
        //--- Declarations
        String weatherURL;
        String mapURL;
        int randomInt = 0;

        //--- Get references to the Location services
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //--- Get the lat and long
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        //--- Get references to the form fields
        TextView txtTempF = (TextView) findViewById(R.id.txtTempF);
        TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
        TextView txtObservationTime = (TextView) findViewById(R.id.txtObservationTime);
        TextView txtForecastLink = (TextView) findViewById(R.id.txtForecastLink);
        TextView txtWeather = (TextView) findViewById(R.id.txtWeather);

        Random randomGenerator = new Random();
        for (int idx = 1; idx <= 10; ++idx) {
            randomInt = randomGenerator.nextInt(100000);
        }

        //--- Set the URL to grab the current conditions from Wunderground
        weatherURL = "http://api.wunderground.com/api/709ef2e627f5129c/conditions/q/" + String.valueOf(latitude) + "," + String.valueOf(longitude) + ".xml";
        mapURL = "http://api.wunderground.com/api/709ef2e627f5129c/radar/image.gif?centerlat=" + String.valueOf(latitude) + "&centerlon=" + String.valueOf(longitude) + "&radius=50&width=600&height=280&newmaps=1&smooth=1&rnd=" + randomInt;

        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(weatherURL); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nlHeader = doc.getElementsByTagName("observation_location");
        Element headerElement = (Element) nlHeader.item(0);
        txtLocation.setText(parser.getValue(headerElement, "city"));

        NodeList nl = doc.getElementsByTagName("current_observation");
        Element e = (Element) nl.item(0);
        txtObservationTime.setText(parser.getValue(e, "observation_time"));
        txtTempF.setText(parser.getValue(e, "temp_f"));
        txtForecastLink.setText(parser.getValue(e, "forecast_url"));
        txtWeather.setText(parser.getValue(e, "weather"));


        int loader = R.drawable.map;
        ImageView image = (ImageView) findViewById(R.id.ivMap);
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.DisplayImage(mapURL, loader, image);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
