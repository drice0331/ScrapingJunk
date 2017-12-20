package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by DrIce on 11/9/17.
 */
public class EntityIdToContactInfoWReassignScraper extends EntityIdToContactInfoScraper {

    private String buttonSelector = "input[type=button]";
    private String formReassignButtonNameChrome = "duplicateForm";
    private String formReassignButtonNameFireFox = "0.1_duplicateForm";
    public String reassignBaseUrl = "https://csrtools.servicemagic.com/crm/hunter/duplicate.crm?pageMode=HS&keepSameSalesRep=true&hunter=true&metered=false&busPhone=";
    private String regexNumber = "[0-9]*";



    public void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {
        super.startScrape(scrapeInfo, loginCredentials, urlParams, result, headlessBrowser);

        //Go through result list
        for(Object contactInfo : result) {
            String originalContactInfo = (String)contactInfo;
            String modifiedContactInfo = originalContactInfo;
            modifiedContactInfo = modifiedContactInfo.replace("(", "");
            modifiedContactInfo = modifiedContactInfo.replace(")", "");
            modifiedContactInfo = modifiedContactInfo.replace(" ", "");
            modifiedContactInfo = modifiedContactInfo.replace("-", "");
            if(modifiedContactInfo.matches(regexNumber)) {
                //Assume we have a valid phone number
                String reassignUrlWModifiedPhone = reassignBaseUrl + modifiedContactInfo;
                boolean buttonFound = false;
                try {
                    sendMessageToListener("About to navigate to " + reassignUrlWModifiedPhone);
                    this.webDriver.navigate().to(reassignUrlWModifiedPhone);

                    List<WebElement> elemList = this.webDriver.findElements(By.name(formReassignButtonNameChrome));
                    if(elemList != null && elemList.size() > 0) {
                        if(elemList.size() == 1) {
                            WebElement reassignButtonForm = elemList.get(0);
                            submitWithRandomDelay(reassignButtonForm);
                            sendMessageToListener("Clicked reassign button - reassign url modified phone");
                            //new WebDriverWait(webDriver).until(ExpectedConditions.alertIsPresent());
                            this.webDriver.switchTo().alert().accept();
                        } else {
                            sendMessageToListener("More than one button found on page, not clicking anything - " +
                                    "call Dr. Ice");
                        }
                    } else {
                        sendMessageToListener("Reassign button not found, skipping that part for this client");
                    }
                } catch (NoSuchElementException noSuchElementException) {
                    sendMessageToListener("Button element not found within reassignUrlWModifiedPhone url");
                } catch (NoAlertPresentException ex) {
                    sendMessageToListener("Alert not found, keep scraping");
                } catch (TimeoutException te) {
                    sendMessageToListener("Timeout exception caught, should just ignore and keep scraping");
                } catch (Exception e) {
                    sendMessageToListener("Exception caught on reassignUrlWModifiedPhone - " + e.getMessage());// + e.getMessage());
                }
            }
        }
    }
    private void click_ok_on_modal_popup_alert(WebElement we) {
        RemoteWebElement rwe = (RemoteWebElement) we;
        try {
            JavascriptExecutor jse = (JavascriptExecutor) rwe.getWrappedDriver();
            jse.executeScript("window.confirm = function(){return true;}");
            jse.executeScript("return window.confirm");
        } catch (Exception e) {
            sendMessageToListener("Error using/casting JavascriptExecutor on webDriver - " +
                    "click_ok_on_modal_popup_alert()");
        }
    }
}
