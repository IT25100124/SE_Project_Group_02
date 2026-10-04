package com.sliit.awardvote.sponsor.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.model.SponsorTier;
import com.sliit.awardvote.sponsor.dao.SponsorDao;
import com.sliit.awardvote.sponsor.strategy.SponsorTierStrategy;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class SponsorService extends AbstractCrudService<Sponsor, Long> {

    private final SponsorDao sponsorDao;

    /** Strategy pattern: one display strategy per tier. Spring injects every SponsorTierStrategy bean. */
    private final Map<SponsorTier, SponsorTierStrategy> tierStrategies = new EnumMap<>(SponsorTier.class);

    public SponsorService(SponsorDao sponsorDao, List<SponsorTierStrategy> strategies) {
        this.sponsorDao = sponsorDao;
        for (SponsorTierStrategy strategy : strategies) {
            tierStrategies.put(strategy.tier(), strategy);
        }
    }

    @Override
    protected GenericDao<Sponsor, Long> getDao() {
        return sponsorDao;
    }

    /** Picks the strategy for a tier (falls back to Bronze if the tier is missing). */
    public SponsorTierStrategy strategyFor(SponsorTier tier) {
        SponsorTierStrategy strategy = tier == null ? null : tierStrategies.get(tier);
        return strategy != null ? strategy : tierStrategies.get(SponsorTier.BRONZE);
    }

    /** All sponsors ordered by their tier's display priority, each with its tier's display rules. */
    public List<SponsorDisplay> findAllForDisplay() {
        return findAll().stream()
                .sorted(Comparator
                        .comparingInt((Sponsor s) -> strategyFor(s.getTier()).displayPriority())
                        .thenComparing(s -> s.getName() == null ? "" : s.getName(), String.CASE_INSENSITIVE_ORDER))
                .map(s -> {
                    SponsorTierStrategy strategy = strategyFor(s.getTier());
                    return new SponsorDisplay(s, strategy.iconClass(), strategy.benefits());
                })
                .toList();
    }
}
