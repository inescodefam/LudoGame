package com.projectkamberinesludogame.ludogame.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static Properties properties;

    static {
        properties = new Properties();
        Hashtable<String, String> config = new Hashtable<>();
        config.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        config.put(Context.PROVIDER_URL, "file:./src/main/resources");

        try(InitialDirContextClosable context = new InitialDirContextClosable(config)) {
            Object configurationObject = context.lookup("app.conf");
            properties.load(new FileReader(configurationObject.toString()));
        } catch (NamingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringForKey(ConfigurationKey key) {
        return (String) properties.get(key.getKey());
    }

    public static Integer getIntegerForKey(ConfigurationKey key) {
        return Integer.valueOf((String) properties.get(key.getKey()));
    }
}
