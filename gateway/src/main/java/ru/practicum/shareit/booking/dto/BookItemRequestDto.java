package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "Необходимо указать id бронируемой вещи")
	private long itemId;
	@NotNull(message = "Необходимо задать начало периода бронирования")
	@FutureOrPresent(message = "Начало периода бронирования должно быть в будущем")
	private LocalDateTime start;
	@NotNull(message = "Необходимо задать конец периода бронирования")
	@Future(message = "Конец периода бронирования должен быть в будущем")
	private LocalDateTime end;
}