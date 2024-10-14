package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserDtoUpdateValidTest {

    @Autowired
    private Validator validator;

    @Test
    public void testValidUserDtoUpdate() {
        UpdateUserRqDto user = UpdateUserRqDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        Set<ConstraintViolation<UpdateUserRqDto>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Нет ошибок валидации");
    }


    @Test
    public void testEmailNull() {
        UpdateUserRqDto user = UpdateUserRqDto.builder()
                .name("John Doe")
                .email(null)
                .build();

        Set<ConstraintViolation<UpdateUserRqDto>> violations = validator.validate(user);
        assertEquals(0, violations.size(),
                "Ожидалось отсутствие ошибок валидации, так как email не помечено как @NotNull");
    }

    @Test
    public void testEmailInvalidFormat() {
        UpdateUserRqDto user = UpdateUserRqDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UpdateUserRqDto>> violations = validator.validate(user);
        String actualMessage = violations.iterator().next().getMessage();
        assertEquals(1, violations.size(), "Ошибка валидации для email");
        assertTrue(actualMessage.contains("должно иметь формат адреса электронной почты"),
                "Сообщение об ошибке не соответствует ожиданиям.");
    }
}