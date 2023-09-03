package com.aspire.aspireproject.dao.request;

import lombok.*;

import java.text.SimpleDateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentRequest {
    private String id;
    private int termNo;
    private double amount;
    private String source;
    private String description;
    private String currentDate;
}
