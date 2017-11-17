package com.drice.scrapingjunk.scrapercontroller;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;

import java.util.List;

/**
 * Created by DrIce on 11/6/17.
 */
public class EntityIdToContactInfoScraper extends UrlWithParamScrapeController {
    public void startScrape(ScrapeInfo scrapeInfo, LoginCredentials loginCredentials, List<UrlParam> urlParams,
                            List<Object> result, boolean headlessBrowser) {
        super.startScrape(scrapeInfo, loginCredentials, urlParams, result, headlessBrowser);
    }
}
