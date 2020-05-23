/**
 * This class represents a SharedQueue data structure, implemented using an ArrayList of Flights and
 * a Lock for synchronization.
 * @Author Ayden Ballard
 */

package ballard.ayden.nearestFlight;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedFlightQueue {

    private ArrayList<Flight> flights; //list of flights to update
    private Lock lock;
    private boolean isFlightsUpdated;

    /**
     * Constructor
     */
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

    /**
     * Dequeue and return the first object in the Flight ArrayList
     * @return Flight
     */
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

    /**
     * Return the first object in the Flight ArrayList
     * @return Flight
     */
    public synchronized  Flight peekFlight(){
        lock.lock();
        Flight toReturn = flights.get(0);
        lock.unlock();
        return  toReturn;
    }

    /**
     * Method to see if the Flight ArrayList is empty or not
     * @return boolean - true if empty
     */
    public boolean isEmpty(){
        return flights.isEmpty();
    }

    /**
     * Method to return the current size of the ArrayList
     * @return int - size
     */
    public int getSize(){
        return flights.size();
    }


}
