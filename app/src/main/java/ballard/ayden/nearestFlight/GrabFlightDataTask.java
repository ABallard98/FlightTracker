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

public class GrabFlightDataTask extends AsyncTask<Void, Void, JSONArray> {

    private final String FLIGHT_DATA_URL = "https://opensky-network.org/api/states/all";
    private JSONArray flightData;

    public GrabFlightDataTask(JSONArray jsonArray){
        this.flightData = jsonArray;

    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        JSONArray jsonArray;
        try{
            JSONObject json = readJsonFromUrl(FLIGHT_DATA_URL);
            jsonArray = json.getJSONArray("states"); //get info of state of flights
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

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
