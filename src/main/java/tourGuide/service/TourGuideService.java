package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserNearestAttractions;
import tourGuide.model.UserPositions;
import tourGuide.model.UserPreferencesDTO;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);

		List<Provider> providersResult = new ArrayList<>();
		for (Provider provider : providers) {
			if ((provider.price <= user.getUserPreferences().getHighPricePoint().getNumber().doubleValue())
					&& (provider.price >= user.getUserPreferences().getLowerPricePoint().getNumber().doubleValue())) {
				providersResult.add(provider);
			}
		}
		return providersResult;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public void trackListOfUserLocation(List<User> users) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(2000);

		for (User user : users){
			Runnable runnable = () -> {
				trackUserLocation(user);
			};

			executorService.execute(runnable);
		}

		executorService.shutdown();
		executorService.awaitTermination(25, TimeUnit.MINUTES);

		return;
	}

	public List<UserNearestAttractions> getClosestAttractions(VisitedLocation visitedLocation, User user) {

		List<Attraction> attractions = gpsUtil.getAttractions();
		List<UserNearestAttractions> allAttractions = attractions.parallelStream()
				.map(a -> new UserNearestAttractions(a.attractionName, a.latitude, a.longitude, visitedLocation.location,
						rewardsService.getDistance(a, visitedLocation.location),
						rewardsService.getRewardPoints(a, user)))
				.sorted(Comparator.comparingDouble(UserNearestAttractions::getAttractionProximity))
				.collect(Collectors.toList());

		List<UserNearestAttractions> closestAttractions = allAttractions.stream().
				limit(user.getUserPreferences().getNumberOfProposalAttraction()).collect(Collectors.toList());

		return closestAttractions;
	}

	public List<UserPositions> getAllUsersPositions () {

		List<UserPositions> userPosition = internalUserMap.values().stream()
				.map(u -> new UserPositions(u.getUserId(),
						u.getVisitedLocations().stream().
								map(l -> new Location(l.location.latitude,l.location.longitude)).collect(Collectors.toList())))
				.collect(Collectors.toList());

		return userPosition;
	}

	public UserPreferencesDTO getUserPreference(String userName){

		User user = getUser(userName);

		UserPreferencesDTO userPreferenceDTO = new UserPreferencesDTO(
				user.getUserPreferences().getAttractionProximity(),
				user.getUserPreferences().getCurrency().getCurrencyCode(),
				user.getUserPreferences().getLowerPricePoint().getNumber().doubleValue(),
				user.getUserPreferences().getHighPricePoint().getNumber().doubleValue(),
				user.getUserPreferences().getTripDuration(),
				user.getUserPreferences().getTicketQuantity(),
				user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getNumberOfProposalAttraction()
				);
		return userPreferenceDTO;
	}

	public UserPreferences setUserPreference(String userName, UserPreferencesDTO userPreferences){
		UserPreferences userPreferencesResult = new UserPreferences();
		User user = getUser(userName);
		CurrencyUnit currency = Monetary.getCurrency(userPreferences.getCurrency());
		Money lowerPricePoint = Money.of(userPreferences.getLowerPricePoint(), currency);
		Money highPricePoint = Money.of(userPreferences.getHighPricePoint(), currency);

		userPreferencesResult.setAttractionProximity(userPreferences.getAttractionProximity());
		userPreferencesResult.setCurrency(currency);
		userPreferencesResult.setLowerPricePoint(lowerPricePoint);
		userPreferencesResult.setHighPricePoint(highPricePoint);
		userPreferencesResult.setTripDuration(userPreferences.getTripDuration());
		userPreferencesResult.setTicketQuantity(userPreferences.getTicketQuantity());
		userPreferencesResult.setNumberOfAdults(userPreferences.getNumberOfAdults());
		userPreferencesResult.setNumberOfProposalAttraction(userPreferences.getNumberOfProposalAttraction());

		user.setUserPreferences(userPreferencesResult);

		return userPreferencesResult;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
