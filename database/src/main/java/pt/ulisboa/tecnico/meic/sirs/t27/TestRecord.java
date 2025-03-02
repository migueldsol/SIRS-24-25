package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import org.json.JSONObject;
import org.json.JSONArray;

public class TestRecord {
    private String mechanicID;
    private String testsPerformed;

    public TestRecord(String mechanicID, String testsPerformed) {
        this.mechanicID = mechanicID;
        this.testsPerformed = testsPerformed;
    }

    @Override
    public String toString() {
        StringBuilder formattedTests = new StringBuilder();
        try {
            JSONObject testsJson = new JSONObject(testsPerformed);
            for (String key : testsJson.keySet()) {
                formattedTests.append(key).append(": ");
                Object value = testsJson.get(key);
                if (value instanceof JSONObject) {
                    JSONObject nested = (JSONObject) value;
                    formattedTests.append("\n");
                    for (String nestedKey : nested.keySet()) {
                        formattedTests.append("  ").append(nestedKey).append(": ").append(nested.get(nestedKey)).append("\n");
                    }
                } else if (value instanceof JSONArray) {
                    JSONArray array = (JSONArray) value;
                    formattedTests.append(array.toString(2)).append("\n");
                } else {
                    formattedTests.append(value.toString()).append("\n");
                }
            }
        } catch (Exception e) {
            formattedTests.append("Invalid test data format.");
        }
        return "Mechanic: " + mechanicID + "\nTests:\n" + formattedTests.toString();
    }
}
