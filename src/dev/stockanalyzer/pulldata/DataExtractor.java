package dev.stockanalyzer.pulldata;

import YahooFinanceYQLWrapper.YQLWrapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Extracts stock data from the Yahoo! YQL/API and stores the stock data for
 * use in the model view controller. 
 * 
 * Update: This class now uses an external wrapper for the YQL to retrieve data.
 * The old code is still available in the GitHub repo history.
 * @author Amandeep Sarow
 */
public class DataExtractor {
    private static final int DATA_SIZE = 6;
    
    public DataExtractor() {
    }

    /**
     * Retrieves stock data for a given stock ticker and stores the data into
     * an arraylist.
     *
     * @param ticker the stock ticker input from the user.
     * @return the stock information as an arraylist.
     */
    public String[] pullTickerData (String ticker) {
	String[] stockDataToReturn = new String[DATA_SIZE];
	stockDataToReturn[0] = YQLWrapper.companyName(ticker);
	stockDataToReturn[1] = Double.toString(YQLWrapper.stockPrice(ticker));
	stockDataToReturn[2] = YQLWrapper.changeInPrice(ticker);
	stockDataToReturn[3] = YQLWrapper.marketCap(ticker);
	stockDataToReturn[4] = YQLWrapper.EBITDA(ticker);
	stockDataToReturn[5] = Double.toString(YQLWrapper.PERatio(ticker));

	return stockDataToReturn;
    }
    
    /**
     * A getter which returns the current size of the stock data.
     *
     * @return the number of stock items as data. 
     */
    public static int getDataSize() {
	return DATA_SIZE;
    }    
}
