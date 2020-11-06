package tourGuide.UT;

import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.DTO.UserPreferencesDTO;
import tourGuide.proxy.RewardCentralProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import java.util.Date;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestTourGuideController {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected TourGuideService tourGuideService;

    @BeforeEach
    void init() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
        tourGuideService.tracker.stopTracking();
    }

    @Test
    public void getIndexControllerTest() throws Exception {


        mockMvc.perform(get("/")
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getUserLocationTest() throws Exception {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Location locationMock = new Location(1.0d, 1.0d);
        Date date = new Date();
        date.setTime( System.currentTimeMillis());
        VisitedLocation visitedLocationMock = new VisitedLocation(user.getUserId(),locationMock,date);

        Mockito.when( tourGuideService.getUserLocation(tourGuideService.getUser(anyString()))).thenReturn(visitedLocationMock);
        this.mockMvc.perform(get("/location")
                    .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getUserPreferenceWithUSerNotExistTest() throws Exception {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();

        userPreferencesDTO.setAttractionProximity(1);
        userPreferencesDTO.setCurrency("USD");
        userPreferencesDTO.setLowerPricePoint(2.0);
        userPreferencesDTO.setHighPricePoint(3.0);
        userPreferencesDTO.setTripDuration(4);
        userPreferencesDTO.setTicketQuantity(5);
        userPreferencesDTO.setNumberOfAdults(6);
        userPreferencesDTO.setNumberOfProposalAttraction(7);


        Mockito.when( tourGuideService.getUserPreference(anyString())).thenReturn(userPreferencesDTO);
        this.mockMvc.perform(get("/userPreference")
                .param("userName", user.getUserName())
        )
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void getUserPreferenceWithUSerExistTest() throws Exception {

        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();

        userPreferencesDTO.setAttractionProximity(1);
        userPreferencesDTO.setCurrency("USD");
        userPreferencesDTO.setLowerPricePoint(2.0);
        userPreferencesDTO.setHighPricePoint(3.0);
        userPreferencesDTO.setTripDuration(4);
        userPreferencesDTO.setTicketQuantity(5);
        userPreferencesDTO.setNumberOfAdults(6);
        userPreferencesDTO.setNumberOfProposalAttraction(7);

        Mockito.when(tourGuideService.getUserPreference("internalUser1")).thenReturn(userPreferencesDTO);

        this.mockMvc.perform(get("/userPreference")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
