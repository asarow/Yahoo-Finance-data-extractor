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

public class FinancialsExtractor {
	private  List<String[]> incomeStatement = new ArrayList<String[]>();
	private  List<String[]> balanceSheet = new ArrayList<String[]>();
	private  List<String[]> cashFlowsStatement = new ArrayList<String[]>();
	private  String yahooAPIFinancialBegin = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20yahoo.finance.";
	private  String yahooAPIFinancialMid = "%20WHERE%20symbol%3D'";
	private  String yahooAPIFinancialEnd = "'&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private final String bs = "balancesheet", is = "incomestatement", cf = "cashflow";
	private final String[] urlModifier = {bs, is, cf};
	private String ticker;
	
	public FinancialsExtractor() {
		
	}

	public void pullFinancialData(String ticker) {
		this.ticker = ticker; //TODO: Change
		URL url;
	    InputStream istream = null;
	    BufferedReader br;
	    String line;
	    
	    for (int i = 0; i <  urlModifier.length; i++) {
		    try {
				url = new URL(yahooAPIFinancialBegin + urlModifier[i] + yahooAPIFinancialMid + ticker + yahooAPIFinancialEnd);
		        try {
		        	istream = url.openStream();  // throws an IOException
		        } catch (IOException io) {
		        	System.out.println("The URL failed to retrieve the stock ticker from the API");
		        	io.printStackTrace();
		        }
		        br = new BufferedReader(new InputStreamReader(istream));
			        while ((line = br.readLine()) != null) {
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
		    } catch (MalformedURLException mue) { //TODO: Check the necessities of these exceptions
		         mue.printStackTrace();
		    } catch (IOException ioe) {
		         ioe.printStackTrace();
		    } 
		    
	    }// end for
	}
	
	public List<String[]> getIncomeStatement() {
		return incomeStatement;
	}
	
	public List<String[]> getBalanceSheet() {
		return balanceSheet;
	}
	
	public List<String[]> getCashFlowsStatement() {
		return cashFlowsStatement;
	}
	
	private void pullIncomeStatement(String urlToPass) {
		int firstIndex = urlToPass.lastIndexOf("CDATA")+6;
		int lastIndex = urlToPass.lastIndexOf("]]");
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
					if (line.contains("Period Ending")) 
						startScraping = true;
						
					if (line.contains("Net Income Applicable To Common Shares"))
						startIncrement = true;
					
					if (startScraping == true) {
						String returned = scrapeExcessHTML(line);
						if (returned.length() > 0) {
							buildSheet(returned.split(" "));
						}
					}
					if (startIncrement == true)
						i++;
				}
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
	
	private void buildSheet(String[] line) {
		if (line.length == 0) {
			return;
		} else if (line.length == 1) {
			System.out.println(line[0]);
			return;
		}
		
		String combine = "";

		for (int i = 0; i < line.length; i++) {
			if (line[i].isEmpty())
				continue;
			
			if (Character.isAlphabetic(line[i].charAt(0))) 
				combine += line[i] + " ";

			if (Character.isDigit(line[i].charAt(0))) {
				System.out.println(line[i]);
			}
			
		} 
	}
	
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
			lineToModify = lineToModify.replaceAll("&nbsp", "").replaceAll(";", "");
		}
			return lineToModify.trim();
	}
}
