package com.drice.scrapingjunk.scrapepagelogic;

import com.drice.scrapingjunk.model.UrlParam;
import com.drice.scrapingjunk.scrapercontroller.BaseScrapeController;

import java.util.List;

/**
 * Created by DrIce on 12/18/17.
 */
public class GetPrimContactScrapeSection extends ScrapeSection {

    public GetPrimContactScrapeSection(BaseScrapeController scrapeController) {
        super(scrapeController);
    }

    public void processScrapeSection(List<UrlParam> csvParam, List<Object> result) {

    }

    public String getScrapeSectionName() {
        return null;
    }
}
