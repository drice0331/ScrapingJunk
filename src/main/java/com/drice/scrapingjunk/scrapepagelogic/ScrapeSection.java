package com.drice.scrapingjunk.scrapepagelogic;

import com.drice.scrapingjunk.model.UrlParam;
import com.drice.scrapingjunk.scrapercontroller.BaseScrapeController;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by DrIce on 12/18/17.
 */
public abstract class ScrapeSection {

    protected BaseScrapeController scrapeController;
    protected WebDriver webDriver;

    public ScrapeSection(BaseScrapeController scrapeController) {
        this.scrapeController = scrapeController;
        this.webDriver = scrapeController.getWebDriver();
    }

    public abstract void processScrapeSection(List<UrlParam> csvParam, List<Object> result);

    protected WebElement getWebElement(String selector) {
        WebElement resultElem = null;
        List<WebElement> webElements = this.webDriver.findElements(By.cssSelector(selector));
        if(webElements != null && webElements.size() > 0) {
            resultElem = webElements.get(0);
        } else {
            this.scrapeController.sendMessageToListener("Element for selector " + selector + " not found");
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

    protected void submitWithRandomDelay(WebElement elem) {
        addRandomDelay();
        try {
            elem.submit();
        } catch (Exception e) {
            this.scrapeController.sendMessageToListener("Exception caught when trying to submit webElement");
        }
    }

    protected void addRandomDelay() {
        try {
            Double ratio = Math.random();
            Double millis = (ratio * 2000) + 1000;
            Thread.sleep(millis.intValue());
        } catch (InterruptedException ie) {
            this.scrapeController.sendMessageToListener("Error adding random delay");
        }
    }

    public abstract String getScrapeSectionName();

}
