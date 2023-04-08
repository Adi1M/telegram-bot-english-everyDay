package utils;

import java.io.*;
import java.util.Properties;

public class PropUtils {
    final static String resourcesPath = "src/test/resources/";
    public static Properties getProperties(String fileName) throws IOException {
        InputStream file = new FileInputStream(resourcesPath + fileName);
        Properties properties = new Properties();
        properties.load(file);
        return properties;
    }
}
