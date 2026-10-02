package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

/** Looks up the state object that matches an {@link AwardStatus}. */
public final class AwardStates {

    private AwardStates() {
    }

    public static AwardState of(AwardStatus status) {
        return switch (status) {
            case DRAFT -> new DraftState();
            case OPEN -> new OpenState();
            case CLOSED -> new ClosedState();
            case COMPLETED -> new CompletedState();
        };
    }
}
