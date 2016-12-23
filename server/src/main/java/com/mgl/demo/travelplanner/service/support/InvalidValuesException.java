package com.mgl.demo.travelplanner.service.support;

import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

public class InvalidValuesException extends ServiceException {

    private static final long serialVersionUID = 1L;

    @Getter private final Map<String, String> invalidAttributes;

    public InvalidValuesException(Map<String, String> invalidAttributes) {
        super(Status.BAD_REQUEST);
        this.invalidAttributes = Objects.requireNonNull(invalidAttributes, "invalidAttributes");
    }

    public InvalidValuesException(String invalidAttributeKey, String invalidAttributeValue) {
        this(ImmutableMap.of(invalidAttributeKey, invalidAttributeValue));
    }

}
