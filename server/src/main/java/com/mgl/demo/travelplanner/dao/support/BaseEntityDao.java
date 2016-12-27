package com.mgl.demo.travelplanner.dao.support;

import static com.mgl.demo.travelplanner.entity.support.PersistenceUnits.PG_SERVER_DS_PU_NAME;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import com.mgl.demo.travelplanner.entity.support.BaseEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class BaseEntityDao<
        I extends Object,
        E extends BaseEntity<I>,
        P extends EntityPathBase<E>> {

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
        this.clazz = requireNonNull(clazz, "clazz");
        this.pathBase = requireNonNull(pathBase, "pathBase");
        this.entityName = this.clazz.getSimpleName();
    }

    protected JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    public void create(E entity) {
        em().persist(requireNonNull(entity, entityName + " entity"));
    }

    public E update(E entity) {
        return em().merge(requireNonNull(entity, entityName + " entity"));
    }

    public void delete(E entity) {
        em().remove(requireNonNull(entity, entityName + " entity"));
    }

    public boolean existsAccordingTo(Predicate ...predicates) {
        JPAQueryFactory factory = jpaQueryFactory();
        return factory
            .select(Expressions.TRUE)
            .from(pathBase())
            .where(factory.from(pathBase()).where(predicates).exists())
            .fetchOne() != null;
    }

    public E findById(I id) {
        E entity = em().find(clazz, id);
        if (entity == null) {
            throw new EntityNotFoundException("Expected existing " + entityName
                    + "with id '" + id + "' not found");
        } else {
            return entity;
        }
    }

    public Optional<E> maybeFind(Predicate ...predicates) {
        E maybeEntity = jpaQueryFactory().selectFrom(pathBase()).where(predicates).fetchOne();
        return Optional.ofNullable(maybeEntity);
    }

    public E findExisting(Predicate ...predicates) {
        E maybeEntity = jpaQueryFactory().selectFrom(pathBase()).where(predicates).fetchOne();
        if (maybeEntity == null) {
            throw new EntityNotFoundException("Expected existing " + entityName + " not found");
        } else {
            return maybeEntity;
        }
    }

    public long count(Predicate ...predicates) {
        JPAQuery<E> query = jpaQueryFactory().selectFrom(pathBase()).where(predicates);
        return query.fetchCount();
    }

    public List<E> find(
            Optional<Long> maybeOffset,
            Optional<Long> maybeLimit,
            Optional<OrderSpecifier<?>> maybeOrder,
            Predicate ...predicates) {
        JPAQuery<E> query = jpaQueryFactory().selectFrom(pathBase()).where(predicates);
        maybeOffset.ifPresent(offset -> query.offset(offset));
        maybeLimit.ifPresent(limit -> query.limit(limit));
        maybeOrder.ifPresent(order -> query.orderBy(order));
        return query.fetch();
    }

}
