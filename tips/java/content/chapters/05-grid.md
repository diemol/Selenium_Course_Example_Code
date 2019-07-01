# How To Use Selenium Grid

## The Problem

If you're looking to run your tests on different browser and operating system combinations but you're unable to justify using a third-party solution like [Sauce Labs](https://saucelabs.com/) or [Browser Stack](http://www.browserstack.com/) then what do you do?

## A Solution

With [Selenium Grid](https://github.com/SeleniumHQ/selenium/wiki/Grid2) you can stand up a simple infrastructure of various browsers on different operating systems to not only distribute test load, but also give you a diversity of browsers to work with.

## A brief Selenium Grid primer

Selenium Grid is part of [the Selenium project](http://www.seleniumhq.org/). It lets you distribute test execution across several machines. You can connect to it with Selenium Remote by specifying the browser, browser version, and operating system you want. You specify these values through Selenium Remote's `Capabilities`.

There are two main elements to Selenium Grid -- a hub, and nodes. First you need to stand up a hub. Then you can connect (or "register") nodes to that hub. Nodes are where your tests will run, and the hub is responsible for making sure your tests end up on the right one (e.g., the machine with the operating system and browser you specified in your test).

Let's step through an example.

## An Example

### Part 1: Grid Setup

Selenium Grid comes built into the Selenium Standalone Server. So to get started we'll need to download the latest version of it from [here](http://selenium-release.storage.googleapis.com/index.html).

Then we need to start the hub.

```sh
> java -jar selenium-server-standalone.jar -role hub
19:05:12.718 INFO - Launching Selenium Grid hub
...
```

After that we can register nodes to it.

```sh
> java -jar selenium-server-standalone.jar -role node -hub http://localhost:4444/grid/register
19:05:57.880 INFO - Launching a Selenium Grid node
...
```

__NOTE: This example only demonstrates a single node on the same machine as the hub. To span nodes across multiple machines you will need to place the standalone server on each machine and launch it with the same registration command (replacing `http://localhost` with the location of your hub, and specifying additional parameters as needed).__

Now that the grid is running we can view the available browsers by visiting our Grid's console at `http://localhost:4444/grid/console`.

<img src='http://elementalselenium.com/img/grid-console.png'/>

To refine the list of available browsers, we can specify an additional `-browser` parameter when registering the node. For instance, if we wanted to only offer Safari on a node, we could specify it with `-browser browserName=safari`, which would look like this:

```sh
java -jar selenium-server-standalone.jar -role node -browser browserName=safari -hub http://localhost:4444/grid/register
```

We could also repeat this parameter again if we wanted to explicitly specify more than one browser.

```sh
java -jar selenium-server-standalone.jar -role node -browser browserName=safari -browser browserName=chrome -browser browserName=firefox -hub http://localhost:4444/grid/register
```

There are numerous parameters that we can use at run time. You can see a full list [here](https://github.com/SeleniumHQ/selenium/wiki/Grid2#optional-parameters).

### Part 2: Test Setup

Now let's wire up a simple test to use our new Grid.

```java
// filename: Grid.java
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class Grid {
    WebDriver driver;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("firefox");
        String url = "http://localhost:4444/wd/hub";
        driver = new RemoteWebDriver(new URL(url), capabilities);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void gridTest() {
        driver.get("http://the-internet.herokuapp.com/");
        assertThat(driver.getTitle(), is(equalTo("The Internet")));
    }

}
```

Notice in this configuration we're using Selenium Remote (e.g., `new RemoteWebDriver()`) to connect to the grid. And we are telling the grid which browser we want to use with `setBrowserName` (e.g., `capabilities.setBrowser("firefox");`).

You can see a full list of the available Selenium `Capabilities` options [here](https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities).

## Expected Behavior

When you save this file and run it (e.g., `mvn clean test` from the command-line) here is what will happen:

+ Connect to the Grid Hub
+ Hub determines which node has the necessary browser/platform combination
+ Hub connects the test to the node
+ Browser opens on the node
+ Test runs
+ Browser closes on the node

## Outro

If you're looking to set up Selenium Grid to work with Internet Explorer or Chrome, be sure to read up on how to set them up since there is additional configuration required for each. And if you run into issues, be sure to check out the browser driver documentation for the browser you're working with:

+ [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/)
+ [EdgeDriver](https://docs.microsoft.com/en-us/microsoft-edge/webdriver)
+ [geckodriver (Firefox)](https://github.com/mozilla/geckodriver)
+ [InternetExplorerDriver](https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver)
+ [SafariDriver](https://developer.apple.com/documentation/webkit/testing_with_webdriver_in_safari)

Also, it's worth noting that while Selenium Grid is a great option for scaling your test infrastructure, it by itself will NOT give you parallelization. That is to say, it can handle as many connections as you throw at it (within reason), but you will still need to find a way to execute your tests in parallel.

Happy Testing!

