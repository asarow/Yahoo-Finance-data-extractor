package dev.stockanalyzer.pulldata;

import java.io.*;

import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Exports financial statement information in an excel-friendly format. This
 * class uses the Apache POI library.
 *
 * @author Amandeep Sarow
 */
public class DataExporter {
    
    public DataExporter() {
	
    }
    
    /**
     * Exports all three financial statements into an excel file which
     * is then saved onto the user's computer with the provided save
     * path.
     *
     * @param savePath the save path provided by the user.
     * @param data A two-dimensional list containing all the financial 
     *             statement information.
     * @param statementType specifies whether the data should be annual
     *                      or quarterly.
     */
    public void exportData(String savePath, ArrayList<ArrayList<String>> 
			   data, String statementType) {

	FileOutputStream fileOut;
	HSSFWorkbook wb = new HSSFWorkbook();
	HSSFSheet incomeStatement = wb.createSheet("Income Statement");
	HSSFSheet balanceSheet = wb.createSheet("Balance Sheet");
	HSSFSheet cashFlows = wb.createSheet("Statement of Cash Flows");
	Row row = null;
	Cell cell = null;
	int columnLimit = 0;

	HSSFSheet[] financialStatementsSheets = {incomeStatement, balanceSheet,
						cashFlows};

	// Sets the number of columns depending on the type of statement
	if (statementType.equals("annual")) {
	    columnLimit = 4;
	} else if (statementType.equals("quarterly")) {
	    columnLimit = 5;
	}

	// For debugging purposes (check time to export)
	//System.out.println("Printing...");
	
	int counter = 0; // An indexer for the rows
	int columnCounter = 0;
	for (int i = 0; i < data.size(); i++) {
	    for (int j = 0; j < data.get(i).size(); j++) {
		/* If the number of columns reaches the column limit,
		   create a new row with an index */
		if (j % columnLimit == 0) {
		    columnCounter = 0;
		    row = financialStatementsSheets[i].createRow(counter);
		    counter++;
		}
		
		// Create a cell in the specified the column index
		cell = row.createCell(columnCounter);
		columnCounter++;
		cell.setCellValue(data.get(i).get(j));
	    }
	    // Reset for each financial statement
	    counter = 0;
	    columnCounter = 0;
	}
	
	// Automatically resizes the columns based on the length of data
	for (int i = 0; i < financialStatementsSheets.length; i++) {
	    for (int j = 0; j < columnLimit; j++) {
		financialStatementsSheets[i].autoSizeColumn(j);
	    }
	}
	
	// Output the excel file to the supplied save path
	try {
	    fileOut = new FileOutputStream(savePath);
	    wb.write(fileOut);
	    fileOut.close();	 
	    System.out.println("Finished");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
}
