package com.factory.congresso.repository;

import com.factory.congresso.model.StakeholderType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StakeholderTypeRepository extends JpaRepository<StakeholderType, Long> {
    Optional<StakeholderType> findByName(String name);
}