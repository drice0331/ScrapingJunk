package com.drice.scrapingjunk.model;

/**
 * Created by DrIce on 8/26/17.
 */
public class ScrapeInfoAndInput {

    String scrapeType;
    //private Class scrapeControllerClass;
    String loginUrl;
    String targetUrlPrefix;
    String targetUrlSuffix;
    String webElementSelector;
    String backupSelector;

    //Figure out way to make this cleaner, whether this is in this class or not
    String emailTemplateNumber;

    public ScrapeInfoAndInput() {

    }

    public ScrapeInfoAndInput(String scrapeType, String loginUrl, String targetUrlPrefix,
                              String targetUrlSuffix, String webElementSelector) {
        this.scrapeType = scrapeType;
        this.loginUrl = loginUrl;
        this.targetUrlPrefix = targetUrlPrefix;
        this.targetUrlSuffix = targetUrlSuffix;
        this.webElementSelector = webElementSelector;
    }

    public ScrapeInfoAndInput(String scrapeType, String loginUrl, String targetUrlPrefix,
                              String targetUrlSuffix, String webElementSelector, String backupSelector) {
        this.scrapeType = scrapeType;
        this.loginUrl = loginUrl;
        this.targetUrlPrefix = targetUrlPrefix;
        this.targetUrlSuffix = targetUrlSuffix;
        this.webElementSelector = webElementSelector;
        this.backupSelector = backupSelector;
    }

    public String getScrapeType() {
        return scrapeType;
    }

    public void setScrapeType(String scrapeType) {
        this.scrapeType = scrapeType;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getTargetUrlPrefix() {
        return targetUrlPrefix;
    }

    public void setTargetUrlPrefix(String targetUrlPrefix) {
        this.targetUrlPrefix = targetUrlPrefix;
    }

    public String getTargetUrlSuffix() {
        return targetUrlSuffix;
    }

    public void setTargetUrlSuffix(String targetUrlSuffix) {
        this.targetUrlSuffix = targetUrlSuffix;
    }

    public String getWebElementSelector() {
        return webElementSelector;
    }

    public void setWebElementSelector(String webElementSelector) {
        this.webElementSelector = webElementSelector;
    }

    public String getBackupSelector() {
        return backupSelector;
    }

    public void setBackupSelector(String backupSelector) {
        this.backupSelector = backupSelector;
    }


    public String getEmailTemplateNumber() {
        return emailTemplateNumber;
    }

    public void setEmailTemplateNumber(String emailTemplateNumber) {
        this.emailTemplateNumber = emailTemplateNumber;
    }

    @Override
    public String toString() {
        return this.scrapeType;
    }
}
