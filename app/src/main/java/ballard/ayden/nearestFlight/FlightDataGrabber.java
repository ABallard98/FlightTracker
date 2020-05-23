/**
 * This class is made up of static methods to collect flight data from the OpenSky API. Data is
 * collected in the form of a JSON Array, which can be used to make the Flight objects.
 * @Author Ayden Ballard
 */

package ballard.ayden.nearestFlight;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.*;

public class FlightDataGrabber {

    //public data about flights
    private static final String FLIGHT_DATA_URL = "https://opensky-network.org/api/states/all";

    /**
     * Method to collect flight data in the form of a JSONArray
     * @return JSONArray - flight data
     */
    public static JSONArray grabData(){
        JSONArray jsonArray;
        try{
            JSONObject json = readJsonFromUrl(FLIGHT_DATA_URL);
            jsonArray = json.getJSONArray("states"); //get info of state of flights
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }

    /**
     * Method to find the nearest flight from the users longitude, latitude coordinates
     * @param jsonArray - flight data
     * @param myLong - user longitude
     * @param myLat - user latitude
     * @return Flight - nearest flight
     */
    public static Flight findNearestFlight(JSONArray jsonArray, double myLong, double myLat){
        //create flight objects
        ArrayList<Flight> flights = new ArrayList<>();
        try{
            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray subArray = jsonArray.getJSONArray(i);
                flights.add(new Flight(subArray, myLong, myLat));
            }
        } catch(Exception e){

        }
        //find nearest flight
        Flight lowestDistance = flights.get(0);
        for(Flight f : flights){
            if(f.getDistanceFromUser() < lowestDistance.getDistanceFromUser() && f.getDistanceFromUser() != 0){
                lowestDistance = f;
            }
        }
       return lowestDistance;
    }

    /**
     * Method to translate a JSONArray of flight data to an ArrayList of Flights
     * @param jsonArray - flight data
     * @return ArrayList of Flight type
     */
    public static ArrayList<Flight> getFlights(JSONArray jsonArray){
        ArrayList<Flight> flights = new ArrayList<>();
        try{
            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray subArray = jsonArray.getJSONArray(i);
                flights.add(new Flight(subArray));
            }
        } catch(Exception e){

        }
        return flights;
    }

    /**
     * Method to read URL and return the JSON data in the form of a JSON Object
     * @param url - flight data URL
     * @return JSONObject - flight data
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    /**
     * Method to read all text on a web page and return it as a String
     * @param rd - Reader
     * @return String
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
