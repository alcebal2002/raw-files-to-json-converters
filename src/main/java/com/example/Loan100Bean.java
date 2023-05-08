package com.example;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.validators.*;

import lombok.Data;

/**
 *
 *
 */
@Data
public class Loan100Bean {
    @CsvBindByPosition(position = 0, required = true)
    @PreAssignmentValidator(validator = MustMatchRegexExpression.class, paramString = "D")
    private String recordType;

    @CsvBindByPosition(position = 1, required = true)
    @PreAssignmentValidator(validator = MustMatchRegexExpression.class, paramString = "LOAN100")
    private String dataType;

    @CsvBindByPosition(position = 2, required = true)
    private String field1;

    @CsvBindByPosition(position = 3, required = true)
    private String field2;

    @CsvBindByPosition(position = 4, required = true)
    private String field3;

    @CsvBindByPosition(position = 5, required = true)
    private String field4;

    @CsvBindByPosition(position = 6, required = true)
    private String field5;
}