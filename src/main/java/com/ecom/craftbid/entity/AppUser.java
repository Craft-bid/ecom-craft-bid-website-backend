package com.ecom.craftbid.entity;

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
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id")
    private long id;

    private String username;
    private String password;
    private String email;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private List<PersonalData> personalData = new ArrayList<>();
}
