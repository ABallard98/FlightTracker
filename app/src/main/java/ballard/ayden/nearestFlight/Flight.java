/**
 * This class represents a Flight, which consists of a planes ICAO, call sign, origin country,
 * time position, last contact, longitude, latitude, degrees from north and altitude.
 * @Author Ayden Ballard
 */

package ballard.ayden.nearestFlight;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

public class Flight {

    private String icao;
    private String callsign;
    private String originCountry;
    private int timePosition;
    private int lastContact;
    private float longitude;
    private float latitude;
    private JSONArray jsonArray;
    private double distance;
    private double degrees;
    private double altitude;

    private String estArrival;
    private String estDept;

    /**
     * Constructor of flight object
     * @param jsonArray - jsonArray of flight data
     * @param userLong - user longitude for distance calculation
     * @param userLat - user latitude for distance calculation
     */
    public Flight(JSONArray jsonArray, double userLong, double userLat){
        this.jsonArray = jsonArray;

        //set flight properties
        try {
            this.icao = jsonArray.getString(0);
            this.callsign = jsonArray.getString(1);
            this.originCountry = jsonArray.getString(2);
            this.timePosition = jsonArray.getInt(3);
            this.lastContact = jsonArray.getInt(4);
            this.longitude = (float) jsonArray.getDouble(5);
            this.latitude = (float) jsonArray.getDouble(6);
            this.degrees = (float) jsonArray.getDouble(10);
        } catch(Exception e){
            //some null errors might occur here
        }

        //calculate distance of flight from user
        this.distance = distance(latitude,longitude,userLat,userLong);
    }

    /**
     * Constructor for plane based on JSONArray of flight data
     * @param jsonArray - flight data
     */
    public Flight(JSONArray jsonArray){
        this.jsonArray = jsonArray;

        //set flight properties
        try {
            this.icao = jsonArray.getString(0);
            this.callsign = jsonArray.getString(1);
            this.originCountry = jsonArray.getString(2);
            this.timePosition = jsonArray.getInt(3);
            this.lastContact = jsonArray.getInt(4);
            this.longitude = (float) jsonArray.getDouble(5);
            this.latitude = (float) jsonArray.getDouble(6);
            this.degrees = (float) jsonArray.getDouble(10);
            this.altitude = (float) jsonArray.getDouble(13);
        } catch(Exception e){
            //some null errors might occur here
        }
    }

    /**
     * Method to return the flight's latitude
     * @return double - latitude
     */
    public double getLatitude(){
        return this.latitude;
    }

    /**
     * Method to return the flight's longitude
     * @return double - longitude
     */
    public double getLongitude(){
        return this.longitude;
    }

    /**
     * Method to return the latitude and longitude of the flight as an LatLng object
     * @return LatLng - flight's current position
     */
    public LatLng getLatLng(){
        return new LatLng(this.getLatitude(),this.getLongitude());
    }

    /**
     * Method to return the call sign of the flight
     * @return String - call sign
     */
    public String getCallsign(){
        return this.callsign;
    }

    /**
     * Method to return the degrees from north of the flight
     * @return double - degrees from north
     */
    public double getDegrees(){
        return this.degrees;
    }

    /**
     * Method to return the origin country of the flight
     * @return String - origin country
     */
    public String getOriginCountry(){
        return this.originCountry;
    }

    /**
     * Method to return the altitude of the flight in kilometers
     * @return int - altitude (km)
     */
    public int getAltitude(){
        return (int) this.altitude;
    }

    /**
     * Method to return the ICAO of the flight
     * @return String - ICAO
     */
    public String getIcao(){
        return this.icao;
    }

    /**
     * Method to get the estimated arrival airport of the flight
     * @return String - estimated arrival airport
     */
    public String getEstArrival(){
        return this.estArrival;
    }

    /**
     * Method to get the estimated departure airport of the flight
     * @return String - estimated departure airport
     */
    public String getEstDept(){
        return this.estDept;
    }

    /**
     * Method to set the estimated arrival airport
     * @param estArrival - String
     */
    public void setEstArrival(String estArrival){
        this.estArrival = estArrival;
    }

    /**
     * Method to set the estimated departure airport
     * @param estDept - String
     */
    public void setEstDept(String estDept){
        this.estDept = estDept;
    }

    /**
     * Method to get distance of flight from the user
     * @return double - flight from user
     */
    public double getDistanceFromUser(){
        return this.distance;
    }

    /**
     * Method to return toString of flight object
     * @return String - flight data
     */
    public String toString(){
        String toReturn = "ICAO: " + this.icao + "\nCallSign: " + this.callsign + "\nOrigin Country: "+
                this.originCountry +"\nLongitude: " + this.longitude + "\nLatitude: " + this.latitude;
        return toReturn;
    }

    /**
     * Method to calculate the distance between flight and user
     * @param lat1 - flight latitude
     * @param lon1 - flight longitude
     * @param lat2 - user latitude
     * @param lon2 - user longitude
     * @return double - distance
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of separation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
