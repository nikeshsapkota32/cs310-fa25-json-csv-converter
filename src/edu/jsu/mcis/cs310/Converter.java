package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
           // 1. Reading CSV
        CSVReader reader = new CSVReader(new java.io.StringReader(csvString));
        java.util.List<String[]> rows = reader.readAll();
        
        // 2. Extracting headers
        String[] headers = rows.get(0);
        
        // 3. Preparing JSON arrays
        JsonArray prodNums = new JsonArray();
        JsonArray data = new JsonArray();
        
        // 4. Looping through CSV rows (skip header)
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            
            prodNums.add(row[0]); // first column = ProdNum
            
            JsonArray rowData = new JsonArray();
            // Add remaining columns
            for (int j = 1; j < row.length; j++) {
                if (headers[j].equalsIgnoreCase("Season") || headers[j].equalsIgnoreCase("Episode")) {
                    rowData.add(Integer.parseInt(row[j])); // convert to int
                } else {
                    rowData.add(row[j]); // keep as string
                }
            }
            data.add(rowData);
        }
        
        // 5. Build final JSON object
        JsonObject json = new JsonObject();
        json.put("ProdNums", prodNums);
        json.put("ColHeadings", new JsonArray(java.util.Arrays.asList(headers)));
        json.put("Data", data);
        
        // 6. Convert JSON to string
        result = json.toJson();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // 1. Parse JSON
        JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
        JsonArray prodNums = (JsonArray) json.get("ProdNums");
        JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
        JsonArray data = (JsonArray) json.get("Data");
        
        // 2. Prepare CSV writer
        java.io.StringWriter sw = new java.io.StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        
        // 3. Write header
        String[] headerRow = new String[colHeadings.size()];
        for (int i = 0; i < colHeadings.size(); i++) {
            headerRow[i] = colHeadings.get(i).toString();
        }
        writer.writeNext(headerRow);
        
        // 4. Write data rows
        for (int i = 0; i < prodNums.size(); i++) {
            JsonArray rowData = (JsonArray) data.get(i);
            String[] row = new String[1 + rowData.size()]; // 1 for ProdNum
            
            row[0] = prodNums.get(i).toString(); // first column
            
            for (int j = 0; j < rowData.size(); j++) {
                String colName = colHeadings.get(j + 1).toString();
                
                if (colName.equalsIgnoreCase("Episode")) {
                    // Pad with zero if needed
                    int ep = ((Number) rowData.get(j)).intValue();
                    row[j + 1] = String.format("%02d", ep);
                } else if (colName.equalsIgnoreCase("Season")) {
                    // convert to string (no padding)
                    row[j + 1] = rowData.get(j).toString();
                } else {
                    row[j + 1] = rowData.get(j).toString();
                }
            }
            
            writer.writeNext(row);
        }
        
        writer.close();
        result = sw.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
