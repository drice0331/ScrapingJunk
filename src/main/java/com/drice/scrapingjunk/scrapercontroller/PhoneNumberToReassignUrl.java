package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Created by DrIce on 11/28/17.
 */
public class PhoneNumberToReassignUrl extends BaseScrapeController {

    private String buttonSelector = "input[type=button]";
    private String formReassignButtonNameChrome = "duplicateForm";
    public String reassignBaseUrl = "https://csrtools.servicemagic.com/crm/hunter/duplicate.crm?pageMode=HS&keepSameSalesRep=true&hunter=true&metered=false&busPhone=";
    private String regexNumber = "[0-9]*";

    public void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {

        setBrowser(headlessBrowser);
        setTotalClientsToListener(urlParams.size());        
        
        String loginUrl = scrapeInfo.getLoginUrl();
        if(this.login(loginCredentials, loginUrl)) {

            //Go through result list
            for (UrlParam contactInfo : urlParams) {
                String modifiedContactInfo = contactInfo.getValue();
                modifiedContactInfo = modifiedContactInfo.replace("(", "");
                modifiedContactInfo = modifiedContactInfo.replace(")", "");
                modifiedContactInfo = modifiedContactInfo.replace(" ", "");
                modifiedContactInfo = modifiedContactInfo.replace("-", "");
                if (modifiedContactInfo.matches(regexNumber)) {
                    //Assume we have a valid phone number
                    String reassignUrlWModifiedPhone = reassignBaseUrl + modifiedContactInfo;
                    boolean buttonFound = false;
                    try {
                        sendMessageToListener("About to navigate to " + reassignUrlWModifiedPhone);
                        this.webDriver.navigate().to(reassignUrlWModifiedPhone);

                        WebElement reassignButtonForm = this.webDriver.findElement(By.name(formReassignButtonNameChrome));
                        reassignButtonForm.submit();
                        sendMessageToListener("Clicked reassign button - reassign url modified phone");

                        //new WebDriverWait(webDriver).until(ExpectedConditions.alertIsPresen‌​t());
                        this.webDriver.switchTo().alert().accept();
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
    }
}
