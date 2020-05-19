package ballard.ayden.nearestFlight;

import android.os.Handler;
import android.os.Message;

public class FlightUpdaterThread extends Thread{

    private Handler handler;
    private SharedFlightQueue sharedFlightQueue;
    private final int UPDATE_MARKER = 101; //message code for updating markers

    public FlightUpdaterThread(SharedFlightQueue sharedFlightQueue, Handler handler){
        this.sharedFlightQueue = sharedFlightQueue;
        this.handler = handler;
    }

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

