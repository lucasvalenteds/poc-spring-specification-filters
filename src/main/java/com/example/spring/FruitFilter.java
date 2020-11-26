package com.example.spring;

import java.util.Arrays;
import java.util.Optional;

public enum FruitFilter {
    NAME_EQUAL_TO("eq"),
    COLOR_ENDS_WITH("ending"),
    COLOR_STARTS_WITH("starting"),
    COLOR_IN("is");

    private final String name;

    private Object value;

    FruitFilter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    private FruitFilter setValue(Object value) {
        this.value = value;
        return this;
    }

    public static Optional<FruitFilter> extractName(String input) {
        String[] pieces = input.split(":");

        if (pieces.length == 2) {
            return Arrays.stream(values())
                .filter(it -> it.name.equals(pieces[0]))
                .findFirst()
                .map(filter -> filter.setValue(pieces[1]));
        }

        return Optional.empty();
    }

    public static Optional<FruitFilter> extractValue(FruitFilter filter) {
        if (filter == COLOR_IN) {
            filter.setValue(Arrays.asList(filter.getValue().toString().split(",")));
        }

        return Optional.of(filter);
    }
}
