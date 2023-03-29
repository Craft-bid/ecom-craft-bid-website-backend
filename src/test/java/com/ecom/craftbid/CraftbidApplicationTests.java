package com.ecom.craftbid;

import com.ecom.craftbid.entities.appuser.User;
import com.ecom.craftbid.enums.FeedbackStar;
import com.ecom.craftbid.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CraftbidApplicationTests {

    @Autowired
    AppUserRepository appUserRepository;

    @Test
    void contextLoads() {
        User au = new User();
        appUserRepository.save(au);

        FeedbackStar feedbackStar = FeedbackStar.FIVE_STARS;
    }

}
