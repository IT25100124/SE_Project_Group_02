package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

import java.util.Set;

/** Closed: voting has ended, so it can be re-opened or completed. */
public class ClosedState implements AwardState {
    @Override public AwardStatus status() { return AwardStatus.CLOSED; }
    @Override public Set<AwardStatus> allowedNext() { return Set.of(AwardStatus.OPEN, AwardStatus.COMPLETED); }
}
