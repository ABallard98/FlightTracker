package ballard.ayden.nearestFlight;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap gmap; //Google map object
    private JSONArray flightData; //Flight data
    private ArrayList<Flight> flights; //flight objects
    private SupportMapFragment mapFragment; //map fragment
    private RelativeLayout loadingPanel; //Loading panel for progress bar
    private Handler handler;

    private int UPDATE_MARKER = 101;
    private int UPDATE_COMPLETE = 102;

    private HashMap<String, Marker> markerHashMap;
    private HashMap<String, LatLng> originalFlightLocationHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise objects
        this.loadingPanel = findViewById(R.id.loadingPanel);
        this.loadingPanel.setVisibility(View.VISIBLE);

        //initialise data structures
        this.markerHashMap = new HashMap<>();
        this.originalFlightLocationHashMap = new HashMap<>();

        //set map properties
        this.mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getView().setVisibility(View.GONE);
        mapFragment.getMapAsync(this);

        //handler for receiving marker updates
        handler = new Handler(){
            @Override
            public void handleMessage(final Message msg){
                super.handleMessage(msg);
                if(msg.what == UPDATE_MARKER){
                    try{
                        //get new flight object from msg
                        Flight newFlight = (Flight) msg.obj;
                        //find old marker and remove from google maps
                        Marker oldMarker = markerHashMap.get(newFlight.getIcao());
                        oldMarker.remove();
                        //Load bitmap flight icon
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.airplane_icon);
                        //rotate icon to match direction of flight
                        icon = Bitmap.createScaledBitmap(icon,
                                50,50, true);
                        icon = rotateIcon(icon, newFlight.getDegrees());
                        //set BitMap descriptor
                        BitmapDescriptor bitmapDescriptor =
                                BitmapDescriptorFactory.fromBitmap(icon);

                        updateFlightLocation(newFlight, bitmapDescriptor);

                    }  catch (Exception e){

                    }
                } else if(msg.what == UPDATE_COMPLETE){
//                    FlightDataUpdaterAsync flightDataUpdater =
//                            new FlightDataUpdaterAsync(flights,markerHashMap,gmap,handler);
//                    flightDataUpdater.execute();
                }
            }
        }; //end of handler
    } //end of onCreate

    private synchronized void updateFlightLocation(final Flight newFlight, final BitmapDescriptor bitmapDescriptor){
        //replace marker
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update marker
                final MarkerOptions newMarker = new MarkerOptions()
                        .position(newFlight.getLatLng())
                        .title("Flight origin: " + newFlight.getOriginCountry())
                        .snippet("Altitude: " + newFlight.getAltitude())
                        .icon(bitmapDescriptor);

                //get original latlng of flight
                final LatLng oldLatLng = originalFlightLocationHashMap.get(newFlight.getIcao());
                Marker m = gmap.addMarker(newMarker);

                markerHashMap.put(newFlight.getIcao(), m);
                //create and add polyline
                PolylineOptions line = new PolylineOptions().add(oldLatLng,
                        m.getPosition()).color(Color.RED);gmap.addPolyline(line);
            }
        });
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
                FlightDataUpdaterAsync flightDataUpdaterAsync = new FlightDataUpdaterAsync(handler);
                flightDataUpdaterAsync.execute();
                Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show();

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
        //set style of google maps
        gmap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mapFragment.getView().setVisibility(View.VISIBLE); //make visible to user
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
        } catch (Exception e){
            e.printStackTrace();
        }

        final ArrayList<Flight> flights = grabFlightDataTask.getFlights();

        this.flights = flights;
        //add each flight to google maps
        for(int i = 0; i < flights.size(); i++){
            addFlightMarker(gmap, flights.get(i));
        }
        //set loading panel visibility to gone
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        //enable gestures on google maps
        gmap.getUiSettings().setScrollGesturesEnabled(true);

        //startUpdaterThread(flights);

    }

    /**
     * Method to add flight marker to the google map object
     * @param gmap - google map object
     * @param flight - Flight object
     */
    private void addFlightMarker(final GoogleMap gmap, Flight flight){
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

                    MarkerOptions newMarker = new MarkerOptions()
                            .position(latLng)
                            .title("Flight origin: " + f.getOriginCountry())
                            .snippet("Altitude: " + f.getAltitude())
                            .icon(bitmapDescriptor);

                    Marker m = gMap.addMarker(newMarker);
                    markerHashMap.put(f.getIcao(), m); //add to hash map
                    originalFlightLocationHashMap.put(f.getIcao(), latLng);

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
