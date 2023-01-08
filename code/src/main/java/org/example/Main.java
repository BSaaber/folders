package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        App app = new App();
        app.solve();
    }
}