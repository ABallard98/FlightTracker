package ballard.ayden.nearestFlight;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private JSONArray flightData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap gmap){
        this.gmap = gmap;
        gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        JSONArray jsonArray = null;
        GrabFlightDataTask grabFlightDataTask = new GrabFlightDataTask(jsonArray);
        try{
            grabFlightDataTask.execute().get();
            jsonArray = grabFlightDataTask.getFlightData();
        } catch (Exception e){

        }

        ArrayList<Flight> flights = FlightDataGrabber.getFlights(jsonArray);
        for(int i = 0; i < flights.size(); i++){
            addFlightMarker(gmap, flights.get(i));
        }
    }



    private void addFlightMarker(GoogleMap gmap, Flight flight){
        if(flight.getLatitude() != 0 && flight.getLongitude() != 0 && flight.getDegrees() != 0 &&
                 !flight.getCallsign().equals("") && flight.getAltitude() != 0){
            LatLng latLng = new LatLng(flight.getLatitude(), flight.getLongitude());

            Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.airplane_icon);
            icon = Bitmap.createScaledBitmap(icon,50,50, true);
            icon = rotateIcon(icon, flight.getDegrees());
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);

            gmap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Flight from: " + flight.getOriginCountry())
                    .snippet("Altitude: " + flight.getAltitude()+"m")
                    .icon(bitmapDescriptor));
        }
    }


    private Bitmap rotateIcon(Bitmap source, double degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate((float) degrees);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(),
                source.getHeight(), matrix, true);
    }
}
