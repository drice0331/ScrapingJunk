package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by dr.ice on 9/22/2017.
 */
public class AttorneyGeneralScrapeController extends BaseScrapeController {

    private final String hicSearchNameSelector = "txtHICNumber";
    private final String searchButtonSelector = "#btnSearch_BImg";
    private final String resultTableSelector = "#gvBusiness_tcrow0 table";
    private final String phoneColText = "Phone";

    public void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams, List<Object> result, boolean headlessBrowser) {
        setBrowser(headlessBrowser);

        setTotalClientsToListener(urlParams.size());

        String elementSelector = scrapeInfo.getWebElementSelector();
        String targetUrlFull = scrapeInfo.getTargetUrlPrefix();
        webDriver.navigate().to(targetUrlFull);
        try {
            int count = 0;
            for (UrlParam urlParam : urlParams) {
                webDriver.navigate().to(targetUrlFull);
                WebElement searchElement = this.webDriver.findElement(By.name(hicSearchNameSelector));
                sendKeysWDelay(searchElement, urlParam.getValue(), 2000);

                WebElement searchButtonElem = getWebElement(searchButtonSelector);
                searchButtonElem.click();

                WebElement resultTableElem = getWebElement(resultTableSelector);
                if(resultTableElem != null) {
                    WebElement targetElem = getTDValueNextToTDCol(resultTableElem, phoneColText);
                    if(targetElem != null) {
                        String resultText = targetElem.getText();
                        result.add(targetElem.getText());
                    } else {
                        sendMessageToListener("Can't find phone number for id " + urlParam.getValue());
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
