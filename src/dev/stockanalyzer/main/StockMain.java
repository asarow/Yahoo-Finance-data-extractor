package dev.stockanalyzer.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import dev.stockanalyzer.gui.StockGUI;
import dev.stockanalyzer.pulldata.DataExtractor;
import dev.stockanalyzer.pulldata.FinancialsExtractor;

/**
 * The main class which creates a graphical user interface object, an object
 * to retrieve stock data (DataExtractor), and an object to retrieve financial
 * statement information (FinancialsExtractor). A model view controller object
 * takes all the objects as parameters.
 *
 * @author Amandeep Sarow
 */
public class StockMain {
    public static void main(String[] args) {
	StockGUI gui = new StockGUI();
	DataExtractor extractor = new DataExtractor();
	FinancialsExtractor financials = new FinancialsExtractor();
	
	StockModelViewController mvc = new StockModelViewController
	    (extractor, gui, financials);	
    }
}
