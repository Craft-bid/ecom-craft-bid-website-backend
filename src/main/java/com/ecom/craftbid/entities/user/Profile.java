package com.ecom.craftbid.entities.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private long id;

    @Column(length = 2500)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PersonalData> personalData = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Feedback> receivedFeedback = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Feedback> givenFeedback = new ArrayList<>();

    @Column(name = "avatar_uri")
    private String avatarUri;

    private Double averageRating;

    private String image;

    public void addPersonalData(PersonalData data) {
        this.personalData.add(data);
        data.setProfile(this);
    }

    public void removePersonalData(PersonalData data) {
        this.personalData.remove(data);
        data.setProfile(null);
    }

    public void addReceivedFeedback(Feedback data) {
        this.receivedFeedback.add(data);
        data.setReceiver(this);
    }
    public void removeReceivedFeedback(Feedback data){
        this.receivedFeedback.remove(data);
        data.setReceiver(null);
    }

    public void addGivenFeedback(Feedback data) {
        this.givenFeedback.add(data);
        data.setAuthor(this);
    }
    public void removeGivenFeedback(Feedback data){
        this.givenFeedback.remove(data);
        data.setAuthor(null);
    }
}
