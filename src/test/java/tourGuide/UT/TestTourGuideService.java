package tourGuide.UT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserNearestAttractions;
import tourGuide.model.UserPositions;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tripPricer.Provider;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		Locale.setDefault(Locale.US);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		Locale.setDefault(Locale.US);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<UserNearestAttractions> userNearestAttractions = tourGuideService.getClosestAttractions(visitedLocation,user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, userNearestAttractions.size());
	}

	@Test
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, providers.size());
	}

	@Test
	public void getAllUsersPositions() {
		Locale.setDefault(Locale.US);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);

		List<UserPositions> userPositions = tourGuideService.getAllUsersPositions();

		tourGuideService.tracker.stopTracking();

		assertEquals(2, userPositions.size());
	}

	@Test
	public void getAllUser() {
		Locale.setDefault(Locale.US);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		tourGuideService.tracker.stopTracking();

		assertEquals(2, tourGuideService.getAllUsers().size());
	}

	@Test
	public void getUserPreference() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = tourGuideService.getUser("internalUser1");
		UserPreferences userPreferences = new UserPreferences();
		CurrencyUnit currency = Monetary.getCurrency("USD");
		Money lowerPricePoint = Money.of(1.0, currency);
		Money highPricePoint = Money.of(100.0, currency);
		userPreferences.setAttractionProximity(userPreferences.getAttractionProximity());
		userPreferences.setCurrency(currency);
		userPreferences.setLowerPricePoint(lowerPricePoint);
		userPreferences.setHighPricePoint(highPricePoint);
		userPreferences.setTripDuration(11);
		userPreferences.setTicketQuantity(3);
		userPreferences.setNumberOfAdults(4);
		userPreferences.setNumberOfProposalAttraction(10);

		user.setUserPreferences(userPreferences);


		assertEquals("USD", tourGuideService.getUserPreference("internalUser1").getCurrency());
		assertEquals("1.0", tourGuideService.getUserPreference("internalUser1").getLowerPricePoint().toString());
		assertEquals("100.0", tourGuideService.getUserPreference("internalUser1").getHighPricePoint().toString());
		assertEquals(11, tourGuideService.getUserPreference("internalUser1").getTripDuration());
		assertEquals(3, tourGuideService.getUserPreference("internalUser1").getTicketQuantity());
		assertEquals(4, tourGuideService.getUserPreference("internalUser1").getNumberOfAdults());
		assertEquals(10, tourGuideService.getUserPreference("internalUser1").getNumberOfProposalAttraction());

		tourGuideService.tracker.stopTracking();
	}
}
