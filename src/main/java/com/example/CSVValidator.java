package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

/**
 *
 *
 */
public class CSVValidator {

    static final Logger logger = LoggerFactory.getLogger(CSVValidator.class);
    static String csvFileName = "csv_sample.csv";

    public static void main(String[] args) throws IllegalStateException, IOException, CsvException {

        String delimiter = "\\|";
        String config = "{" +
                "    \"rowTypePosition\": 0," +
                "    \"dataTypePosition\": 1," +
                "    \"rowTypes\": {" +
                "       \"H\": \"HeaderBean\"," +
                "       \"T\": \"TrailerBean\"," +
                "       \"D\": {" +
                "           \"LOAN100\": \"Loan100Bean\"," +
                "           \"LOAN108\": \"Loan108Bean\"," +
                "           \"LOAN900\": \"Loan900Bean\"" +
                "       }" +
                "    }" +
                "}";

        JSONObject jsonConfig = new JSONObject(config);

        logger.info("config: " + jsonConfig.toString(2));

        try (CSVReader reader = new CSVReader(
                new FileReader(new File(CSVValidator.class.getClassLoader().getResource(csvFileName).getPath())))) {
            List<String[]> fullCsv = reader.readAll();
            fullCsv.forEach(csvLine -> {
                System.out.println("Reading line: " + csvLine[0]);
                String rowType = csvLine[0].split(delimiter)[0];
                String dataType = csvLine[0].split(delimiter)[1];
                String beanClass = rowType.equals("D")
                        ? (String) jsonConfig.getJSONObject("rowTypes").getJSONObject(rowType).get(dataType)
                        : (String) jsonConfig.getJSONObject("rowTypes").get(rowType);
                System.out.println("Row Type: " + rowType + " -> Bean: " + beanClass);
            });
        }

        ArrayList<String> arrayStrings = new ArrayList<String>();
        arrayStrings.add("H|header-data0|header-data1|header-data2");
        arrayStrings.add("D|type0|data0-1|data0-2|data0-3|data0-4|data0-5");
        arrayStrings.add("D|type1|data1-1|data1-2");
        arrayStrings.add("D|type2|data2-1|data2-2|data2-3|data2-4|data2-5|data2-6");
        arrayStrings.add("T|10100||2023-05-04|trailer-data3");

        StringBuilder stringBuilder = new StringBuilder();
        arrayStrings.forEach(line -> stringBuilder.append(line).append(System.lineSeparator()));

        Reader reader = new StringReader(stringBuilder.toString());

        final CsvToBean<TrailerBean> beans = new CsvToBeanBuilder(reader)
                .withSeparator('|') // or any other separator you are using
                .withType(TrailerBean.class)
                .withThrowExceptions(false)
                .build();

        // final CsvToBean<TrailerBean> beans = new CsvToBeanBuilder(
        // new FileReader(new
        // File(CSVValidator.class.getClassLoader().getResource(csvFileName).getPath())))
        // .withSeparator('|') // or any other separator you are using
        // .withType(TrailerBean.class)
        // .withThrowExceptions(false)
        // .build();

        final List<TrailerBean> trailers = beans.parse();

        trailers.stream().forEach((bean) -> {
            logger.info("Parsed data: " + bean.toString());
        });

        beans.getCapturedExceptions().stream().forEach((exception) -> {
            logger.error("Incosistent data in row: " + exception.getLineNumber() + " -> " + exception.getMessage());
        });
    }
}
