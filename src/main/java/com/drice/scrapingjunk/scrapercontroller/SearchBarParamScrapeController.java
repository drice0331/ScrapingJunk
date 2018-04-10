package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfoAndInput;
import com.drice.scrapingjunk.model.CSVInputParam;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by DrIce on 4/9/18.
 */
public class SearchBarParamScrapeController extends BaseScrapeController {

    public void startScrape(ScrapeInfoAndInput scrapeInfo, LoginCredentials loginCredentials, List<CSVInputParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {
        setBrowser(headlessBrowser);
        setTotalClientsToListener(urlParams.size());

        String loginUrl = scrapeInfo.getLoginUrl();
        String searchBarSelector = scrapeInfo.getSearchBarSelector();
        String searchButtonSelector = scrapeInfo.getSearchButtonSelector();
        String elementSelector = scrapeInfo.getWebElementSelector();
        String backupSelector = scrapeInfo.getBackupSelector();
        String targetUrlPrefix = scrapeInfo.getTargetUrlPrefix();
        String targetUrlSuffix = scrapeInfo.getTargetUrlSuffix();
        String targetUrlFull = "";
        if (loginUrl.equals("") || this.login(loginCredentials, loginUrl)) {
            try {
                int count = 0;
                for (CSVInputParam searchBarParam : urlParams) {
                    webDriver.navigate().to(targetUrlFull);
                    WebElement searchElement = this.webDriver.findElement(By.cssSelector(searchBarSelector));
                    sendKeysWDelay(searchElement, searchBarParam.getValue(), 2000);

                    WebElement searchButtonElem = getWebElement(searchButtonSelector);
                    searchButtonElem.click();

                    WebElement resultTableElem = getWebElement(elementSelector);
                    if(resultTableElem != null) {
                        WebElement targetElem = getWebElement(backupSelector);
                        if(targetElem != null) {
                            result.add(targetElem.getText());
                        } else {
                            sendMessageToListener("Can't find target or backup selector for param "
                                    + searchBarParam.getValue());
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