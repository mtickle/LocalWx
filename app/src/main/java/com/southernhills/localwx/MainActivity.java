package com.southernhills.localwx;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.util.ArrayList;
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

//        String forecastURL;
//        TextView txtForecastLink = (TextView) findViewById(R.id.txtForecastLink);
//        forecastURL = txtForecastLink.getText().toString();
//
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(forecastURL));
//        startActivity(browserIntent);

    }

    private void updateConditions() {

        //--- Declarations
        String weatherURL;
        String mapURL;
        int randomInt = 0;
        String Location;
        String currentTemp;
        String humidity;
        String weather;
        String observationTime;
        String observationDate;
        String observed;

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
        TextView txtWeather = (TextView) findViewById(R.id.txtWeather);
        TextView tvToday = (TextView) findViewById(R.id.tvToday);
        TextView tvTonight = (TextView) findViewById(R.id.tvTonight);
        TextView tvTomorrow = (TextView) findViewById(R.id.tvTomorrow);
        TextView tvTomorrowNight = (TextView) findViewById(R.id.tvTomorrowNight);
        TextView tvDay3 = (TextView) findViewById(R.id.tvDay3);
        TextView tvDay3Night = (TextView) findViewById(R.id.tvDay3Night);
        TextView tvDay4 = (TextView) findViewById(R.id.tvDay4);
        TextView tvDay4Night = (TextView) findViewById(R.id.tvDay4Night);
        TextView tvDay5 = (TextView) findViewById(R.id.tvDay5);
        TextView tvDay5Night = (TextView) findViewById(R.id.tvDay5Night);
        TextView tvHighToday = (TextView) findViewById(R.id.tvHighToday);
        TextView tvHighTomorrow = (TextView) findViewById(R.id.tvHighTomorrow);
        TextView tvHighDay3 = (TextView) findViewById(R.id.tvHighDay3);
        TextView tvHighDay4 = (TextView) findViewById(R.id.tvHighDay4);
        TextView tvHighDay5 = (TextView) findViewById(R.id.tvHighDay5);
        TextView tvLowToday = (TextView) findViewById(R.id.tvLowTonight);
        TextView tvLowTomorrow = (TextView) findViewById(R.id.tvLowTomorrow);
        TextView tvLowDay3 = (TextView) findViewById(R.id.tvLowDay3);
        TextView tvLowDay4 = (TextView) findViewById(R.id.tvLowDay4);
        TextView tvLowDay5 = (TextView) findViewById(R.id.tvLowDay5);

        TextView tvWeatherToday = (TextView) findViewById(R.id.tvWeatherToday);
        TextView tvWeatherTomorrow = (TextView) findViewById(R.id.tvWeatherTomorrow);
        TextView tvWeatherTomorrowNight = (TextView) findViewById(R.id.tvWeatherTomorrowNight);
        TextView tvWeatherDay3 = (TextView) findViewById(R.id.tvWeatherDay3);
        TextView tvWeatherDay3Night = (TextView) findViewById(R.id.tvWeatherDay3Night);
        TextView tvWeatherDay4 = (TextView) findViewById(R.id.tvWeatherDay4);
        TextView tvWeatherDay4Night = (TextView) findViewById(R.id.tvWeatherDay4Night);
        TextView tvWeatherDay5 = (TextView) findViewById(R.id.tvWeatherDay5);
        TextView tvWeatherDay5Night = (TextView) findViewById(R.id.tvWeatherDay5Night);

        //--- Old stuff for the map
//        Random randomGenerator = new Random();
//        for (int idx = 1; idx <= 10; ++idx) {
//            randomInt = randomGenerator.nextInt(100000);
//        }
//
//        mapURL = "http://api.wunderground.com/api/709ef2e627f5129c/radar/image.gif?centerlat=" + String.valueOf(latitude) + "&centerlon=" + String.valueOf(longitude) + "&radius=50&width=600&height=280&newmaps=1&smooth=1&rnd=" + randomInt;


        //--- Current conditions start here
        try {

            //--- Assemble the URL to grab the XML data
            weatherURL = "http://forecast.weather.gov/MapClick.php?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&unit=0&lg=english&FcstType=dwml";

            //--- Load up the XML from the webpage.
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(weatherURL); // getting XML
            Document doc = parser.getDomElement(xml); // getting DOM element
            NodeList nList = doc.getElementsByTagName("data");

            //--- Get a reference to the first data node, which is where we
            //--- will get the location name and some other stuff.
            Node nNode = nList.item(1);
            System.out.println("\nCurrent Element: " + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                //--- Get the location value
                Element point = (Element) (eElement.getElementsByTagName("area-description").item(0));
                Location = point.getTextContent();

                //--- Now get the current temp, which is the "apparent" value temp.
                Element tempNode = (Element) (eElement.getElementsByTagName("temperature").item(0));
                NodeList nlTemp = tempNode.getChildNodes();
                Node n = nlTemp.item(1);
                currentTemp = n.getTextContent();

                //--- Now get the humidity.
                Element humidNode = (Element) (eElement.getElementsByTagName("humidity").item(0));
                NodeList nlHumid = humidNode.getChildNodes();
                Node h = nlHumid.item(1);
                humidity = h.getTextContent();

                //--- Now get the weather.
                Element weatherNode = (Element) (eElement.getElementsByTagName("weather-conditions").item(0));
                weather = weatherNode.getAttribute("weather-summary");

                //--- And now get the last updated time and date.
                Element whenNode = (Element) (eElement.getElementsByTagName("start-valid-time").item(0));
                observationTime = whenNode.getTextContent();
                String[] parts = observationTime.split("T");
                observationDate = parts[0];
                observationTime = parts[1].replace(":00-04:00", "");
                observed = observationDate + " at " + observationTime;

                //--- Set these values in the interface
                txtLocation.setText(Location);
                txtObservationTime.setText(observed);
                txtTempF.setText(currentTemp);
                txtWeather.setText(weather);

                //--- Get the PERIOD NAMES for the forecast
                NodeList fList = doc.getElementsByTagName("time-layout");
                Node fNode = fList.item(0);
                NodeList fcNodes = fNode.getChildNodes();
                //--- Create an array of the time periods to show later
                ArrayList<String> periodList = new ArrayList<String>();
                for (int i = 0; i < fcNodes.getLength(); i++) {
                    Node timeNode = fcNodes.item(i);
                    if (timeNode.getNodeName().equals("start-valid-time")) {
                        Element e = (Element) timeNode;
                        periodList.add(e.getAttribute("period-name"));
                    }
                }

                //--- Get the MAX TEMPS for the forecast
                NodeList maxTempNodeList = doc.getElementsByTagName("temperature");
                Node maxTempParentNode = maxTempNodeList.item(0);
                NodeList maxTempNodes = maxTempParentNode.getChildNodes();
                //--- Create an array of the periods
                ArrayList<String> maxTempList = new ArrayList<String>();
                for (int i = 0; i < maxTempNodes.getLength(); i++) {
                    Node maxTempNode = maxTempNodes.item(i);
                    if (maxTempNode.getNodeName().equals("value")) {
                        maxTempList.add(maxTempNode.getTextContent());
                    }
                }

                //--- Get the MIN TEMPS for the forecast
                NodeList minTempNodeList = doc.getElementsByTagName("temperature");
                Node minTempParentNode = minTempNodeList.item(1);
                NodeList minTempNodes = minTempParentNode.getChildNodes();
                //--- Create an array of the periods
                ArrayList<String> minTempList = new ArrayList<String>();
                for (int i = 0; i < minTempNodes.getLength(); i++) {
                    Node minTempNode = minTempNodes.item(i);
                    if (minTempNode.getNodeName().equals("value")) {
                        minTempList.add(minTempNode.getTextContent());
                    }
                }

                //--- Get the WEATHER for the forecast
                NodeList weatherNodeList = doc.getElementsByTagName("weather");
                Node weatherParentNode = weatherNodeList.item(0);
                NodeList weatherNodes = weatherParentNode.getChildNodes();
                //--- Create an array of the periods
                ArrayList<String> weatherList = new ArrayList<String>();
                for (int i = 0; i < weatherNodes.getLength(); i++) {
                    Node weatherForecastNode = weatherNodes.item(i);
                    if (weatherForecastNode.getNodeName().equals("weather-conditions")) {
                        Element e = (Element) weatherForecastNode;
                        periodList.add(e.getAttribute("weather-summary"));
                    }
                }

                //--- Start adding stuff to the forecast grid at the bottom.
                //--- First, add the period names
                tvToday.setText(periodList.get(0));
                tvTonight.setText(periodList.get(1));
                tvTomorrow.setText(periodList.get(2));
                tvTomorrowNight.setText(periodList.get(3));
                tvDay3.setText(periodList.get(4));
                tvDay3Night.setText(periodList.get(5));
                tvDay4.setText(periodList.get(6));
                tvDay4Night.setText(periodList.get(7));
                tvDay5.setText(periodList.get(8));
                tvDay5Night.setText(periodList.get(9));

                //--- Now add the high temps
                tvHighToday.setText(maxTempList.get(0));
                tvHighTomorrow.setText(maxTempList.get(1));
                tvHighDay3.setText(maxTempList.get(2));
                tvHighDay4.setText(maxTempList.get(3));
                tvHighDay5.setText(maxTempList.get(4));

                //--- And here are the low temps
                tvLowToday.setText(minTempList.get(0));
                tvLowTomorrow.setText(minTempList.get(1));
                tvLowDay3.setText(minTempList.get(2));
                tvLowDay4.setText(minTempList.get(3));
                tvLowDay5.setText(minTempList.get(4));

                //--- Now the weather conditions
                tvWeatherToday.setText(weatherList.get(0));

            }
        } catch (Exception ex) {

            System.out.println(ex.toString());
        }


        //--- Now let's try to craft a forecast


//
//        XMLParser parser = new XMLParser();
//        String xml = parser.getXmlFromUrl(weatherURL); // getting XML
//        Document doc = parser.getDomElement(xml); // getting DOM element
//        NodeList nlHeader = doc.getElementsByTagName("temperature");
//
//        Node node = nlHeader.item(0);
//        if(node instanceof Element) {
//            String data = ((Element) node).getAttribute("name");
//            System.out.println(data);
//        }


//        System.out.println(nlHeader);

        //          Element headerElement = (Element) nlHeader.item(0);
        //Element e = (Element) headerElement.item(0);
        //System.out.println(parser.getValue(e, "parameters "));

        //txtLocation.setText(parser.getValue(headerElement, "city"));


//        NodeList nlHeader = doc.getElementsByTagName("observation_location");
//        Element headerElement = (Element) nlHeader.item(0);
//        txtLocation.setText(parser.getValue(headerElement, "city"));
//
//        NodeList nl = doc.getElementsByTagName("current_observation");
//        Element e = (Element) nl.item(0);
//        txtObservationTime.setText(parser.getValue(e, "observation_time"));
//        txtTempF.setText(parser.getValue(e, "temp_f"));
//        txtForecastLink.setText(parser.getValue(e, "forecast_url"));
//        txtWeather.setText(parser.getValue(e, "weather"));


//        int loader = R.drawable.map;
//        ImageView image = (ImageView) findViewById(R.id.ivMap);
//        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
//        imgLoader.DisplayImage(mapURL, loader, image);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
