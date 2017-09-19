package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by DrIce on 8/26/17.
 */
public class UrlWithParamScrapeController extends BaseScrapeController {

    public void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {
        setBrowser(headlessBrowser);

        setTotalClientsToListener(urlParams.size());

        String loginUrl = scrapeInfo.getLoginUrl();
        String elementSelector = scrapeInfo.getWebElementSelector();
        String targetUrlPrefix = scrapeInfo.getTargetUrlPrefix();
        String targetUrlSuffix = scrapeInfo.getTargetUrlSuffix();
        String targetUrlFull = "";
        if (this.login(loginCredentials, loginUrl)) {
            try {
                int count = 0;
                for (UrlParam urlParam : urlParams) {
                    targetUrlFull = targetUrlPrefix + urlParam.getValue() + targetUrlSuffix;
                    webDriver.navigate().to(targetUrlFull);
                    List<WebElement> targetElements = webDriver.findElements(By.cssSelector(elementSelector));
                    if(targetElements != null && targetElements.size() > 0) {
                        result.add(targetElements.get(0).getText().trim());
                    } else {
                        String backupSelector = scrapeInfo.getBackupSelector();
                        if(backupSelector != null && !backupSelector.equals("")) {
                            List<WebElement> backupElements = webDriver.findElements(
                                    By.cssSelector(backupSelector));
                            if(backupElements != null && backupElements.size() > 0) {
                                result.add(backupElements.get(0).getText().trim());
                            } else {
                                sendMessageToListener("Both target selector and backup selector " +
                                        "failed to find contact info for url " + targetUrlFull);
                            }
                        } else {
                            sendMessageToListener("Target selector failed to find contact info for " +
                                    "url " + targetUrlFull);
                        }
                    }
                    count++;
                    updateNumClientsToListener(count);
                }
            }
            catch (Exception e) {
                sendMessageToListener("Scrape failed going to " + targetUrlFull + " current url - "
                        + webDriver.getCurrentUrl() + ", Error - " + e.getMessage());
            }
        }
    }
}
