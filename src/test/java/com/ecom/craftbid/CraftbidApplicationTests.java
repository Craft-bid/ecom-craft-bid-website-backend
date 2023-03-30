package com.ecom.craftbid;

import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.enums.FeedbackStar;
import com.ecom.craftbid.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CraftbidApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Test
    void contextLoads() {
        User au = new User();
        userRepository.save(au);

        FeedbackStar feedbackStar = FeedbackStar.FIVE_STARS;
    }

}
