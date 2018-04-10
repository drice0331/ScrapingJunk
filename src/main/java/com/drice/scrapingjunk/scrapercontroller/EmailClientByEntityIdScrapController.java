package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfoAndInput;
import com.drice.scrapingjunk.model.CSVInputParam;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by DrIce on 3/19/18.
 */
public class EmailClientByEntityIdScrapController extends BaseScrapeController {

    protected String sendEmailUrl = "https://csrtools.servicemagic.com/crm/communications/spSendEmailLoad.crm?entityID=";
    protected String emailButtonSelectorTemplate = "input[onclick=submitForm(this.form, 'TEMPLATENUMBER')]";
    protected String templateNumberPlaceholder = "TEMPLATENUMBER";

    public void startScrape(ScrapeInfoAndInput scrapeInfo, LoginCredentials loginCredentials, List<CSVInputParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {
        setBrowser(headlessBrowser);

        setTotalClientsToListener(urlParams.size());

        String loginUrl = scrapeInfo.getLoginUrl();
        if (this.login(loginCredentials, loginUrl)) {
            try {
                int count = 0;
                for (CSVInputParam urlParam : urlParams) {
                    //Navigate to send email page
                    String sendEmailUrlForEntityId = sendEmailUrl + urlParam.getValue();
                    this.webDriver.navigate().to(sendEmailUrlForEntityId);
                    /*
                    String emailSendButtonSelector = emailButtonSelectorTemplate;
                    emailSendButtonSelector = emailSendButtonSelector.replace(templateNumberPlaceholder,
                            scrapeInfo.getEmailTemplateNumber());
                            */
                    String emailSendButtonSelector = scrapeInfo.getWebElementSelector();
                    WebElement sendButtonElement = getWebElement(emailSendButtonSelector);
                    if(sendButtonElement == null) {
                        throw new NoSuchElementException("");
                    }
                    sendButtonElement.click();
                    count++;
                    updateNumClientsToListener(count);
                }
            } catch (NoSuchElementException noSuchElementException) {
                sendMessageToListener("EmailSendButtonSelector for template " + scrapeInfo.getEmailTemplateNumber()
                        + " not found url, going to next entityID");
            } catch (Exception e) {
                sendMessageToListener("Send email failed. Likely from emailButton click. Error message - "
                        + e.getMessage());
            }
        }
    }
}
