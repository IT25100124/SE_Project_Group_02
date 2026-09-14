package com.sliit.awardvote.award;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<AwardProgramme, Long> {
}
