package kr.co.bigsnow.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfileInfo {

	@Bean
	@Qualifier("GetProfile")
	@Profile({ "local" })
    public String GetLocalProfile() {
        return "local";
    }

	@Bean
	@Qualifier("GetProfile")
	@Profile({ "dev" })
    public String GetDevProfile() {
        return "dev";
    }

	@Bean
	@Qualifier("GetProfile")
	@Profile({ "stg" })
	public String GetStgProfile() {
		return "stg";
	}

	@Bean
	@Qualifier("GetProfile")
	@Profile({ "prd" })
	public String GetProfile() {
		return "prd";
	}

}
