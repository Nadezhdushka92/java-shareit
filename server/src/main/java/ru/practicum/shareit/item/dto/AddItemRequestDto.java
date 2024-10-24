
package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddItemRequestDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
