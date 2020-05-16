package ballard.ayden.nearestFlight;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class FlightDataUpdater extends AsyncTask<Void, Void, Void> {

    private ArrayList<Flight> flights; //flight data
    private HashMap<String, Marker> markerHashMap; //HashMap of markers on map
    private WeakReference<GoogleMap> gMap; //google map object
    private Context context; //context of application
    private Handler handler; //handler for new markers

    private final int UPDATE_MARKER = 101; //message code for updating markers

    /**
     * Constructor for FlightDataUpdater object
     * @param flights - ArrayList of flights
     * @param markerHashMap - HashMap of markers
     * @param gMap - google map object
     * @param context - context of application
     * @param handler - handler
     */
    public FlightDataUpdater(ArrayList<Flight> flights, HashMap<String, Marker> markerHashMap,
                             GoogleMap gMap, Context context, Handler handler){
        this.context = context;
        this.gMap = new WeakReference<>(gMap);
        this.flights = flights;
        this.markerHashMap = markerHashMap;
        this.handler = handler;
    }

    /**
     * Method to collect new data of flights, and create a message for the MainActivity to update
     * the marker on the google map object
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {

        //grab updated data
        JSONArray flightData = FlightDataGrabber.grabData();
        //turn updated data into an ArrayList of flights
        ArrayList<Flight> newFlights = FlightDataGrabber.getFlights(flightData);

        for(Flight f : newFlights){
            for(Flight f1 : this.flights){
                if(f.getIcao().equals(f1.getIcao())){ //if flight matches - update map location
                    if(!isFlightEqualLatLng(f,f1)){ //if the flights have different lat lng
                        Marker marker = markerHashMap.get(f.getIcao());

                        //create message for handler in MainActivity
                        Message msg = handler.obtainMessage();
                        msg.what = UPDATE_MARKER;
                        msg.obj = marker;
                        msg.arg1 = (int) f.getLatitude();
                        msg.arg2 = (int) f.getLongitude();

                        handler.sendMessage(msg); //send msg to handler
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method to see if 2 planes have the same latitude and longitude
     * @param f - flight
     * @param f1 - flight
     * @return true if they both have the same latitude and longitude
     */
    private boolean isFlightEqualLatLng(Flight f, Flight f1){
        if(f.getLongitude() == f1.getLongitude() && f.getLatitude() == f1.getLatitude()){
            return true;
        }
        return false;
    }
}