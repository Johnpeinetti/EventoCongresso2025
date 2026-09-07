package com.factory.congresso.repository;

import com.factory.congresso.model.AcquisitionChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcquisitionChannelRepository extends JpaRepository<AcquisitionChannel, Long> {
    Optional<AcquisitionChannel> findByName(String name);
}