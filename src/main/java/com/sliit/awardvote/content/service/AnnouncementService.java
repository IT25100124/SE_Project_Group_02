package com.sliit.awardvote.content.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.dao.AnnouncementDao;

import org.springframework.stereotype.Service;

@Service
public class AnnouncementService extends AbstractCrudService<Announcement, Long> {

    private final AnnouncementDao announcementDao;

    public AnnouncementService(AnnouncementDao announcementDao) {
        this.announcementDao = announcementDao;
    }

    @Override
    protected GenericDao<Announcement, Long> getDao() {
        return announcementDao;
    }
}
