package ballard.ayden.nearestFlight;

import android.os.Handler;
import android.os.Message;

public class FlightUpdaterThread extends Thread{

    private Handler handler; //handler to send messages to the Main thread
    private SharedFlightQueue sharedFlightQueue; //SharedFlightQueue
    private final int UPDATE_MARKER = 101; //message code for updating markers

    /**
     * Constructor for FlightUpdaterThread
     * @param sharedFlightQueue - Queue of flights to be updated
     * @param handler - handler to send messages to the main thread
     */
    public FlightUpdaterThread(SharedFlightQueue sharedFlightQueue, Handler handler){
        this.sharedFlightQueue = sharedFlightQueue;
        this.handler = handler;
    }

    /**
     * Method to dequeue an object in the flight shared queue and update the location on the
     * google map by sending a message of the updated information to the main thread
     */
    public void run(){
        while(!sharedFlightQueue.isEmpty()){
            synchronized (sharedFlightQueue){
                Flight flight = sharedFlightQueue.dequeueFlight();
                Message msg = handler.obtainMessage();
                msg.what = UPDATE_MARKER;
                msg.obj = flight; //send new flight

                handler.sendMessage(msg);

                sharedFlightQueue.notifyAll();
            }
        }
    }



}

