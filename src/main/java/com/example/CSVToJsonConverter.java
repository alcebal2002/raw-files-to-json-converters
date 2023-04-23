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
    static String dataFileName = "csv_sample.csv";
    static String formatFileName = "csv_format.json";

    static String separator = "\\|";
    static int rowTypePosition;

    public static void main(String[] args) {

        JSONObject outputJson = new JSONObject();

        ArrayList<String> contentArray = new ArrayList<String>();

        JSONObject formatJson;
        StringBuffer formatStringBuffer = new StringBuffer();

        try (BufferedReader formatReader = new BufferedReader(new InputStreamReader(
                CSVToJsonConverter.class.getClassLoader().getResourceAsStream(formatFileName)))) {

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

        rowTypePosition = formatJson.getJSONObject("data").getInt("rowTypePosition");

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
        // Last line -> Trailers

        // Header
        outputJson.put("header", getJsonObjectFromLine(contentArray.get(0), formatJson, "header"));
        logger.info("output with header: ", outputJson.toString(2));
        // Data rows
        JSONArray dataArray = new JSONArray();
        for (int i = 1; i < (numberOfLines - 1); i++) {
            dataArray.put(getJsonObjectFromLine(contentArray.get(i), formatJson, "data"));
        }
        outputJson.put("data", dataArray);
        logger.info("output with data: ", outputJson.toString(2));

        // Trailer
        outputJson.put("trailer", getJsonObjectFromLine(contentArray.get(numberOfLines - 1), formatJson, "trailer"));
        logger.info("output with trailer: ", outputJson.toString(2));

        logger.info(outputJson.toString(2));
        logger.info("data[0][first_field]: {}",
                outputJson.getJSONArray("data").getJSONObject(0).get("5").toString());
    }

    private static JSONObject getJsonObjectFromLine(String line, JSONObject formatJson, String block) {
        JSONObject jsonObject = new JSONObject();
        String[] dataValues = line.split(separator);
        for (int i = 0; i < dataValues.length; i++) {
            if ("data".equals(block)) {
                // data section contains rows of different types (with different lengths)
                // the formatJson file can replace data row keys by specific names if they are
                // defined in that file
                jsonObject.put(
                        (formatJson.getJSONObject(block)
                                .has(dataValues[rowTypePosition])
                                && formatJson.getJSONObject(block).getJSONObject(dataValues[rowTypePosition])
                                        .has("" + i))
                                                ? formatJson.getJSONObject(block)
                                                        .getJSONObject(dataValues[rowTypePosition])
                                                        .getString("" + i)
                                                : ("" + i),
                        dataValues[i]);
            } else {
                jsonObject.put(
                        (formatJson.getJSONObject(block).has("" + i))
                                ? formatJson.getJSONObject(block).getString("" + i)
                                : ("" + i),
                        dataValues[i]);
            }
        }
        return jsonObject;
    }
}
