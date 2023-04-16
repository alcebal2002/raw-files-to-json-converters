package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class CSVToJsonConverter {

    static final Logger logger = LoggerFactory.getLogger(CSVToJsonConverter.class);

    public static void main(String[] args) {
        String dataFileName = "csv_sample.csv";
        JSONObject outputJson = new JSONObject();

        ArrayList<String> contentArray = new ArrayList<String>();

        // Read the csv file and count the number of lines of the file
        int numberOfLines = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                CSVToJsonConverter.class.getClassLoader().getResourceAsStream(dataFileName)))) {
            String line = reader.readLine();
            while (line != null) {
                contentArray.add(line);
                numberOfLines++;
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Number of lines: {}", numberOfLines);
        logger.info("file content: {}", contentArray);

        // Generate JsonObject from the sample file
        // First line -> Header
        // Middle lines -> Data
        // Last line -> Trailer

        // Header
        outputJson.put("header", getJsonObjectFromLine(contentArray.get(0)));

        // Data rows
        JSONArray dataArray = new JSONArray();
        for (int i = 1; i < (numberOfLines - 1); i++) {
            dataArray.put(getJsonObjectFromLine(contentArray.get(i)));
        }
        outputJson.put("data", dataArray);

        // Trailer
        outputJson.put("trailer", getJsonObjectFromLine(contentArray.get(numberOfLines - 1)));

        logger.info(outputJson.toString(2));
        logger.info("data[0][first_field]: {}",
                outputJson.getJSONArray("data").getJSONObject(0).get("0").toString());
    }

    private static JSONObject getJsonObjectFromLine(String line) {
        JSONObject jsonObject = new JSONObject();
        String[] dataValues = line.split("\\|");
        for (int i = 0; i < dataValues.length; i++) {
            jsonObject.put(Integer.toString(i), dataValues[i]);
        }
        return jsonObject;
    }
}
