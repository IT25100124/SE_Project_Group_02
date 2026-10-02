package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

import java.util.Set;

/** Completed: the awards season is finished, so no further change is allowed. */
public class CompletedState implements AwardState {
    @Override public AwardStatus status() { return AwardStatus.COMPLETED; }
    @Override public Set<AwardStatus> allowedNext() { return Set.of(); }
}
