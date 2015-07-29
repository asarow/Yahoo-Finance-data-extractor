package dev.stockanalyzer.pulldata;

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
 * @author Amandeep Sarow
 */
public class DataExtractor {
    private final String yahooApi = "https://query.yahooapis.com/v1/public/
        yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20
        (%22";
    private final String yahooApiEnd = "%22)&diagnostics=true&env=store%3A%
        2F%2Fdatatables.org%2Falltableswithkeys";
    private List<String[]> stockTickers = new ArrayList<String[]>();
    private static final int dataSize = 6;
    
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
	URL url;
	InputStream is = null;
	BufferedReader br;
	String stockXMLLine;
	String stockData[] = null;

	try {
	    // The YQL URL which requires modification for the stock ticker 
	    url = new URL(yahooApi + ticker + yahooApiEnd);
	    
	    try {
		is = url.openStream();  // throws an IOException
	    } catch (IOException io) {
		System.out.println("The URL failed to retrieve the stock 
                ticker from the API");
		io.printStackTrace();
		return null;
	    }
	    
	    br = new BufferedReader(new InputStreamReader(is));
	    
	    while ((stockXMLLine = br.readLine()) != null) {
		if (stockXMLLine.contains("LastTradePriceOnly")) {
		    /* Each line is processed individually starting with the
		       last trade price of the stock */
		    stockData = processLine(stockXMLLine, ticker);
		    stockTickers.add(stockData);
		}
	    }
	} catch (MalformedURLException mue) { 
	    //TODO: Check the necessities of these exceptions
	    mue.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} 
	return stockData;
    }
    
    /**
     * Processes each line returned from the YQL (in XML format) and returns
     * the necessary information as an array.
     *
     * @param stockLine the relevant line from the XML file.
     * @param tickerSymbol the stock ticker input by the user.
     * @return the stock data as an array.
     */
    private String[] processLine(String stockLine, String tickerSymbol) {
	String[] stockData = new String[dataSize];
	
	int firstIndex = stockLine.indexOf("<LastTradePriceOnly>") + 20;
	int lastIndex = stockLine.indexOf("</LastTradePriceOnly>");
	if (firstIndex == -1 || lastIndex == -1) {
	    System.out.println(tickerSymbol + " is not a valid ticker");
	    stockData[0] ="Invalid ticker";
	    return stockData;		
	}
	stockData[1] = stockLine.substring(firstIndex, lastIndex);
	
	firstIndex = stockLine.indexOf("<Name>")+6;
	lastIndex = stockLine.indexOf("</Name>");
	stockData[0] = stockLine.substring(firstIndex, lastIndex);
	
	firstIndex = stockLine.indexOf("<Change>") + 8;
	lastIndex = stockLine.indexOf("</Change>");
	stockData[2] = stockLine.substring(firstIndex, lastIndex);
	
	firstIndex = stockLine.indexOf("<MarketCapitalization>") + 22;
	lastIndex = stockLine.indexOf("</MarketCapitalization>");
	stockData[3] = stockLine.substring(firstIndex, lastIndex);
	
	firstIndex = stockLine.indexOf("<EBITDA>") + 8;
	lastIndex = stockLine.indexOf("</EBITDA>");
	stockData[4] = stockLine.substring(firstIndex, lastIndex);
	
	firstIndex = stockLine.indexOf("<PERatio>") + 9;
	lastIndex = stockLine.indexOf("</PERatio>");
	stockData[5] = stockLine.substring(firstIndex, lastIndex);
	
	return stockData;
    }
    
    /**
     * A getter which returns the current size of the stock data.
     *
     * @return the number of stock items as data. 
     */
    public static int getDataSize() {
	return dataSize;
    }    
}
