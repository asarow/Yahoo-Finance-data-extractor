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

public class StockMain {
	private static List<String[]> stockTickers = new ArrayList<String[]>();
	
	public static void retrieveStockDataFromFile() throws IOException {
		File file = new File("Res/Stock_Data_SP500.txt");
		LineNumberReader read; 
		
		try {
			read = new LineNumberReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("The file containing stock tickers was not read properly.");
			e.printStackTrace();
			return;
		}
		
		while(true) {
			try {
				String line = read.readLine();
				if (line == null) 
					break;
				stockTickers.add(line.split(" " ));
			} catch (IOException e) {
				System.out.println("Failure reading a ticker. Continuing...");
				e.printStackTrace();
				continue;
			}
		} read.close(); // END WHILE LOOP
	}
	
	public static List<String[]> getStockTickers() {
		return stockTickers;
	}

	public static void main(String[] args) {
		StockGUI gui = new StockGUI();
		DataExtractor extractor = new DataExtractor();
		FinancialsExtractor financials = new FinancialsExtractor();
		
		StockModelViewController mvc = new StockModelViewController(extractor, gui, financials);
		
	}
}
