package tourGuide.IT;

import gpsUtil.GpsUtil;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.helper.InternalTestHelper;

import tourGuide.proxy.RewardCentralProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TestTourGuideIT {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void getUserLocationTestIT() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(get("/location")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getNearbyAttractions() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(get("/nearbyAttractions")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getTripDeals() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(get("/tripDeals")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getUserPreference() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(get("/userPreference")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void postUserPreferenceForUserNotExist() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(post("/userPreference")
                .param("userName", "internalUserX")
        )
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void postUserPreferenceWithoutUserpreferences() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        this.mockMvc.perform(post("/userPreference")
                .param("userName", "internalUser1")
        )
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void postUserPreference() throws Exception {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentralProxy());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        String questionBody = "{\n" +
                "    \"attractionProximity\": 2147483647,\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"lowerPricePoint\": 0.0,\n" +
                "    \"highPricePoint\": 300.0,\n" +
                "    \"tripDuration\": 1,\n" +
                "    \"ticketQuantity\": 1,\n" +
                "    \"numberOfAdults\": 1,\n" +
                "    \"numberOfChildren\": 0,\n" +
                "    \"numberOfProposalAttraction\": 2\n" +
                "}";

        this.mockMvc.perform(post("/userPreference")
                .param("userName", "internalUser1")
                .content(questionBody).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk());

    }


}