package com.mgl.demo.travelplanner.dao.support;

import static com.mgl.demo.travelplanner.entity.support.PersistenceUnits.PG_SERVER_DS_PU_NAME;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    protected JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    public void create(E entity) {
        em().persist(Objects.requireNonNull(entity, entityName + " entity"));
    }

    public boolean existsAccordingTo(Predicate ...predicates) {
        JPAQueryFactory factory = jpaQueryFactory();
        return factory
            .select(Expressions.TRUE)
            .from(pathBase())
            .where(factory.from(pathBase()).where(predicates).exists())
            .fetchOne() != null;
    }

    public E findExisting(Predicate ...predicates) {
        E maybeEntity = (E) jpaQueryFactory().from(pathBase()).where(predicates).fetchOne();
        if (maybeEntity == null) {
            throw new EntityNotFoundException("Expected existing " + entityName + " not found");
        } else {
            return maybeEntity;
        }
    }

}
