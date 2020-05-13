package ballard.ayden.nearestFlight;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GoogleMapMakerTask extends AsyncTask<ArrayList<Flight>, Void, Void> {

    private WeakReference<SupportMapFragment> mapFragment;

    public GoogleMapMakerTask(SupportMapFragment mapFragment){
        this.mapFragment = new WeakReference<>(mapFragment);
    }

    @Override
    protected Void doInBackground(ArrayList<Flight>... arrayLists) {
        return null;
    }
}
