package com.ecom.craftbid;

import com.ecom.craftbid.entity.AppUser;
import com.ecom.craftbid.enums.FeedbackStars;
import com.ecom.craftbid.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CraftbidApplicationTests {

    @Autowired
    AppUserRepository appUserRepository;

    @Test
    void contextLoads() {
        AppUser appUser = new AppUser();
        appUserRepository.save(appUser);

        FeedbackStars feedbackStars = FeedbackStars.FIVE_STARS;
    }

}
