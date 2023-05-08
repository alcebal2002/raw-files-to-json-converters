package com.example;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.validators.*;

import lombok.Data;

/**
 *
 *
 */
@Data
public class HeaderBean {
    @CsvBindByPosition(position = 0, required = true)
    @PreAssignmentValidator(validator = MustMatchRegexExpression.class, paramString = "H")
    private String recordType;

    @CsvBindByPosition(position = 1, required = true)
    private String header1;

    @CsvBindByPosition(position = 2, required = true)
    private String header2;

    @CsvBindByPosition(position = 3, required = true)
    private String header3;
}