package com.aspire.aspireproject.dao.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class MessageResponse {

    private String message;
    private HttpStatus status;
}
