package tourGuide;

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
import tourGuide.model.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private Logger logger = LoggerFactory.getLogger(TourGuideController.class);

    @Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    

    @RequestMapping("/getNearbyAttractions")
    public List<UserNearestAttractions> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getClosestAttractions(visitedLocation,getUser(userName));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public List<UserPositions> getAllCurrentLocations() {

        return tourGuideService.getAllUsersPositions();
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

    @RequestMapping("/userPreference")
    public UserPreferencesDTO getUserPreference(@RequestParam String userName) throws UserNameNotFoundException {

        if (tourGuideService.getUser(userName) == null ) {
            String message = " this username does not exist : "+ userName;
            logger.error(message);
            throw new UserNameNotFoundException(message);
        }
        return tourGuideService.getUserPreference(userName);

    }

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