package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

import java.util.Set;

/** Open: nominations and voting are running, so it can only be closed. */
public class OpenState implements AwardState {
    @Override public AwardStatus status() { return AwardStatus.OPEN; }
    @Override public Set<AwardStatus> allowedNext() { return Set.of(AwardStatus.CLOSED); }
}
