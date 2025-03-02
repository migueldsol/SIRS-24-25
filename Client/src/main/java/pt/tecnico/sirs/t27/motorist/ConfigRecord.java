package main.java.pt.tecnico.sirs.t27.motorist;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigRecord {
    private JSONObject configuration;

    public ConfigRecord(String configurationWrapper) {
        // Parse the outer JSON object
        JSONObject wrapper = new JSONObject(configurationWrapper);

        // Extract the "configuration" field as a JSONObject
        this.configuration = wrapper.getJSONObject("configuration");
    }

    public String formatConfiguration() {
        StringBuilder formatted = new StringBuilder();

        // Add a first line saying "Configuration:"
        formatted.append("\nCurrent Configuration:\n");

        // Process each key in the configuration JSONObject
        for (String key : configuration.keySet()) {
            formatted.append(key).append(":\n");

            // Handle JSONArray under each key
            JSONArray array = configuration.optJSONArray(key);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    if (item != null) {
                        for (String subKey : item.keySet()) {
                            formatted.append("  - ").append(subKey).append(": ").append(item.get(subKey)).append("\n");
                        }
                    }
                }
            }
        }

        return formatted.toString();
    }

    @Override
    public String toString() {
        return formatConfiguration();
    }
}
