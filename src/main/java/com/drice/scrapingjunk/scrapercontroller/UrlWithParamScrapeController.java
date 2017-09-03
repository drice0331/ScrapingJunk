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
                    WebElement targetElement = webDriver.findElement(By.cssSelector(elementSelector));
                    result.add(targetElement.getText().trim());
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
