package tourGuide.model;

import gpsUtil.location.Location;

import java.util.List;
import java.util.UUID;

public class UserPositions {
    private UUID userId;
    private List<Location> userLocations;

    public UserPositions() {
    }

    public UserPositions(UUID userId, List<Location> visitedLocations) {
        this.userId = userId;
        this.userLocations = visitedLocations;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<Location> getUserLocations() {
        return userLocations;
    }

    public void setUserLocation(List<Location> userLocations) {
        this.userLocations = userLocations;
    }
}
