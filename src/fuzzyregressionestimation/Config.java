package fuzzyregressionestimation;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    public static String getDirectory() throws IOException {
        return Files.readString(Paths.get("directory.txt")).trim();
    }
}
