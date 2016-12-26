package com.mgl.demo.travelplanner.rest.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pagination {

    public static enum OrderBySpec {
        ASC, DESC, NONE;

        public static final String DEFAULT = "NONE";

        public boolean isOrdered() {
            return this != NONE;
        }
    }

    public static final long MAX_PAGINATED_RESULTS = 100;

    public static long boundLimit(long limit) {
        return Math.min(MAX_PAGINATED_RESULTS, limit);
    }

}
