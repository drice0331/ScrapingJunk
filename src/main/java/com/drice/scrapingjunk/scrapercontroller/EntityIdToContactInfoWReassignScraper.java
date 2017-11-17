package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

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
                    this.webDriver.navigate().to(reassignUrlWModifiedPhone);

                    WebElement reassignButtonForm = this.webDriver.findElement(By.name(formReassignButtonNameChrome));
                    reassignButtonForm.submit();
                    sendMessageToListener("Clicked reassign button");
                    //click_ok_on_modal_popup_alert(reassignButtonForm);
                    //WebElement webElement = this.webDriver.findElement(By.cssSelector(buttonSelector));
                    //webElement.click();
                    buttonFound = true;
                } catch (NoSuchElementException noSuchElementException) {
                    sendMessageToListener("Button element not found within reassignUrlWModifiedPhone url");
                } catch (Exception e) {
                    sendMessageToListener("Exception caught on reassignUrlWModifiedPhone - ");// + e.getMessage());
                }

                if(!buttonFound) {
                    String reassignUrlWOriginalPhone = reassignBaseUrl + originalContactInfo;
                    try {
                        this.webDriver.navigate().to(reassignUrlWOriginalPhone);

                        WebElement reassignButtonForm = this.webDriver.findElement(By.name(formReassignButtonNameChrome));
                        reassignButtonForm.submit();
                        sendMessageToListener("Clicked reassign button");
                        //click_ok_on_modal_popup_alert(reassignButtonForm);
                        //WebElement webElement = this.webDriver.findElement(By.cssSelector(buttonSelector));
                        //webElement.click();
                    } catch (NoSuchElementException noSuchElementException) {
                        sendMessageToListener("Button element not found within reassignUrlWOriginalPhone url");
                    } catch (Exception e) {
                        sendMessageToListener("Exception caught on reassignUrlWOriginalPhone - ");// + e.getMessage());
                    }
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
