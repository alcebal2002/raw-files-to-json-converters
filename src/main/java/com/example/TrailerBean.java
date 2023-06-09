package com.example;

import java.time.LocalDate;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import com.opencsv.bean.validators.*;

import lombok.Data;

/**
 *
 *
 */
@Data
public class TrailerBean {
    @CsvBindByPosition(position = 0, required = true)
    @PreAssignmentValidator(validator = MustMatchRegexExpression.class, paramString = "T")
    private String recordType;

    @CsvBindByPosition(position = 1, required = true)
    private int totalRecords;

    @CsvBindByPosition(position = 2, required = true)
    @CsvNumber("#.#")
    private double controlTotal;

    @CsvBindByPosition(position = 3, required = true)
    @CsvDate("yyyy-MM-dd")
    private LocalDate date;

    @CsvBindByPosition(position = 4, required = true)
    private String comment;
}