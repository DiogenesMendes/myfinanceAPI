package com.diogeMendes.personalFinance.api.dto;

import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntriesDTO {


    private Long id;

    @NotEmpty
    private String description;

    @NotNull
    private Integer mount;

    @NotNull
    private Integer year;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal value;


    private LocalDate localDate;

    @NotEmpty
    private String entriesType;

    @NotEmpty
    private String entriesStatus;
}
