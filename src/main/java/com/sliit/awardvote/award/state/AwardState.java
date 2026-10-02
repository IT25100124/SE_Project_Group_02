package com.sliit.awardvote.award.state;

import com.sliit.awardvote.award.model.AwardStatus;

import java.util.Set;

/**
 * AwardState - State design pattern.
 *
 * Each stage of an award programme (Draft, Open, Closed, Completed) is its own
 * class that knows which stage it may move to next. This replaces a free
 * choice of any status with a controlled life cycle.
 */
public interface AwardState {

    /** The status this state represents. */
    AwardStatus status();

    /** The statuses this state is allowed to move to. */
    Set<AwardStatus> allowedNext();

    default boolean canChangeTo(AwardStatus target) {
        return target == status() || allowedNext().contains(target);
    }
}
