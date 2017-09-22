package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.listener.ScrapeControllerListener;
import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

/**
 * Created by DrIce on 8/26/17.
 */
public abstract class BaseScrapeController {

    protected WebDriver webDriver;

    protected volatile boolean isPaused;
    protected boolean isRunning;

    private final String loginUsernameSelector = "userName";
    private final String passwordSelector = "password";
    private final String submitButtonSelector = "input[type='submit']";

    protected ScrapeControllerListener scrapeControllerListener;

    public BaseScrapeController() {
        init();
    }

    protected void init() {
    }

    public abstract void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams,
                                     List<Object> result, boolean headlessBrowser);

    protected boolean setBrowser(boolean headlessBrowser) {
        sendMessageToListener("Setting webdriver to " + (headlessBrowser ? "headless browser" : "visible browser"));

        try {
            String webDriverFileType = "";
            if(SystemUtils.IS_OS_WINDOWS) {
                webDriverFileType = ".exe";
            }
            if (headlessBrowser) {
                //for headless production ie windowless
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setJavascriptEnabled(true);
                caps.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled", true);
                caps.setCapability("phantomjs.page.settings.browserConnectionEnabled", true);
                caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs"
                        + webDriverFileType);

                this.webDriver = new PhantomJSDriver(caps);
                sendMessageToListener("Webdriver set");
            } else {
                //for debugging and dev work with a window
                System.setProperty("webdriver.chrome.driver", "chromedriver" + webDriverFileType);
                this.webDriver = new ChromeDriver();
            }
            return true;
        } catch (Exception e) {
            sendMessageToListener("Set browser failed - " + e.getMessage());
            sendMessageToListener("Aborting scan");
            return false;
        }
    }

    public void sendMessageToListener(String message) {
        if(this.scrapeControllerListener != null) {
            this.scrapeControllerListener.sendMessage(message);
        }
    }

    public void setTotalClientsToListener(int totalClients) {
        if(this.scrapeControllerListener != null) {
            this.scrapeControllerListener.setTotalClientsToScrape(totalClients);
        }
    }

    public void updateNumClientsToListener(int numClients) {
        if(this.scrapeControllerListener != null) {
            this.scrapeControllerListener.updateNumberClientsScraped(numClients);
        }
    }

    protected boolean login(LoginCredentials loginCredentials, String loginUrl) {
        try {
            this.webDriver.navigate().to(loginUrl);
            WebElement usernameElem = webDriver.findElement(By.name(loginUsernameSelector));
            WebElement passwordElem = webDriver.findElement(By.name(passwordSelector));
            WebElement submitButton = webDriver.findElement(By.cssSelector(submitButtonSelector));

            usernameElem.sendKeys(loginCredentials.getUsername());
            passwordElem.sendKeys(loginCredentials.getPassword());

            submitButton.submit();
            return true;
        } catch (Exception e) {
            sendMessageToListener("Login failed - " + e.getMessage());
            return false;
        }
    }

    protected WebElement getTDValueNextToTDCol(WebElement tableElem, String targetTDElemText) {
        WebElement tdResultElem = null;

        if(tableElem.getTagName().equals("table")) {
            List<WebElement> listOfTDs = tableElem.findElements(By.tagName("td"));
            int curTdElemInd = 0;
            for(WebElement tdElem : listOfTDs) {
                if(tdElem.getText().contains(targetTDElemText)) {
                    if(listOfTDs.size() > curTdElemInd + 1) {
                        tdResultElem = listOfTDs.get(curTdElemInd + 1);
                    }
                    break;
                }
                curTdElemInd++;
            }
        } else {
            sendMessageToListener("Supposed table we're searching through is not a table");
        }
        return tdResultElem;
    }

    protected WebElement getWebElement(String selector) {
        WebElement resultElem = null;
        List<WebElement> webElements = this.webDriver.findElements(By.cssSelector(selector));
        if(webElements != null && webElements.size() > 0) {
            resultElem = webElements.get(0);
        } else {
            sendMessageToListener("Element for selector " + selector + " not found");
        }
        return resultElem;
    }

    protected void sendKeysWDelay(WebElement elem, String seq, int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        elem.sendKeys(seq);
    }

    public ScrapeControllerListener getScanControllerListener() {
        return this.scrapeControllerListener;
    }

    public void setScanControllerListener(ScrapeControllerListener scrapeControllerListener) {
        this.scrapeControllerListener = scrapeControllerListener;
    }

    public boolean getIsPaused() {
        return this.isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

}
