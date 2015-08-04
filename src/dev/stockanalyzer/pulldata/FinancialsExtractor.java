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
    private ArrayList<ArrayList<String>> incomeStatement = new ArrayList
	<ArrayList<String>>();
    private ArrayList<ArrayList<String>> balanceSheet = new ArrayList
	<ArrayList<String>>();
    private ArrayList<ArrayList<String>> cashFlowsStatement = new 
	ArrayList<ArrayList<String>>();
    private  String yahooAPIFinancialBegin = "https://query.yahooapis.com/v1/"
        + "public/yql?q=SELECT%20*%20FROM%20yahoo.finance.";
    private  String yahooAPIFinancialMid = "%20WHERE%20symbol%3D'";
    private  String yahooAPIFinancialEnd = "'&diagnostics=true&env=store%3A%2F%"
        + "2Fdatatables.org%2Falltableswithkeys";
    private final String bs = "balancesheet", is = "incomestatement", 
	cf = "cashflow";
    private final String[] urlModifier = {bs, is, cf};
    private String ticker, periodType;
    private boolean print = false;
	
    public FinancialsExtractor() {
  
    }
    
    /**
     * Pulls financial statement information from the Yahoo! YQL/API. The URL
     * used within this method does not return the information, rather it 
     * returns another URL to the specified financial statement.
     * 
     * @param ticker the stock ticker input by the user.
     */
    public void pullFinancialData(String ticker, String periodType) {
	this.ticker = ticker; //TODO: Change how the ticker is stored.
	this.periodType = periodType;
	URL url;
	InputStream istream = null;
	BufferedReader br;
	String line;
	
	incomeStatement.clear();
	balanceSheet.clear();
	cashFlowsStatement.clear();

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
    public ArrayList<ArrayList<String>> getBalanceSheet() {
	return balanceSheet;
    }
    
    /**
     * Getter which returns the statement of cash flows for a given ticker.
     *
     * @return an array containing the statement of cash flows information.
     */
    public ArrayList<ArrayList<String>> getCashFlowsStatement() {
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

	// The URL must be modified depending on the type of statement
	if (periodType.equals("annual")) {
	    incomeStatementURL = incomeStatementURL.replace("quarterly",
							    "annual");
	}
	
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
		/* All comments below apply to pullBalanceSheet() and
		   pullCashFlows(); */

		/* The BufferedReader returns each line of the HTML page.
		   Most lines are not necessary as they do not contain
		   any of the financial data. Lines that do contain financial
		   data are passed to a HTML scraper. Certain checks are in
		   place to specify when to start storing lines of HTML and
		   when to stop. */

		while((line = br.readLine())!= null && 
		      (periodType.equals("quarterly") && i != 17) ||
		      (periodType.equals("annual") && i != 14)) {

		    /* Start scraping the HTML at this point */ 
		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    /* There is a fixed number of lines to be passed after
		       this line and the startIncrement boolean variable
		       specifies when to start counting the number of lines*/
		    if (line.contains("Net Income Applicable To Common Shares"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			String returned = scrapeExcessHTML(line);
			if (returned.length() > 0) {
			    //  buildSheet() stores the necessary data
			    buildSheet(returned.split(" "), is);
			}
		    }
		    
		    /* A counter which keeps track of how many remaining lines
		       need to be scraped after "Net Income Applicable... 
		       Once this counter hits either 14 or 17 depending on
		       the financial statement type. No more lines of HTML
		       are stored. The HTML from here on does not contain
		       financial data */
		    if (startIncrement == true)
			i++;

		} // end while
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

	if (periodType.equals("annual")) {
	    balanceSheetURL = balanceSheetURL.replace("quarterly", "annual");
	}
	
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
		while((line = br.readLine())!= null && 
		      (periodType.equals("quarterly") && i != 17) ||
		      (periodType.equals("annual") && i != 14)) {
		    
		    if (line == null) {
			System.out.println("THIS IS NULL");
		    }

		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    if (line.contains("Net Tangible Assets"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			String returned = scrapeExcessHTML(line);
			if (returned.length() > 0) {
			    buildSheet(returned.split(" "), bs);
			}
		    }
		    if (startIncrement == true)
			i++;
		} // end while
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
	
	if (periodType.equals("annual")) {
	    cashFlowURL = cashFlowURL.replace("quarterly", "annual");
	}

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
		while((line = br.readLine())!= null &&
		      (periodType.equals("quarterly") && i != 17) ||
		      (periodType.equals("annual") && i != 14)) {
		    if (line.contains("Period Ending")) 
			startScraping = true;
		    
		    if (line.contains("Change In Cash and Cash Equivalents"))
			startIncrement = true;
		    
		    if (startScraping == true) {
			String returned = scrapeExcessHTML(line);
			if (returned.length() > 0) {
			    buildSheet(returned.split(" " ), cf);
			}
		    }
		    if (startIncrement == true)
			i++;
		} // end while
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
    private void buildSheet(String[] line, String statementType) {
	ArrayList<String> dataToAdd = new ArrayList<String>();

	/* This method scrapes each line of HTML code and only stores
	   important financial information. Since the data needs to
	   be stored in a specific format, there are many checks in
	   place to ensure this format is met */
	
	// Ignore empty lines
	if (line.length == 0) 
	    return;

	/* Some financial data is not available and thus is represented by
	   a small dash ("-") only. In some rare cases, only a single
	   number is available (new public company). */
	if (line.length == 1 || line[0].equals("-")) {
	    dataToAdd.add(line[0]);
	    if (statementType.equals(is)) {
		incomeStatement.add(dataToAdd);
	    } else if (statementType.equals(bs)) {
		balanceSheet.add(dataToAdd);
	    } else {
		cashFlowsStatement.add(dataToAdd);
	    }
	    return;
	}
	
	/* Some important financial statement headers are split and must
	   be recombined */
	String combine = "";
	boolean printAgain = true;
	boolean numeric = false;

	/* The below for statement represents line of HTML code that contains
	   multiple financial data in a single line */
	for (int i = 0; i < line.length; i++) {
	    if (line[i].isEmpty()) {
		continue;
	    }
 	    
	    /* If the data begins with a letter, it is likely a financial
	       statement header */
	    if (Character.isAlphabetic(line[i].charAt(0))) {

		/* The very first line contains the dates of the financial
		   periods which needs to be extracted carefully */
		if (line[i].equals("Add")) {
			boolean periodEnding = false;
			int counter = 0;
			String period = "";
			
			for (int j=0; j < line.length; j++) {
			    if (line[j].equals("Ending")) {
				periodEnding = true;
			    }
			    
			    if (periodEnding == true && !line[j].isEmpty() 
				&& (line[j].length() == 3 || 
				    line[j].length() == 4)) 
				{
				    counter++;
				    period += line[j] + " ";
				
				    if (counter == 3) {
					dataToAdd.add(period);
					counter = 0;
					period = "";
				    }	
				}//  end period ending if-block

			    if (j == line.length-1 && statementType.equals(bs))
				{
				    dataToAdd.add("Assets");
				    dataToAdd.add("Current Assets");
				}
			} // end inner for-loop	
			
			if (statementType.equals(is)) {
			    dataToAdd.add(0, "Income Statement");
			    incomeStatement.add(dataToAdd);
			} else if (statementType.equals(bs)) {
			    dataToAdd.add(0, "Balance Sheet");
			    balanceSheet.add(dataToAdd);
			} else {
			    dataToAdd.add(0, "Statement Of Cash Flows");
			    cashFlowsStatement.add(dataToAdd);
			}
			return;
		    }// end "add" if block
		    
			
		if (printAgain == false && numeric == true) {
		    printAgain = true;
		    numeric = false;
		}
		
		// Combine split financial statement header words 
		combine += line[i] + " " ;
		
		/* checkIfHeader() checks if the String combine matches
		   known financial headers */
		if (checkIfHeader(combine) == true) {
		    dataToAdd.add(combine);
		    combine = "";
	        }
		
	   
	    }// end main IF
	    
	    // If the financial data begins with a number
	    if (Character.isDigit(line[i].charAt(0)) ||
		Character.isDigit(line[i].charAt(1))) {

		numeric = true;
		if (printAgain == true) {
		    dataToAdd.add(combine);
		    combine = "";
		    dataToAdd.add(line[i]);
		    printAgain = false;
		} else {
		    dataToAdd.add(line[i]);
		}    
	    } //  end characterNumericCheck if-block

	    if (i == line.length-1 && Character.isAlphabetic(line[i].charAt(0)))
		dataToAdd.add(combine);

	} // end main for
	
	if (statementType.equals(is)) {
	    incomeStatement.add(dataToAdd);
	} else if (statementType.equals(bs)) {
	    balanceSheet.add(dataToAdd); 
	} else {
	    cashFlowsStatement.add(dataToAdd);
	}
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

    /** 
     * Compares known financial headers to the String parameter.
     *
     * @return true if there is a match, false otherwise.
     */
    private boolean checkIfHeader(String currentHeaderWord) {
	String[] headerWords = {"Assets", "Liabilities", "Stockholders' Equity",
		 "Operating Activities, Cash Flows Provided By or Used In",
	         "Investing Activities, Cash Flows Provided By or Used In",
	          "Financing Activities, Cash Flows Provided By or Used In"};
	
	for (int i = 0; i < headerWords.length; i++) {
	    if (currentHeaderWord.trim().equals(headerWords[i])) {
		return true;
	    }
	} return false;
    }


}
