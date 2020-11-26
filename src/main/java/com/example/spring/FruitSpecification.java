package com.example.spring;

import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class FruitSpecification {

    private final Map<FruitFilter, Function<Object, Function<String, Specification<?>>>> filters = Map.ofEntries(
        Map.entry(FruitFilter.NAME_EQUAL_TO, FruitSpecification.EQUAL_TO),
        Map.entry(FruitFilter.COLOR_ENDS_WITH, FruitSpecification.ENDS_WITH),
        Map.entry(FruitFilter.COLOR_IN, FruitSpecification.HAS)
    );

    public <T> Specification<T> create(String field, FruitFilter filter) {
        Specification<?> specification = Optional.ofNullable(filters.get(filter))
            .map(it -> it.apply(filter.getValue()))
            .map(it -> it.apply(field))
            .orElseThrow(() -> new IllegalArgumentException("Invalid filter informed: " + filter));

        return (Specification<T>) specification;
    }

    private static final Function<Object, Function<String, Specification<?>>> EQUAL_TO =
        value -> field -> (root, quer, builder) ->
            builder.equal(root.get(field), value);

    private static final Function<Object, Function<String, Specification<?>>> ENDS_WITH =
        value -> field -> (root, quer, builder) ->
            builder.like(root.get(field), "%" + value);

    private static final Function<Object, Function<String, Specification<?>>> HAS =
        value -> field -> (root, quer, builder) ->
            root.get(field).in(value);
}
