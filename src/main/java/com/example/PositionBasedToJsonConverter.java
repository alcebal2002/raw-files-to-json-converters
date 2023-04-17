package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionBasedToJsonConverter {

    static final Logger logger = LoggerFactory.getLogger(PositionBasedToJsonConverter.class);

    public static void main(String[] args) {

        String formatFileName = "position_based_format.json";
        String dataFileName = "position_based_data.txt";
        JSONObject outputJson = new JSONObject();

        logger.info("Start Position-Based Converter...");

        ArrayList<String> dataContentArray = new ArrayList<String>();
        JSONObject formatJson;
        StringBuffer formatStringBuffer = new StringBuffer();

        try (BufferedReader formatReader = new BufferedReader(new InputStreamReader(
                PositionBasedToJsonConverter.class.getClassLoader().getResourceAsStream(formatFileName)))) {

            String line = formatReader.readLine();
            formatStringBuffer = new StringBuffer();
            while (line != null) {
                formatStringBuffer.append(line);
                line = formatReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        formatJson = new JSONObject(formatStringBuffer.toString());
        logger.info("formatJson: {}", formatJson);

        // Read the data file and count the number of lines of the file
        int numberOfLines = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                PositionBasedToJsonConverter.class.getClassLoader().getResourceAsStream(dataFileName)))) {
            String line = reader.readLine();
            while (line != null) {
                dataContentArray.add(line);
                numberOfLines++;
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Number of lines: {}", numberOfLines);
        logger.info("dataContentArray: {}", dataContentArray);

        // Generate outputJsonObject from the sample file
        // First line -> Header
        // Middle lines -> Account Balance
        // Last line -> Trailer

        // Header
        outputJson.put("header",
                getJsonObjectFromLine(dataContentArray.get(0), formatJson, "header"));

        // AccountBalances
        JSONArray accountBalancesObject = new JSONArray();
        for (int i = 1; i < (dataContentArray.size() - 1); i++) {
            accountBalancesObject
                    .put(getJsonObjectFromLine(dataContentArray.get(i), formatJson, "accountBalance"));
        }
        outputJson.put("accountBalances", accountBalancesObject);

        // Trailer
        outputJson.put("trailer",
                getJsonObjectFromLine(dataContentArray.get(numberOfLines - 1), formatJson, "trailer"));

        // print the JSON object to the console
        logger.info("outputJson: " + outputJson.toString(2));
        logger.info("End Position-Based Converter");
    }

    private static JSONObject getJsonObjectFromLine(String line, JSONObject formatJson, String jsonKey) {
        JSONObject jsonObject = new JSONObject();
        for (String fieldName : formatJson.getJSONObject(jsonKey).keySet()) {
            JSONObject fieldInfo = formatJson.getJSONObject(jsonKey).getJSONObject(fieldName);
            int startPos = fieldInfo.getInt("start");
            int endPos = fieldInfo.getInt("end");
            jsonObject.put(fieldName,
                    (line.substring(startPos - 1, endPos - 1)));
        }
        return jsonObject;
    }
}
