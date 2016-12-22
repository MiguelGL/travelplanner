package com.mgl.demo.travelplanner.dao.support;

import static com.mgl.demo.travelplanner.entity.support.PersistenceUnits.PG_SERVER_DS_PU_NAME;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class BaseEntityDao<
        E extends BaseEntity,
        P extends EntityPathBase<E>,
        D extends BaseEntityDao> {

    @PersistenceContext(unitName = PG_SERVER_DS_PU_NAME)
    @Getter(AccessLevel.PROTECTED)
    private EntityManager em;

    @Getter(AccessLevel.PROTECTED)
    private final Class<E> clazz;

    @Getter(AccessLevel.PROTECTED)
    private final P pathBase;

    @Getter(AccessLevel.PROTECTED)
    private final String entityName;

    protected BaseEntityDao(Class<E> clazz, P pathBase) {
        this.clazz = Objects.requireNonNull(clazz, "clazz");
        this.pathBase = Objects.requireNonNull(pathBase, "pathBase");
        this.entityName = this.clazz.getSimpleName();
    }

}
