package dev.stockanalyzer.pulldata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pulls financial statement information from the Yahoo! YQL/API and stores the
 * data.
 *
 * @author Amandeep Sarow
 */
public class FinancialsExtractor {
    private  ArrayList<ArrayList<String>> incomeStatement = new ArrayList<ArrayList<String>>();
    private  List<String[]> balanceSheet = new ArrayList<String[]>();
    private  List<String[]> cashFlowsStatement = new ArrayList<String[]>();
    private  String yahooAPIFinancialBegin = "https://query.yahooapis.com/v1/"
        + "public/yql?q=SELECT%20*%20FROM%20yahoo.finance.";
    private  String yahooAPIFinancialMid = "%20WHERE%20symbol%3D'";
    private  String yahooAPIFinancialEnd = "'&diagnostics=true&env=store%3A%2F%"
        + "2Fdatatables.org%2Falltableswithkeys";
    private final String bs = "balancesheet", is = "incomestatement", 
	cf = "cashflow";
    private final String[] urlModifier = {bs, is, cf};
    private String ticker;
	
    public FinancialsExtractor() {	
    }
    
    /**
     * Pulls financial statement information from the Yahoo! YQL/API. The URL
     * used within this method does not return the information, rather it 
     * returns another URL to the specified financial statement.
     * 
     * @param ticker the stock ticker input by the user.
     */
    public void pullFinancialData(String ticker) {
	this.ticker = ticker; //TODO: Change
	URL url;
	InputStream istream = null;
	BufferedReader br;
	String line;
	
	for (int i = 0; i <  urlModifier.length; i++) {
	    try {
		url = new URL(yahooAPIFinancialBegin + urlModifier[i] + 
			      yahooAPIFinancialMid + ticker + 
			      yahooAPIFinancialEnd);
		try {
		    istream = url.openStream();  // throws an IOException
		} catch (IOException io) {
		    System.out.println("The URL failed to retrieve the stock" + 
                    "ticker from the API");
		    io.printStackTrace();
		}

		br = new BufferedReader(new InputStreamReader(istream));

		while ((line = br.readLine()) != null) {
		    /* All three financial statements are pulled from the YQL
		       /API. */
		    if(line.contains(urlModifier[i])) {
			if (urlModifier[i].equals(is)) {
			    pullIncomeStatement(line);
			} else if (urlModifier[i].equals(bs)) {
			    pullBalanceSheet(line);
			} else if (urlModifier[i].equals(cf)) {
			    pullCashFlows(line);
			} else {
			    System.out.println("Could not find financials");
			}
		    }
		}
	    } catch (MalformedURLException mue) { 
		//TODO: Check the necessities of these exceptions
		mue.printStackTrace();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } 
	}// end for
    }
    
    /** 
     * Getter which returns the income statement for a given ticker. 
     *
     * @return an array containing the income statement information.
     */
    public ArrayList<ArrayList<String>> getIncomeStatement() {
	return incomeStatement;
    }
    
    /** 
     * Getter which returns the balance sheet for a given ticker.
     *
     * @return an array containing the balance sheet information.
     */
    public List<String[]> getBalanceSheet() {
	return balanceSheet;
    }
    
    /**
     * Getter which returns the statement of cash flows for a given ticker.
     *
     * @return an array containing the statement of cash flows information.
     */
    public List<String[]> getCashFlowsStatement() {
	return cashFlowsStatement;
    }
    
    /* Begin private helper methods */

    /* Private methods are supplied a String which contains the direct URL to
       the financial statement(s). The statements are returned in HTML format
       therefore they are scraped and only the necessary data is stored. */

