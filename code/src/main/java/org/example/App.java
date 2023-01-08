package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App {

    HashMap<String, Set<String>> dependenciesMap;
    String baseRoot = null;
    public void dfs(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            for (final File child : Objects.requireNonNull(file.listFiles())) {
                if (!dependenciesMap.containsKey(child.getAbsolutePath()) && !child.isDirectory()) {
                    dependenciesMap.put(child.getAbsolutePath(), new HashSet<String>());
                }
                dfs(child);
            }
        } else {
            String text = getFileText(file);
            final String requireRegex = "require ‘.*’";

            final Matcher m = Pattern.compile(requireRegex).matcher(text);
            while (m.find()) {
                String requirement = m.group(0);
                dependenciesMap.get(file.getAbsolutePath()).add(baseRoot + requirement.substring("require ‘".length(), requirement.length() - 1));
            }
        }
    }

    public boolean dfsCycleSearch(String path, ArrayList<String> cycle) {
        cycle.add(path);
        if (cycle.size() != 1 && cycle.get(0).equals(cycle.get(cycle.size() - 1))) {
            return true;
        }
        for (String element :
                dependenciesMap.get(path)) {
            if (dfsCycleSearch(element, cycle)) {
                return true;
            }
        }
        cycle.remove(cycle.size() - 1);
        return false;
    }

    public String getFileText(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);
        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }


    public void solve() throws FileNotFoundException {
        dependenciesMap = new HashMap<>();
        this.baseRoot = Paths.get("")
                .toAbsolutePath() + "\\test_root\\";
        File baseDir = new File(baseRoot);
        dfs(baseDir);
        String checkedFilePath = null;
        while (!dependenciesMap.isEmpty()) {
            for(Map.Entry<String, Set<String>> entry : dependenciesMap.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    checkedFilePath = entry.getKey();
                    System.out.println(getFileText(new File(checkedFilePath)));
                    break;
                }
            }
            if (checkedFilePath == null) {
                System.out.println("\n\nERROR: cycle dependencies");
                ArrayList<String> cycle = new ArrayList<>();
                dfsCycleSearch(dependenciesMap.entrySet().iterator().next().getKey(), cycle);
                for (String element :
                        cycle) {
                    System.out.println(element);
                }
                return;
            }
            dependenciesMap.remove(checkedFilePath);
            for(Map.Entry<String, Set<String>> entry : dependenciesMap.entrySet()) {
                entry.getValue().remove(checkedFilePath);
            }
            checkedFilePath = null;
        }
    }
}
