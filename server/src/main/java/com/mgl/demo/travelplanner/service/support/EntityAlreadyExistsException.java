package com.mgl.demo.travelplanner.service.support;

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

public class EntityAlreadyExistsException extends ServiceException {

    private static final long serialVersionUID = 1L;

    @Getter private final Map<String, String> attributes;

    public EntityAlreadyExistsException(Map<String, String> attributes) {
        super(Status.CONFLICT);
        this.attributes = ImmutableMap.copyOf(attributes);
    }

    public EntityAlreadyExistsException(String attributeKey, String attributeValue) {
        this(ImmutableMap.of(attributeKey, attributeValue));
    }

}
