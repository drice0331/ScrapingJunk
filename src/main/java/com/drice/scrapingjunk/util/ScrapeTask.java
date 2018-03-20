package com.drice.scrapingjunk.util;

import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfoAndInput;
import com.drice.scrapingjunk.model.UrlParam;
import com.drice.scrapingjunk.scrapercontroller.BaseScrapeController;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DrIce on 8/26/17.
 */
public class ScrapeTask extends Task<List<Object>> {

    Thread taskThread;

    BaseScrapeController scrapeController;
    ScrapeInfoAndInput scrapeInfo;
    LoginCredentials loginCredentials;
    List<UrlParam> urlParamList;
    boolean headlessBrowser;

    Object LOCK = new Object();
    volatile boolean paused = false;

    public ScrapeTask(BaseScrapeController scrapeController, ScrapeInfoAndInput scrapeInfo, LoginCredentials loginCredentials,
                      List<UrlParam> urlParamList, boolean headlessBrowser) {
        this.scrapeController = scrapeController;
        this.scrapeInfo = scrapeInfo;
        this.loginCredentials = loginCredentials;
        this.urlParamList = urlParamList;
        this.headlessBrowser = headlessBrowser;
    }

    protected List<Object> call() throws Exception {
        List<Object> resultsList = new ArrayList<Object>();
        scrapeController.startScrape(scrapeInfo, loginCredentials, urlParamList, resultsList, headlessBrowser);
        return resultsList;
    }


    public void run() {
        super.run();
        while(scrapeController.isRunning()) {
            if (paused) {
                scrapeController.setIsPaused(true);
                halt();
            }
        }
    }

    public boolean isThreadRunning() {
        return !paused;
        //return taskThread != null && taskThread.getState().equals(Thread.State.RUNNABLE);
    }

    public void start() {
        taskThread = new Thread(this);
        taskThread.start();
    }

    private void halt() {
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        paused = true;
        this.scrapeController.setIsPaused(true);
    }

    public void resume() {
        synchronized (LOCK) {
            paused = false;
            scrapeController.setIsPaused(false);
            LOCK.notify();
        }
    }
}
