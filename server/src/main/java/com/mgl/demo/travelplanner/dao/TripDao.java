package com.mgl.demo.travelplanner.dao;

import com.mgl.demo.travelplanner.dao.support.BaseEntityDao;
import com.mgl.demo.travelplanner.entity.QTrip;
import com.mgl.demo.travelplanner.entity.Trip;

public class TripDao extends BaseEntityDao<Trip, QTrip, TripDao> {

    public TripDao() {
        super(Trip.class, QTrip.trip);
    }

}
