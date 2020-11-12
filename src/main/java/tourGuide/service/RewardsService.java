package tourGuide.service;


import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.proxy.RewardCentralProxy;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private final int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private final int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentralProxy rewardsCentralProxy;

	//private final Logger logger = LoggerFactory.getLogger(RewardsService.class);
	
	public RewardsService(GpsUtil gpsUtil, RewardCentralProxy rewardCentralProxy) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentralProxy = rewardCentralProxy;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(User user) {
		//
		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>() ;
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();

		attractions.addAll(gpsUtil.getAttractions());
		userLocations.addAll(user.getVisitedLocations());

		userLocations.forEach(visitedLocation -> {
			attractions.forEach(attraction ->
			{
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						//logger.debug("addreward");
						user.addUserReward(new UserReward(visitedLocation, attraction, rewardsCentralProxy.getRewardPointsExterne(attraction, user)));
					}
				}
			});
		});
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return !(getDistance(attraction, location) > attractionProximityRange);
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		Location locationVisitedLocation = new Location(attraction.latitude,attraction.longitude);

		return !(getDistance(locationVisitedLocation, visitedLocation.location) > proximityBuffer);
	}

	public int getRewardPoints(Attraction attraction, User user) {
		//logger.debug("getRewardPoints");
		int rewardPoint = rewardsCentralProxy.getRewardPointsExterne(attraction, user);
		//logger.debug("rewartPoint : "+rewartPoint);
		//return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
		return rewardPoint;
	}

	 /**
	public int getRewardPointsExterne(Attraction attraction, User user) {
		logger.debug("getRewardPointsExterne");
		String rewardCentralURL = "http://localhost:8081/AttractionRewardPoints/?";
		String paramAttractionUUID = "attractionId=" + attraction.attractionId;
		String paramUserUUID = "userId=" + user.getUserId();

		int rewardPoint = 0;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<Integer> result = null;
		try {
			result = restTemplate.getForEntity(rewardCentralURL
					+paramAttractionUUID
					+"&"
					+paramUserUUID
					,Integer.class);
		} catch (RestClientException e) {
			return rewardPoint;
		}

		rewardPoint = result.getBody();

		logger.debug("rewartPoint : "+rewardPoint);
		//return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
		return rewardPoint;
	}
	 **/
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
