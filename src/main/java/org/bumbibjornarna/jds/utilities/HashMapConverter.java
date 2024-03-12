package org.bumbibjornarna.jds.utilities;

import java.util.HashMap;
import java.util.Map;

public class HashMapConverter {

    public static String hashMapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().replace("=", "\\=").replace(";", "\\;");
            String value = entry.getValue().replace("=", "\\=").replace(";", "\\;");

            stringBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append(";");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static HashMap<String, String> stringToHashMap(String str) {
        if (str == null || str.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = str.split("(?<!\\\\);");
        for (String pair : pairs) {
            String[] keyValue = pair.split("(?<!\\\\)=", 2);
            String key = keyValue[0].replace("\\=", "=").replace("\\;", ";");
            String value = (keyValue.length > 1) ? keyValue[1].replace("\\=", "=").replace("\\;", ";") : "";
            map.put(key, value);
        }
        return map;
    }
}
