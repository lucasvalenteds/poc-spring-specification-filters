package com.example.spring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = TestConfiguration.class,
    loader = AnnotationConfigContextLoader.class
)
class FruitSpecificationTest {

    @Autowired
    private FruitRepository repository;

    private final FruitSpecification specification = new FruitSpecification();

    @BeforeEach
    public void beforeEach() {
        assertNotNull(repository);

        repository.save(new Fruit("Pineapple", "Yellow"));
        repository.save(new Fruit("Apple", "Red"));
        repository.save(new Fruit("Apple", "Green"));
        repository.save(new Fruit("Banana", "Yellow"));
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }

    @Test
    void testFilteringByNameEqualTo() {
        String userInput = "eq:Apple";

        List<Fruit> fruits = repository.findAll(
            FruitFilter.extractName(userInput)
                .flatMap(FruitFilter::extractValue)
                .map(filter -> specification.<Fruit>create("name", filter))
                .orElseThrow()
        );

        assertEquals(2, fruits.size());
        assertEquals("Red", fruits.get(0).getColor());
        assertEquals("Green", fruits.get(1).getColor());
    }

    @Test
    void testFilteringByColorEndingWith() {
        String userInput = "ending:en";

        Optional<Fruit> fruit = repository.findOne(
            FruitFilter.extractName(userInput)
                .flatMap(FruitFilter::extractValue)
                .map(filter -> specification.<Fruit>create("color", filter))
                .orElseThrow()
        );

        assertTrue(fruit.isPresent());
        assertEquals("Apple", fruit.get().getName());
    }

    @Test
    void testFilteringByColors() {
        String userInput = "is:Red,Yellow,Orange";

        List<Fruit> fruits = repository.findAll(
            FruitFilter.extractName(userInput)
                .flatMap(FruitFilter::extractValue)
                .map(filter -> specification.<Fruit>create("color", filter))
                .orElseThrow()
        );

        List<String> names = fruits.stream()
            .map(Fruit::getName)
            .collect(Collectors.toList());

        assertEquals(3, fruits.size());
        assertTrue(names.contains("Apple"));
        assertTrue(names.contains("Pineapple"));
        assertTrue(names.contains("Banana"));
    }

    @Test
    void testInvalidFilterThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> specification.create("name", FruitFilter.COLOR_STARTS_WITH),
            "Invalid filter informed: contains"
        );
    }

}
