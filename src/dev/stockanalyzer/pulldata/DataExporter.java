package dev.stockanalyzer.pulldata;

import java.io.*;

import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;


public class DataExporter {
    
    public DataExporter() {
	
    }

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

	if (statementType.equals("annual")) {
	    columnLimit = 4;
	} else if (statementType.equals("quarterly")) {
	    columnLimit = 5;
	}

	System.out.println("Printing...");
	
	int counter = 0;
	int columnCounter = 0;
	for (int i = 0; i < data.size(); i++) {
	    for (int j = 0; j < data.get(i).size(); j++) {
		if (j % columnLimit == 0) {
		    columnCounter = 0;
		    row = financialStatementsSheets[i].createRow(counter);
		    counter++;
		}
		
		cell = row.createCell(columnCounter);
		columnCounter++;
		cell.setCellValue(data.get(i).get(j));
	    }
	    counter = 0;
	    columnCounter = 0;
	}
	
	// works
	for (int i = 0; i < financialStatementsSheets.length; i++) {
	    financialStatementsSheets[i].autoSizeColumn(0);
	}

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
