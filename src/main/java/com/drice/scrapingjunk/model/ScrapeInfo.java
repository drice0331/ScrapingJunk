package com.drice.scrapingjunk.model;

import com.drice.scrapingjunk.scrapercontroller.BaseScrapeController;

/**
 * Created by DrIce on 8/26/17.
 */
public class ScrapeInfo {

    String scrapeType;
    //private Class scrapeControllerClass;
    String loginUrl;
    String targetUrlPrefix;
    String targetUrlSuffix;
    String webElementSelector;

    public ScrapeInfo() {

    }

    public ScrapeInfo(String scrapeType, String loginUrl, String targetUrlPrefix,
                      String targetUrlSuffix, String webElementSelector) {
        this.scrapeType = scrapeType;
        this.loginUrl = loginUrl;
        this.targetUrlPrefix = targetUrlPrefix;
        this.targetUrlSuffix = targetUrlSuffix;
        this.webElementSelector = webElementSelector;
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

    @Override
    public String toString() {
        return this.scrapeType;
    }
}
