package com.projectkamberinesludogame.ludogame.utils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DocumentationUtils {

    private static final String PATH_WITH_CLASSES = "target/classes/";
    private static final String HTML_DOCUMENTATION_FILE_NAME = "doc/documentation.html";
    private static final String CLASS_FILE_NAME_EXTENSION = ".class";

    public static void generateHtmlDocumentationFile() throws IOException {

        Path start = Paths.get(PATH_WITH_CLASSES);
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            List<String> classList = stream
                    .filter(f -> f.getFileName().toString().endsWith(CLASS_FILE_NAME_EXTENSION)
                            && Character.isUpperCase(f.getFileName().toString().charAt(0)))
                    .map(String::valueOf)
                    .sorted()
                    .toList();

            String htmlString = generateHtmlDocumentationCode(classList);

            Files.writeString(Path.of(HTML_DOCUMENTATION_FILE_NAME), htmlString);
        }
    }

    private static String generateHtmlDocumentationCode(List<String> classList) {
        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("<!DOCTYPE html>\n<html>\n<head>\n");
        htmlContent.append("<title>Ludo Game Documentation</title>\n");
        htmlContent.append("<style>\n");
        htmlContent.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        htmlContent.append("table { border-collapse: collapse; width: 100%; margin-bottom: 30px; }\n");
        htmlContent.append("th, td { border: 1px solid black; padding: 8px; text-align: left; }\n");
        htmlContent.append("th { background-color: #4CAF50; color: white; }\n");
        htmlContent.append("h2 { color: #2E86C1; margin-top: 30px; }\n");
        htmlContent.append("h3 { color: #28B463; }\n");
        htmlContent.append(".package { color: #666; font-style: italic; }\n");
        htmlContent.append("</style>\n");
        htmlContent.append("</head>\n<body>\n");
        htmlContent.append("<h1>Ludo Game - Documentation</h1>\n");

        for (String classPath : classList) {
            String className = classPath
                    .substring(PATH_WITH_CLASSES.length(), classPath.length() - CLASS_FILE_NAME_EXTENSION.length())
                    .replace("\\", ".");

            try {
                Class<?> clazz = Class.forName(className);

                htmlContent.append("<h2>Class: ").append(clazz.getSimpleName()).append("</h2>\n");
                htmlContent.append("<p class='package'>Package: ").append(clazz.getPackage().getName()).append("</p>\n");


                htmlContent.append("<h3>Fields</h3>\n<table>\n");
                htmlContent.append("<tr><th>Field Name</th><th>Type</th><th>Modifiers</th></tr>\n");

                Field[] fields = clazz.getDeclaredFields();
                if (fields.length == 0) {
                    htmlContent.append("<tr><td colspan='3'>No fields</td></tr>\n");
                } else {
                    for (Field field : fields) {
                        htmlContent.append("<tr>");
                        htmlContent.append("<td>").append(field.getName()).append("</td>");
                        htmlContent.append("<td>").append(field.getType().getSimpleName()).append("</td>");
                        htmlContent.append("<td>").append(Modifier.toString(field.getModifiers())).append("</td>");
                        htmlContent.append("</tr>\n");
                    }
                }
                htmlContent.append("</table>\n");

                htmlContent.append("<h3>Methods</h3>\n<table>\n");
                htmlContent.append("<tr><th>Method Name</th><th>Return Type</th><th>Parameters</th><th>Modifiers</th></tr>\n");

                Method[] methods = clazz.getDeclaredMethods();
                if (methods.length == 0) {
                    htmlContent.append("<tr><td colspan='4'>No methods</td></tr>\n");
                } else {
                    for (Method method : methods) {
                        htmlContent.append("<tr>");
                        htmlContent.append("<td>").append(method.getName()).append("</td>");
                        htmlContent.append("<td>").append(method.getReturnType().getSimpleName()).append("</td>");

                        Parameter[] params = method.getParameters();
                        htmlContent.append("<td>");
                        if (params.length == 0) {
                            htmlContent.append("None");
                        } else {
                            for (int i = 0; i < params.length; i++) {
                                htmlContent.append(params[i].getType().getSimpleName());
                                htmlContent.append(" ").append(params[i].getName());
                                if (i < params.length - 1) htmlContent.append(", ");
                            }
                        }
                        htmlContent.append("</td>");
                        htmlContent.append("<td>").append(Modifier.toString(method.getModifiers())).append("</td>");
                        htmlContent.append("</tr>\n");
                    }
                }
                htmlContent.append("</table>\n");

            } catch (ClassNotFoundException e) {
                htmlContent.append("<p style='color: red;'>Error loading class: ").append(className).append("</p>\n");
            }
        }

        htmlContent.append("</body>\n</html>");

        return htmlContent.toString();
    }
}
