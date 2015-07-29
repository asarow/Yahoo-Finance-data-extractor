package dev.stockanalyzer.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dev.stockanalyzer.gui.StockGUI;
import dev.stockanalyzer.pulldata.DataExtractor;
import dev.stockanalyzer.pulldata.FinancialsExtractor;

/** 
 * This class is a model view controller which is responsible for acting as a
 * mediator between the data extraction classes, and the graphical user 
 * interface.
 * 
 * @author Amandeep Sarow
 */
public class StockModelViewController {
    private DataExtractor extractor;
    private StockGUI gui;
    private FinancialsExtractor financials;

    StockModelViewController(DataExtractor extractor, StockGUI gui, 
			     FinancialsExtractor financials) {
	this.extractor = extractor;
	this.gui = gui;
	this.financials = financials;
	
	this.gui.getStockData(new StockButtonListener());
	this.gui.getFinancialData(new FinancialDataButtonListener());
    }
     
    class StockButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String stockTicker = gui.getTicker();
	    String[] stockData = extractor.
		pullTickerData(stockTicker);
	    gui.displayStockData(stockData);
	}
    }
    
    class FinancialDataButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String stockTicker = gui.getTicker();
	    financials.pullFinancialData(stockTicker);
	}
    }
    
	
	
	
	
}
