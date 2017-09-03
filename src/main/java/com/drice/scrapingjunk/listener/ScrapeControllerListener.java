package com.drice.scrapingjunk.listener;

/**
 * Created by DrIce on 8/28/17.
 */
public interface ScrapeControllerListener {

    void sendMessage(String message);
    void setTotalClientsToScrape(int totalClients);
    void updateNumberClientsScraped(int clientsScraped);
}
