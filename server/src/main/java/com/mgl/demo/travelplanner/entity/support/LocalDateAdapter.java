package com.mgl.demo.travelplanner.entity.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<Long, LocalDate> {

    @Override
    public LocalDate unmarshal(Long dateTs) throws Exception {
        return Instant.ofEpochMilli(dateTs).atZone(ZoneOffset.UTC).toLocalDate();
    }

    @Override
    public Long marshal(LocalDate localDate) throws Exception {
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

}
