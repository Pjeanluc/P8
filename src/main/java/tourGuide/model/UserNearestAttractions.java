package tourGuide.model;

import gpsUtil.location.Location;

public class UserNearestAttractions {
    // Name of Tourist attraction,
    private String attractionName;

    // Tourist attractions lat/long,
    private Double latitudeAttraction;

    private Double longitudeAttraction;

    // The user's location lat/long,
    private Location userLocation;

    // The distance in miles between the user's location and each of the attractions.
    private double attractionProximity;

    // The reward points for visiting each Attraction.
    private int attractionRewardPoint;

    public UserNearestAttractions(){

    }

    public UserNearestAttractions(String attractionName, double latitude, double longitude, Location location, double distance, int i) {
        this.attractionName=attractionName;
        this.latitudeAttraction=latitude;
        this.longitudeAttraction=longitude;
        this.userLocation=location;
        this.attractionProximity=distance;
        this.attractionRewardPoint = i;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }


    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public double getAttractionProximity() {
        return attractionProximity;
    }

    public void setAttractionProximity(double attractionProximity) {
        this.attractionProximity = attractionProximity;
    }

    public int getAttractionRewardPoint() {
        return attractionRewardPoint;
    }

    public void setAttractionRewardPoint(int attractionRewardPoint) {
        this.attractionRewardPoint = attractionRewardPoint;
    }

    public void setLongitude(double longitude) {
        this.longitudeAttraction = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitudeAttraction = latitude;
    }

    public Double getLatitudeAttraction() {
        return latitudeAttraction;
    }

    public Double getLongitudeAttraction() {
        return longitudeAttraction;
    }
}
