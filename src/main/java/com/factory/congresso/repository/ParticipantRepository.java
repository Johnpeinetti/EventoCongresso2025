package com.factory.congresso.repository;

import com.factory.congresso.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {
}