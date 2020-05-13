package ballard.ayden.nearestFlight;

import org.json.*;

import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        System.out.println("Grabbing flight data...");

        //Big Ben co-ordinates
        double bbLong = 0.1246;
        double bbLat = 51.5007;

        //Eiffel Tower co-ordinates
        double etLong = 2.2945;
        double etLat = 48.8584;

        try {
            JSONArray jsonArray = FlightDataGrabber.grabData();
            if(jsonArray != null){
                //find nearest flight to Ben Ben
                Flight nearestFlight = FlightDataGrabber.findNearestFlight(jsonArray, bbLong, bbLat);
                System.out.println("\nNearest Flight To Big Ben: \n" + nearestFlight.toString());
                //find nearest flight to Eiffel Tower
                nearestFlight = FlightDataGrabber.findNearestFlight(jsonArray, etLong, etLat);
                System.out.println("\nNearest Flight to Eiffel Tower: \n" + nearestFlight.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
