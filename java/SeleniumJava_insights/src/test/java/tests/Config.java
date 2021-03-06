// filename: tests/Config.java
package tests;

public class Config {
    public static final String baseUrl = System.getProperty("baseUrl", "http://the-internet.herokuapp.com");
    public static final String host = System.getProperty("host", "saucelabs");
    public static final String sauceUser = System.getenv("SAUCE_USERNAME");
    public static final String sauceKey = System.getenv("SAUCE_ACCESS_KEY");
    public static final String sauceTunnel = System.getenv("SAUCE_TUNNEL");
}
