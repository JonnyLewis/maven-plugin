package com.apim.service;

import java.util.List;

public class PluginPropertyValues {

    public static String HOST;
    public static int OFFSET;
    public static String USERNAME;
    public static String PASSWORD;
    public static String APINAME;
    public static String DESCRIPTION;
    public static String CONTEXT;
    public static String VERSION;
    public static String APIPATH;
    public static String TYPE;
    public static List<String> TRANSPORTS;
    public static List<String> TIERS;
    public static String VISIBILITY;
    public static String PRODUCTION;
    public static String SANDBOX;
    public static String GATEWAY;

    // Preset values.
    public static int ADMINPORT = 9443 + OFFSET;
    public static int GATEWAYPORT = 8243 + OFFSET;

}
