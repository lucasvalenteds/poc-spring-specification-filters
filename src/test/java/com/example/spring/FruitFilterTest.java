package com.example.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FruitFilterTest {

    static Stream<Arguments> sourceExtractingNameAndValue() {
        return Stream.of(
            Arguments.of("eq:pineapple", "eq", "pineapple"),
            Arguments.of("ending:apple", "ending", "apple"),
            Arguments.of("is:pineapple", "is", List.of("pineapple")),
            Arguments.of("is:pineapple,apple", "is", List.of("pineapple", "apple"))
        );
    }

    @ParameterizedTest
    @MethodSource("sourceExtractingNameAndValue")
    void testExtractingNameAndValue(String input, String name, Object value) {
        FruitFilter filter = FruitFilter.extractName(input)
            .flatMap(FruitFilter::extractValue)
            .orElseThrow();

        assertEquals(name, filter.getName());
        assertEquals(value, filter.getValue());
    }

    static Stream<Arguments> sourceFailingToExtractValueWhenItIsNotPresent() {
        return Stream.of(
            Arguments.of("eq:"),
            Arguments.of("ending:"),
            Arguments.of("is:")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceFailingToExtractValueWhenItIsNotPresent")
    void testFailingToExtractValueWhenItIsNotPresent(String input) {
        Optional<FruitFilter> filter = FruitFilter.extractName(input)
            .flatMap(FruitFilter::extractValue);

        assertTrue(filter.isEmpty());
    }

    @Test
    void testFailsToExtractInputThatIsNotAndName() {
        String inputWithoutName = "pineapple";

        Optional<FruitFilter> result = FruitFilter.extractName(inputWithoutName)
            .flatMap(FruitFilter::extractValue);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFailsToExtractUnknownName() {
        String inputWithUnknownName = "unknown-filter:pineapple";

        Optional<FruitFilter> result = FruitFilter.extractName(inputWithUnknownName)
            .flatMap(FruitFilter::extractValue);

        assertTrue(result.isEmpty());
    }
}
