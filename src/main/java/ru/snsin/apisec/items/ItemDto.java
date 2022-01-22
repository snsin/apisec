package ru.snsin.apisec.items;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ItemDto {

    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private BigDecimal price;
    private String owner;
}
