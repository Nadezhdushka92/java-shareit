package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemDtoValidTest {

    @Autowired
    private Validator validator;

    @Test
    public void testValidItemDto() {
        ItemDto item = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        assertEquals(0, violations.size(), "Нет ошибок валидации");
    }

    @Test
    public void testNameNotBlank() {
        ItemDto item = ItemDto.builder()
                .name("")
                .description("Item Description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(1, violations.size(), "Ошибка валидации для имени");
        assertTrue(actualMessage.contains("не должно быть пустым"),
                "Сообщение об ошибке не соответствует ожиданиям.");
    }

    @Test
    public void testDescriptionNotBlank() {
        ItemDto item = ItemDto.builder()
                .name("Item Name")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(1, violations.size(), "Ошибка валидации для описания");
        assertTrue(actualMessage.contains("не должно быть пустым"),
                "Сообщение об ошибке не соответствует ожиданиям.");
    }

    @Test
    public void testAvailableNotNull() {
        ItemDto item = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(1, violations.size(), "Ошибка валидации для поля available");
        assertTrue(actualMessage.contains("не должно равняться null"),
                "Сообщение об ошибке не соответствует ожиданиям.");

    }
}