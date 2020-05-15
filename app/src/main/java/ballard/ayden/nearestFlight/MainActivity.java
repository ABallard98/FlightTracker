package ballard.ayden.nearestFlight;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap gmap; //Google map object
    private JSONArray flightData; //Flight data
    private SupportMapFragment mapFragment; //map fragment
    private RelativeLayout loadingPanel; //Loading panel for progress bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise objects
        this.loadingPanel = findViewById(R.id.loadingPanel);
        this.loadingPanel.setVisibility(View.VISIBLE);

        this.mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        //set map properties
        mapFragment.getView().setVisibility(View.GONE);
        mapFragment.getMapAsync(this);

    }

    /**
     * Method to initialise action bar
     * @param menu - menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method to perform action corresponding to menu item clicked
     * @param item - action clicked
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case(R.id.action_refresh): //refresh flight data
                Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show();
                refreshMarkers();
                Toast.makeText(this,"Refreshed.",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Method to set map properties upon loading
     * @param gmap - GoogleMap object
     */
    @Override
    public void onMapReady(GoogleMap gmap){
        this.gmap = gmap;
        //set style
        gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mapFragment.getView().setVisibility(View.VISIBLE);
        gmap.setOnMapLoadedCallback(this);
        gmap.getUiSettings().setScrollGesturesEnabled(false); //false until markers placed
    }

    /**
     * Method to grab flight data and place markers upon map loaded
     */
    @Override
    public void onMapLoaded() {
        JSONArray jsonArray = null;
        GrabFlightDataTask grabFlightDataTask = new GrabFlightDataTask(jsonArray);
        try{
            grabFlightDataTask.execute().get();
            jsonArray = grabFlightDataTask.getFlightData();
        } catch (Exception e){

        }

        ArrayList<Flight> flights = grabFlightDataTask.getFlights();
        for(int i = 0; i < flights.size(); i++){
            addFlightMarker(gmap, flights.get(i));
        }

        System.out.println("Loaded.");
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        gmap.getUiSettings().setScrollGesturesEnabled(true);
    }

    /**
     * Method to refresh flight markers on the GoogleMap object
     */
    private void refreshMarkers(){
        JSONArray jsonArray = null;
        GrabFlightDataTask grabFlightDataTask = new GrabFlightDataTask(jsonArray);
        try{
            grabFlightDataTask.execute().get();
            jsonArray = grabFlightDataTask.getFlightData();
        } catch (Exception e){

        }

        ArrayList<Flight> flights = grabFlightDataTask.getFlights();
        gmap.clear();
        for(int i = 0; i < flights.size(); i++){
            addFlightMarker(gmap, flights.get(i));
        }
        System.out.println("Loaded.");
    }

    /**
     * Method to add flight marker to the google map object
     * @param gmap - google map object
     * @param flight - Flight object
     */
    private void addFlightMarker(GoogleMap gmap, Flight flight){
        final Flight f = flight;
        final GoogleMap gMap = gmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if plane has no null properties
                if(f.getLatitude() != 0 && f.getLongitude() != 0 && f.getDegrees() != 0 &&
                        !f.getCallsign().equals("") && f.getAltitude() != 0){
                    LatLng latLng = new LatLng(f.getLatitude(), f.getLongitude());

                    //Load bitmap flight icon
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.airplane_icon);
                    //rotate icon to match direction of flight
                    icon = Bitmap.createScaledBitmap(icon,50,50, true);
                    icon = rotateIcon(icon, f.getDegrees());
                    //set BitMap descriptor
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);
                    //add marker to GoogleMap object
                    gMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Flight origin: " + f.getOriginCountry())
                            .snippet("Altitude: " + f.getAltitude())
                            .icon(bitmapDescriptor));
                }
            }
        });
    }

    /**
     * Method to rotate BitMap icon by x degrees
     * @param source - BitMap image to rotate
     * @param degrees - degrees of rotation
     * @return BitMap - rotated image
     */
    private Bitmap rotateIcon(Bitmap source, double degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate((float) degrees);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(),
                source.getHeight(), matrix, true);
    }


}
