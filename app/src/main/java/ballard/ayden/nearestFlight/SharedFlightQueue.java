package ballard.ayden.nearestFlight;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedFlightQueue {

    private ArrayList<Flight> flights;
    private Lock lock;
    private boolean isFlightsUpdated;

    public SharedFlightQueue(){
        this.lock = new ReentrantLock();
        //grab updated data
        JSONArray flightData = FlightDataGrabber.grabData();
        //turn updated data into an ArrayList of flights
        ArrayList<Flight> newFlights = FlightDataGrabber.getFlights(flightData);

        this.flights = newFlights;
        if(this.flights.size() > 0){
            this.isFlightsUpdated = false;
        } else {
            this.isFlightsUpdated = true;
        }
    }

    public synchronized Flight dequeueFlight(){
        if(!flights.isEmpty()){
            lock.lock();
            Flight toReturn = flights.get(0);
            flights.remove(0);
            lock.unlock();

            if(flights.size() == 0){
                this.isFlightsUpdated = true;
            }
            return toReturn;
        }
        return null;
    }

    public synchronized  Flight peekFlight(){
        lock.lock();
        Flight toReturn = flights.get(0);
        lock.unlock();
        return  toReturn;
    }

    public boolean isFlightsUpdated(){
        return isFlightsUpdated;
    }

    public boolean isEmpty(){
        return flights.isEmpty();
    }

    public int getSize(){
        return flights.size();
    }


}
