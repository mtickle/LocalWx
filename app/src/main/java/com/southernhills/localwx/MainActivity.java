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
        //TextView txtForecastLink = (TextView) findViewById(R.id.txtForecastLink);
        TextView txtWeather = (TextView) findViewById(R.id.txtWeather);

        Random randomGenerator = new Random();
        for (int idx = 1; idx <= 10; ++idx) {
            randomInt = randomGenerator.nextInt(100000);
        }

        mapURL = "http://api.wunderground.com/api/709ef2e627f5129c/radar/image.gif?centerlat=" + String.valueOf(latitude) + "&centerlon=" + String.valueOf(longitude) + "&radius=50&width=600&height=280&newmaps=1&smooth=1&rnd=" + randomInt;



        //--- Current conditions start here
        try {

            weatherURL = "http://forecast.weather.gov/MapClick.php?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&unit=0&lg=english&FcstType=dwml";

            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(weatherURL); // getting XML
            Document doc = parser.getDomElement(xml); // getting DOM element
            NodeList nList = doc.getElementsByTagName("data");

            String Location;
            String currentTemp;
            String humidity;
            String weather;
            String observationTime;
            String observationDate;
            String observed;

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

                Element whenNode = (Element) (eElement.getElementsByTagName("start-valid-time").item(0));
                observationTime = whenNode.getTextContent();
                String[] parts = observationTime.split("T");
                //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                observationDate = parts[0];
                observationTime = parts[1].replace(":00-04:00", "");
                observed = observationDate + " at " + observationTime;

                //--- Some debug stuff.
                System.out.println("Location: " + Location);
                System.out.println("Observed: " + observed);
                System.out.println("Current Temp: " + currentTemp);
                System.out.println("Humidity: " + humidity);
                System.out.println("Weather: " + weather);

                txtLocation.setText(Location);
                txtObservationTime.setText(observed);
                txtTempF.setText(currentTemp);
                txtWeather.setText(weather);

                //--- Now let's try to craft a forecast
                NodeList fList = doc.getElementsByTagName("time-layout");

                //for(int i = 0; i < nList.getLength(); i++){
                Node fNode = fList.item(0);
                System.out.println("\nValid STart Times: " + fNode.getNodeName());

                NodeList fcNodes = fNode.getChildNodes();
                System.out.println("\nfcNodes.getLength(): " + fcNodes.getLength());

                for (int i = 0; i < fcNodes.getLength(); i++) {
                    Node timeNode = fcNodes.item(i);
                    if (timeNode.getNodeName().equals("start-valid-time")) {

                        Element e = (Element)timeNode;
                        System.out.println(e.getAttribute("period-name"));
                    }
                }

//
//
//                   String timeNodeName = timeNode.getNodeName();
//                   System.out.println(timeNodeName);
//
//
//                   if (timeNodeName.equals("start-valid-time")) {
//                       //Element startTime = (Element)(eElement.getElementsByTagName("start-valid-time").item(0).getAttributes("period-name"));
//                       System.out.println(timeNodeName);
//                    if(nNode.getNodeType() == Node.ELEMENT_NODE){
//                        Element eElement = (Element) nNode;
//
//                        Element point = (Element)(eElement.getElementsByTagName("point").item(0));
//                        System.out.println("Location: " + point.getAttribute("latitude") + ", " + point.getAttribute("longitude"));
//                        System.out.println("Start Time: "+eElement.getElementsByTagName("start-valid-time").item(0).getTextContent());
//                        System.out.println("End Time: "+eElement.getElementsByTagName("end-valid-time").item(0).getTextContent());
//
//                        System.out.println("");
//                    }
                //}

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
