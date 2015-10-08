package dev.stockanalyzer.pulldata;

import YahooFinanceYQLWrapper.YQLWrapper;
import java.util.ArrayList;


/**
 * Pulls financial statement information from the Yahoo! YQL/API and stores the
 * data.
 *
 * @author Amandeep Sarow
 */
public class FinancialsExtractor {	
    public FinancialsExtractor() {
  
    }  
    /** 
     * Getter which returns the income statement for a given ticker. 
     *
     * @return an arrayList containing the income statement information.
     */
    public ArrayList<ArrayList<String>> getIncomeStatement(String stockTicker, String periodType) {
	return YQLWrapper.incomeStatement(stockTicker, periodType);
    }
    
    /** 
     * Getter which returns the balance sheet for a given ticker.
     *
     * @return an arrayList containing the balance sheet information.
     */
    public ArrayList<ArrayList<String>> getBalanceSheet(String stockTicker, String periodType) {
	return YQLWrapper.balanceSheet(stockTicker, periodType);
    }
    
    /**
     * Getter which returns the statement of cash flows for a given ticker.
     *
     * @return an arrayList containing the statement of cash flows information.
     */
    public ArrayList<ArrayList<String>> getCashFlowsStatement(String stockTicker, String periodType) {
	return YQLWrapper.statementOfCashFlows(stockTicker, periodType);
    }
}
