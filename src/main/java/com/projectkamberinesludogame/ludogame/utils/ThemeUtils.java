package com.projectkamberinesludogame.ludogame.utils;

import com.projectkamberinesludogame.ludogame.model.PlayerType;
import com.projectkamberinesludogame.ludogame.model.Theme;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ThemeUtils {

    private static final String THEME_CONFIG_FILE =  "xml/themeConfig.xml";
    private static final String DTD_FILE = "xml/themeConfig.dtd";

    public static boolean saveTheme(PlayerType playerType, Theme theme) {
        try {
            File configFile = new File(THEME_CONFIG_FILE);
            Document doc;

            if (configFile.exists()) {
                doc = loadDocument(configFile);
            } else {
                doc = createNewDocument();
            }

            updatePlayerTheme(doc, playerType, theme);

            saveDocument(doc, configFile);

            System.out.println("Theme saved successfully for " + playerType);
            return true;

        } catch (Exception e) {
            System.err.println("Error saving theme: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static Theme loadTheme(PlayerType playerType) {
        try {
            File configFile = new File(THEME_CONFIG_FILE);

            if (!configFile.exists()) {
                System.out.println("No theme config file found. Using default theme.");
                return Theme.DEFAULT;
            }

            Document doc = loadDocument(configFile);
            NodeList playerThemes = doc.getElementsByTagName("player-theme");

            String playerId = getPlayerId(playerType);

            for (int i = 0; i < playerThemes.getLength(); i++) {
                Element playerTheme = (Element) playerThemes.item(i);
                String id = playerTheme.getAttribute("player-id");

                if (id.equals(playerId)) {
                    String themeName = playerTheme.getElementsByTagName("selected-theme")
                            .item(0).getTextContent();

                    try {
                        return Theme.valueOf(themeName);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid theme name: " + themeName);
                        return Theme.DEFAULT;
                    }
                }
            }

            System.out.println("No saved theme found for " + playerType + ". Using default.");
            return Theme.DEFAULT;

        } catch (Exception e) {
            System.err.println("Error loading theme: " + e.getMessage());
            e.printStackTrace();
            return Theme.DEFAULT;
        }
    }


    private static Document createNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // doc = builder.parse(new File(DTD_FILE));
        Element root = doc.createElement("theme-config");
        doc.appendChild(root);

        return doc;
    }

    private static Document loadDocument(File file) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler() {
            @Override
            public void error(org.xml.sax.SAXParseException e) throws SAXException {
                System.err.println("XML Validation Error: " + e.getMessage());
            }
        });

        return builder.parse(file);
    }

    private static void updatePlayerTheme(Document doc, PlayerType playerType, Theme theme) {
        String playerId = getPlayerId(playerType);
        String playerName = getPlayerName(playerType);

        NodeList playerThemes = doc.getElementsByTagName("player-theme");
        Element playerThemeElement = null;

        for (int i = 0; i < playerThemes.getLength(); i++) {
            Element element = (Element) playerThemes.item(i);
            if (element.getAttribute("player-id").equals(playerId)) {
                playerThemeElement = element;
                break;
            }
        }

        if (playerThemeElement == null) {
            playerThemeElement = doc.createElement("player-theme");
            playerThemeElement.setAttribute("player-id", playerId);

            Element playerNameElement = doc.createElement("player-name");
            playerNameElement.setTextContent(playerName);
            playerThemeElement.appendChild(playerNameElement);

            Element selectedThemeElement = doc.createElement("selected-theme");
            selectedThemeElement.setTextContent(theme.name());
            playerThemeElement.appendChild(selectedThemeElement);

            doc.getDocumentElement().appendChild(playerThemeElement);
        } else {
            NodeList selectedThemes = playerThemeElement.getElementsByTagName("selected-theme");
            if (selectedThemes.getLength() > 0) {
                selectedThemes.item(0).setTextContent(theme.name());
            }
        }
    }

    private static void saveDocument(Document doc, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        try {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // lijepo formatira xml
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, new File(DTD_FILE).getName());

        } catch (IllegalArgumentException e) {
            System.err.println("Error setting output properties: " + e.getMessage());
        }

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static String getPlayerId(PlayerType playerType) {
        switch (playerType) {
            case PLAYER_RED:
                return "RED";
            case PLAYER_BLUE:
                return "BLUE";
            case SINGLE_PLAYER:
                return "SINGLE";
            default:
                return "UNKNOWN";
        }
    }

    private static String getPlayerName(PlayerType playerType) {
        switch (playerType) {
            case PLAYER_RED:
                return "Red Player";
            case PLAYER_BLUE:
                return "Blue Player";
            case SINGLE_PLAYER:
                return "Single Player";
            default:
                return "Unknown Player";
        }
    }

    public static boolean deleteThemeConfig() {
        File configFile = new File(THEME_CONFIG_FILE);
        if (configFile.exists()) {
            return configFile.delete();
        }
        return false;
    }
}
