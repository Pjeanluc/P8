package tourGuide.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.exceptions.UserNameNotFoundException;
import tourGuide.exceptions.UserPreferenceEmptyException;
import tourGuide.model.UserNearestAttractions;
import tourGuide.model.UserPositions;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

    @Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * @param userName
     * @return location of the userName
     */
    @RequestMapping("/location")
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }


    /**
     * @param userName
     * @return list of the nearby attraction for the username
     */
    @RequestMapping("/nearbyAttractions")
    public List<UserNearestAttractions> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getClosestAttractions(visitedLocation,getUser(userName));
    }

    /**
     * @param userName
     * @return rewards for the username
     */
    @RequestMapping("/rewards")
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    /**
     * @return return the list of all position of all users
     */
    @RequestMapping("/allCurrentLocations")
    public List<UserPositions> getAllCurrentLocations() {

        return tourGuideService.getAllUsersPositions();
    }

    /**
     * @param userName
     * @return proposition of trip
     */
    @RequestMapping("/tripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    public User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

    /**
     * @param userName
     * @return list of the user's preference
     * @throws UserNameNotFoundException
     */
    @RequestMapping("/userPreference")
    public UserPreferencesDTO getUserPreference(@RequestParam String userName) throws UserNameNotFoundException {

        if (tourGuideService.getUser(userName) == null ) {
            String message = " this username does not exist : "+ userName;
            logger.error(message);
            throw new UserNameNotFoundException(message);
        }
        return tourGuideService.getUserPreference(userName);

    }

    /**
     * @param userName
     * @param userPreference
     * @return list of the user's preferences updated
     * @throws UserNameNotFoundException
     * @throws UserPreferenceEmptyException
     */
    @PostMapping("/userPreference")
    public UserPreferences createUserPreference(@RequestParam String userName, @RequestBody UserPreferencesDTO userPreference) throws UserNameNotFoundException, UserPreferenceEmptyException {
        if (tourGuideService.getUser(userName) == null ) {
            String message = " this username does not exist : "+ userName;
            logger.error(message);
            throw new UserNameNotFoundException(message);
        }
        if (userPreference == null ) {
            String message = " userPreference is empty  ";
            logger.error(message);
            throw new UserPreferenceEmptyException(message);
        }

        return tourGuideService.setUserPreference(userName,userPreference);
    }
   

}