package com.mgl.demo.travelplanner.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.mgl.demo.travelplanner.dao.DestinationDao;
import com.mgl.demo.travelplanner.entity.Destination;

@Stateless
public class DestinationService {

    @Inject private DestinationDao destinationDao;

    public Destination ensureDestinationExists(String destinationName) {
        return destinationDao.maybeFindByName(destinationName)
                .orElseGet(() -> {
                    Destination destination = new Destination(destinationName);
                    destinationDao.create(destination);
                    return destination;
                });
    }

}
