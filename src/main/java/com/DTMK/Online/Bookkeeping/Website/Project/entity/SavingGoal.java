package com.DTMK.Online.Bookkeeping.Website.Project.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SavingGoal {
    private Integer id;
    private Integer userId;
    private String name;
    private BigDecimal target;
    private BigDecimal saved;
    private String note;
    private Boolean completed;
    private LocalDateTime createdAt;
}