    /**
     * Pulls the income statement information from the supplied URL and stores
     * the information as an array.
     *
     * @param the string containing the  URL to query for the income statement 
     *        information.
     */
    private void pullIncomeStatement(String urlToPass) {
	int firstIndex = urlToPass.lastIndexOf("CDATA")+6;
	int lastIndex = urlToPass.lastIndexOf("]]");
	// The true URL is stored below.
	String incomeStatementURL = urlToPass.substring(firstIndex, lastIndex);
	List<String> isToBuild = new ArrayList<String>();
	URL url;
	InputStream istream = null;
	BufferedReader br;
	String line;
	boolean startScraping = false;
	boolean startIncrement = false;
	int i = 0;
	
	try {
	    url = new URL(incomeStatementURL);
	    try {
		istream = url.openStream();
	    } catch (IOException e) {
		System.out.println("Failed to retrieve balance sheet URL");
		e.printStackTrace();
	    }
	    
	    br = new BufferedReader(new InputStreamReader(istream));
	    
	    try {
		while((line = br.readLine())!= null && i != 17) {
		    /* Start scraping the HTML at this point */ 
		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    if (line.contains("Net Income Applicable To Common Shares"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			String returned = scrapeExcessHTML(line);
			if (returned.length() > 0) {
			    //  buildSheet() stores the necessary data
			    buildSheet(returned.split(" "));
			}
		    }
		    
		    if (startIncrement == true)
			i++;
		} // end while
			printBS();
	    } catch (IOException e) {
		System.out.println("Failed to read from income statement URL");
		e.printStackTrace();
	    }
	} catch (MalformedURLException e) {
	    System.out.println("Invalid income statement URL.");
	    e.printStackTrace();
	    return;
	}
    }
    
    /**
     * Pulls the balance sheet information from the supplied URL and stores
     * the information as an array.
     *
     * @param the string containing the  URL to query for the balance sheet 
     *        information.
     */
    private void pullBalanceSheet(String urlToPass) {
	int firstIndex = urlToPass.lastIndexOf("CDATA")+6;
	int lastIndex = urlToPass.lastIndexOf("]]");
	String balanceSheetURL = urlToPass.substring(firstIndex, lastIndex);
	URL url;
	InputStream istream = null;
	BufferedReader br;
	String line;
	boolean startScraping = false;
	boolean startIncrement = false;
	int i = 0;
	
	try {
	    url = new URL(balanceSheetURL);
	    try {
		istream = url.openStream();
	    } catch (IOException e) {
		System.out.println("Failed to retrieve balance sheet URL");
		e.printStackTrace();
	    }
	    
	    br = new BufferedReader(new InputStreamReader(istream));
	    
	    try {
		while((line = br.readLine())!= null && i != 17) {
		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    if (line.contains("Net Tangible Assets"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			String returned = scrapeExcessHTML(line);
			//if (returned.length() > 0)
			//System.out.println(returned);
		    }
		    if (startIncrement == true)
			i++;
		}
	    } catch (IOException e) {
		System.out.println("Failed to read from balance sheet URL");
		e.printStackTrace();
	    }
	} catch (MalformedURLException e) {
	    System.out.println("Invalid balance sheet URL.");
	    e.printStackTrace();
	    return;
	}		
    }
    
    /**
     * Pulls the cash flows statement information from the supplied URL and 
     * stores the information as an array.
     *
     * @param the string containing the  URL to query for the cash flows 
     * statement information.
     */
    private void pullCashFlows(String urlToPass) {
	int firstIndex = urlToPass.lastIndexOf("CDATA")+6;
	int lastIndex = urlToPass.lastIndexOf("]]");
	String cashFlowURL = urlToPass.substring(firstIndex, lastIndex);
	URL url;
	InputStream istream = null;
	BufferedReader br;
	String line;
	boolean startScraping = false;
	boolean startIncrement = false;
	int i = 0;
	
	try {
	    url = new URL(cashFlowURL);
	    try {
		istream = url.openStream();
	    } catch (IOException e) {
		System.out.println("Failed to retrieve balance sheet URL");
		e.printStackTrace();
	    }
	    
	    br = new BufferedReader(new InputStreamReader(istream));
	    
	    try {
		while((line = br.readLine())!= null && i != 17) {
		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    if (line.contains("Change In Cash and Cash Equivalents"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			
			//	if (returned.length() > 0)
			//System.out.println(returned);
		    }
		    if (startIncrement == true)
			i++;
		}
	    } catch (IOException e) {
		System.out.println("Failed to read from cash flows URL");
		e.printStackTrace();
	    }
	} catch (MalformedURLException e) {
	    System.out.println("Invalid cash flows URL.");
	    e.printStackTrace();
	    return;
	}
	
    }
    
    /**
     * Builds the financial statements as an array.
     *
     * @param line a line of data containing financial information.
     */
    private void buildSheet(String[] line) {
	ArrayList<String> dataToAdd = new ArrayList<String>();

	if (line.length == 0) 
	    return;

	if (line.length == 1 || line[0].equals("-")) {
	    dataToAdd.add(line[0]);
	    incomeStatement.add(dataToAdd);
	    return;
	}
	
	String combine = "";
	boolean printAgain = true;

	for (int i = 0; i < line.length; i++) {
	    if (line[i].isEmpty())
		continue;
 	    
	    if (Character.isAlphabetic(line[i].charAt(0))) {
		if (line[i].equals("Add")) {
		    return;
		}

		combine += line[i] + " " ;
	    }
	    
	    if (Character.isDigit(line[i].charAt(0))) {
		if (printAgain == true) {
		    dataToAdd.add(combine);
		    dataToAdd.add(line[i]);
		    printAgain = false;
		} else {
		    dataToAdd.add(line[i]);
		}    
	    }

	    if (i == line.length-1 && Character.isAlphabetic(line[i].charAt(0)))
		dataToAdd.add(combine);

	} // end for
	
	incomeStatement.add(dataToAdd);
    }
    
    /**
     * Scrapes each HTML line by removing unnecessary characters and only
     * returning raw data.
     *
     * @param line a line of HTML code to be scraped.
     * @return raw data extracted from the line of HTML code.
     */
    private String scrapeExcessHTML(String line) {
	String lineToModify = line;
	while (lineToModify.contains("<") || line.contains(">")) {
	    int firstIndex = lineToModify.indexOf("<");
	    int nextIndex = lineToModify.indexOf(">")+1;
	    if (firstIndex == -1 || nextIndex == -1)
		break;
	    
	    String lineToRemove = lineToModify.substring(firstIndex, nextIndex);
	    lineToModify = lineToModify.replace(lineToRemove, " ");
	    lineToModify.trim();
	}
	
	while(lineToModify.contains("&nbsp;")) {
	    lineToModify = lineToModify.replaceAll("&nbsp", "").replaceAll
		(";", "");
	}
	return lineToModify.trim();
    }

    private void printBS() {
	
	for (int i = 0; i < incomeStatement.size(); i++) {
	    for (int j = 0; j < incomeStatement.get(i).size(); j++) {
		System.out.print(incomeStatement.get(i).get(j)+ " ");
		
	    } if (i != incomeStatement.size() -1 &&
		  Character.isAlphabetic(incomeStatement.get(i+1).get(0).charAt(0)))
		  System.out.println();
	}
	
    }
}
