package dev.stockanalyzer.main;

import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dev.stockanalyzer.gui.StockGUI;
import dev.stockanalyzer.pulldata.DataExtractor;
import dev.stockanalyzer.pulldata.FinancialsExtractor;
import dev.stockanalyzer.pulldata.DataExporter;

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
    private DataExporter exporter;
    private String savePath;

    StockModelViewController(DataExtractor extractor, StockGUI gui, 
			     FinancialsExtractor financials, 
			     DataExporter exporter) {
	this.extractor = extractor;
	this.gui = gui;
	this.financials = financials;
	this.exporter = exporter;
	
	this.gui.getStockData(new StockButtonListener());
	this.gui.getFinancialData(new FinancialDataButtonListener());
	this.gui.findSavePath(new FileButtonListener());
	this.gui.exportFinancialData(new ExportButtonListener());
	
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
	    String statementPeriod = gui.getSelectedButton();

	    if (gui.isFrameActive() == true)
		gui.closeRunningFrame();

	    gui.loadBackgroundFrame(statementPeriod);
	    
	    ArrayList<ArrayList<String>> incomeStatement;
	    ArrayList<ArrayList<String>> balanceSheet;
	    ArrayList<ArrayList<String>> cashFlowsStatement;

	    incomeStatement = financials.getIncomeStatement(stockTicker, statementPeriod);
	    balanceSheet = financials.getBalanceSheet(stockTicker, statementPeriod);
	    cashFlowsStatement = financials.getCashFlowsStatement(stockTicker, statementPeriod);

	    gui.displayFinancialStatementsData(incomeStatement, balanceSheet,
					       cashFlowsStatement);
	}
    }	

    class FileButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    savePath = gui.storeSavePath();
	    System.out.println(savePath);
	}
    }

    class ExportButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
	    String statementType = gui.getSelectedButton();
	    exporter.exportData(savePath, gui.getFinancialStatementsData(), 
				statementType);
	}
    }
}
