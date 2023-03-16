package com.ecom.craftbid.entity;

import com.ecom.craftbid.enums.FeedbackStars;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user_feedback")
public class AppUserFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_feedback_id")
    private long id;

    @OneToOne
    private AppUser receiver;
    @OneToOne
    private AppUser giver;

    private FeedbackStars stars;
}
