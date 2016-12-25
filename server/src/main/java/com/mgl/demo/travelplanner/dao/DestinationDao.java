package com.mgl.demo.travelplanner.dao;

import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QDestination;
import com.mgl.demo.travelplanner.entity.Destination;

public class DestinationDao extends BaseEntityDao<Long, Destination, QDestination> {

    public DestinationDao() {
        super(Destination.class, QDestination.destination);
    }

}
