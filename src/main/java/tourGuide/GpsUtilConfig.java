package tourGuide;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class GpsUtilConfig {

        @Bean
        public GpsUtil gpsUtil() {
            Locale.setDefault(Locale.US);
            return new GpsUtil();
        }
}
