package ballard.ayden.nearestFlight;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.time.Instant;

public class GrabFlightDataTask extends AsyncTask<Void, Void, JSONArray> {

    private final String FLIGHT_DATA_URL = "https://opensky-network.org/api/states/all";
    //todo                                                         3 6000 00
    //todo replace time with current time and 2 hours before  - 1589 2306 49 -
    // format of URL "https://opensky-network.org/api/flights/all?begin=1517227200&end=1517230800"
    private String flight_arrival_dept_data_url = "https://opensky-network.org/api/flights/all?begin=";
    private JSONArray flightData;
    private JSONArray flightArrvDeptData;
    private ArrayList<Flight> flights;

    public GrabFlightDataTask(JSONArray jsonArray){
        this.flightData = jsonArray;
        //int currentTime = (int) (new Date().getTime()/1000);
        long currentTime = Instant.now().getEpochSecond() - 86400;
        long prevHourDateTime = currentTime - (3600*2);
        flight_arrival_dept_data_url += prevHourDateTime + "&end=" + currentTime;

        System.out.println(flight_arrival_dept_data_url);

    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        JSONArray jsonArray;
        try{
            JSONObject json = readJsonFromUrl(FLIGHT_DATA_URL);
            jsonArray = json.getJSONArray("states"); //get info of state of flights
            this.flights = FlightDataGrabber.getFlights(jsonArray);
            try{
                this.flightArrvDeptData = readJsonArrayFromUrl(flight_arrival_dept_data_url);
                System.out.println(this.flightArrvDeptData.toString());

                //todo call arrv dept finder here
                this.flights = matchIcaoArrvDept(flightArrvDeptData,flights);


            } catch (Exception e){
                e.printStackTrace();
            }


        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        this.flightData = jsonArray;
        return jsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray){
        //post execute
    }

    @Override
    protected void onProgressUpdate(Void... voids){
        //progress update
    }

    public JSONArray getFlightData(){
        return this.flightData;
    }

    public ArrayList<Flight> getFlights(){
        return this.flights;
    }

    /**
     * Method to read URL and return the JSON data in the form of a JSON Object
     * @param url - flight data URL
     * @return JSONObject - flight data
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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

    private JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException{
        InputStream is = new URL(url).openStream();
        try{
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray jsonArray = new JSONArray(jsonText);
            return jsonArray;
        } finally {
            is.close();
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private ArrayList<Flight> matchIcaoArrvDept(JSONArray flightArrvDeptData, ArrayList<Flight> flights) throws JSONException {
        for(int i = 0; i < flightArrvDeptData.length(); i++){
            JSONObject obj = (JSONObject) flightArrvDeptData.get(i);
            //todo fix ICAO24 matcher for arrival and departure
//            for(Flight f : flights){
//                if(f.getIcao().equals(obj.getString("icao24"))&& !obj.getString("estDepartureAirport").equals("null")){
//                    //if icao matches, assign arrv dept locations
//                    System.out.println("PAIR FOUND - checked: " + i + " planes");
//                    f.setEstDept(obj.getString("estDepartureAirport"));
//                    f.setEstArrival(obj.getString("estArrivalAirport"));
//
//                } else {
//                    f.setEstDept("???");
//                    f.setEstArrival("???");
//                }
//            }
        }
        return flights;
    }

}
