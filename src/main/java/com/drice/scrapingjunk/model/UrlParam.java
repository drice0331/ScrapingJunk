package com.drice.scrapingjunk.model;

/**
 * Created by DrIce on 8/26/17.
 */
public class UrlParam {

    private String paramName;
    private String value;

    public UrlParam(String paramName, String value) {
        this.paramName = paramName;
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public String getValue() {
        return value;
    }
}
