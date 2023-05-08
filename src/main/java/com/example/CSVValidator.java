package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

/**
 *
 *
 */
public class CSVValidator {

    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(CSVValidator.class);
        final String csvFileName = "csv_sample.csv";
        final String delimiter = "\\|";

        final String config = "{" +
                "  \"rowTypePosition\": 0," +
                "  \"dataTypePosition\": 1," +
                "  \"rowTypes\": {" +
                "    \"H\": \"com.example.HeaderBean\"," +
                "    \"T\": \"com.example.TrailerBean\"," +
                "    \"D\": {" +
                "      \"LOAN100\": \"com.example.Loan100Bean\"," +
                "      \"LOAN108\": \"com.example.Loan108Bean\"," +
                "      \"LOAN900\": \"com.example.Loan900Bean\"" +
                "    }" +
                "  }" +
                "}";

        JSONObject jsonConfig = new JSONObject(config);

        logger.info("config: " + jsonConfig.toString(2));

        JSONObject result = new JSONObject();
        result.put("status", "OK");
        result.put("errors", new JSONArray());

        HashMap<String, List<String>> csvTypeMap = new HashMap<String, List<String>>();

        try (CSVReader reader = new CSVReader(
                new FileReader(new File(CSVValidator.class.getClassLoader().getResource(csvFileName).getPath())))) {
            List<String[]> fullCsv = reader.readAll();
            fullCsv.forEach(csvLine -> {
                String rowType = csvLine[0]
                        .split(delimiter)[(int) jsonConfig.get("rowTypePosition")];
                String dataType = csvLine[0]
                        .split(delimiter)[(int) jsonConfig.get("dataTypePosition")];
                String beanClass = rowType.equals("D")
                        ? (String) jsonConfig.getJSONObject("rowTypes").getJSONObject(rowType).get(dataType)
                        : (String) jsonConfig.getJSONObject("rowTypes").get(rowType);
                logger.info("Line: \"" + csvLine[0] + "\" -> RowType: " + rowType + " -> Bean: " + beanClass);

                csvTypeMap.computeIfAbsent(beanClass,
                        k -> new ArrayList<>()).add(csvLine[0]);
            });
        } catch (Exception e) {
            logger.error("Exception: " + e.getClass() + " - " + e.getMessage());
            result.put("status", "FAIL");
            result.put("errors", result.getJSONArray("errors").put(e.getClass() + " - " + e.getMessage()));
        }

        csvTypeMap.forEach((key, value) -> {
            StringBuilder stringBuilder = new StringBuilder();
            value.forEach(line -> {
                stringBuilder.append(line).append(System.lineSeparator());
            });
            // Parse rows for a particular rowType (ie. bean)
            logger.info("Parsing rows for type: " + key);
            try {
                Class<?> clazz = Class.forName(key);
                Reader reader = new StringReader(stringBuilder.toString());

                CsvToBean<Object> beans = new CsvToBeanBuilder(reader)
                        .withSeparator('|') // or any other separator you are using
                        .withType(clazz)
                        .withThrowExceptions(false)
                        .build();

                List<Object> parsedBeans = beans.parse();

                parsedBeans.stream().forEach((bean) -> {
                    logger.info("  > Parsed data for type [" + key + "]:" + bean.toString());
                });

                beans.getCapturedExceptions().stream().forEach((exception) -> {
                    logger.error(
                            "  > Incosistent data in row: " + exception.getLineNumber() + " -> "
                                    + exception.getMessage());
                });
            } catch (ClassNotFoundException e) {
                logger.error("Exception: " + e.getClass() + " - " + e.getMessage());
                result.put("status", "FAIL");
                result.put("errors", result.getJSONArray("errors").put(e.getClass() + " - " + e.getMessage()));

            }
        });
    }
}
