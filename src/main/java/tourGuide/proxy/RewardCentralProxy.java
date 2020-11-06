package tourGuide.proxy;

import gpsUtil.location.Attraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.User;
import tourGuide.service.RewardsService;

import java.util.Arrays;


public class RewardCentralProxy {

    private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

    public int getRewardPointsExterne(Attraction attraction, User user) {
        //logger.debug("getRewardPointsExterne");
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

        //logger.debug("rewartPoint : "+rewardPoint);

        return rewardPoint;
    }

}
