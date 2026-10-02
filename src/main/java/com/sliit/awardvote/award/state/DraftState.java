package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

import java.util.Set;

/** Draft: the programme is still being set up, so it can only be opened. */
public class DraftState implements AwardState {
    @Override public AwardStatus status() { return AwardStatus.DRAFT; }
    @Override public Set<AwardStatus> allowedNext() { return Set.of(AwardStatus.OPEN); }
}
