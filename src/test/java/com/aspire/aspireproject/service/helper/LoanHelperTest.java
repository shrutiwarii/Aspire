package com.aspire.aspireproject.service.helper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanHelperTest {

    private LoanHelper loanHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanHelper = new LoanHelper();
    }

}
