package ballard.ayden.nearestFlight;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class FlightDataUpdaterAsync extends AsyncTask<Void, Void, Void> {

    private Handler handler; //handler for new markers
    private final int UPDATE_COMPLETE = 102; //message code for update complete

    /**
     * Constructor for FlightDataUpdaterAsync object
     * @param handler - handler
     */
    public FlightDataUpdaterAsync(Handler handler){
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

        SharedFlightQueue sharedFlightQueue = new SharedFlightQueue();
        //todo change this to an array of threads
        FlightUpdaterThread threadA = new FlightUpdaterThread(sharedFlightQueue, handler);
        FlightUpdaterThread threadB = new FlightUpdaterThread(sharedFlightQueue, handler);
        FlightUpdaterThread threadC = new FlightUpdaterThread(sharedFlightQueue, handler);

        threadA.start();
        threadB.start();
        threadC.start();
        try{
            threadB.join();
            threadC.join();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void voids){
        //post execute
        Message postMsg = handler.obtainMessage();
        postMsg.what = UPDATE_COMPLETE;
        handler.sendMessage(postMsg);
    }



}
