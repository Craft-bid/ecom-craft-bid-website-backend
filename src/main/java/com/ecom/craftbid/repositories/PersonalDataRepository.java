package com.ecom.craftbid.repositories;

import com.ecom.craftbid.entities.user.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDataRepository extends JpaRepository<PersonalData, Long> {
}
