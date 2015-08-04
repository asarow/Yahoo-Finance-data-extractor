package dev.stockanalyzer.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;

import java.util.*;

import java.io.*;

import dev.stockanalyzer.pulldata.DataExtractor;

/** 
 * This class builds the GUI for the stock analyzer and creates getters
 * for use in the model view controller class.
 *
 * @author Amandeep Sarow
 */
public class StockGUI extends JFrame {
    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel(new GridLayout(DataExtractor
							   .getDataSize(), 1));
    private JPanel southPanel = new JPanel();
    private JPanel eastPanel = new JPanel();
    private JPanel isPanel, bsPanel, cfPanel;
    private JTextField tickerField = new JTextField("AAPL", 4);
    private JTabbedPane pane;
    private JRadioButton annual, quarter;
    private JButton tickerButton, buildFinancialsButton, exportButton, 
	            fileButton;
    private JLabel[] stockLabels = new JLabel[DataExtractor.getDataSize()*2];
    private JLabel[] isLabels = new JLabel[150];
    private JLabel[] bsLabels = new JLabel[200];
    private JLabel[] cfLabels = new JLabel[150];
    private JFrame backgroundFrame;
    private File file;
    private int numOfPeriods = 4;
    private boolean activeFrame = false;
    private final int x = 450, y = 300, bgX = 850, bgY = 300;
    private final String[] headers = {"Operating Expenses", 
				      "Income from Continuing Operations",
				      "Non-recurring Events", "Assets", 
                                      "Current Assets", "Liabilities",
                                      "Current Liabilities", "Stockholders'" +
                                      " Equity", "Operating Activities, Cash" +
                                      " Flows Provided By or Used In", 
                                      "Investing Activities, Cash Flows" +
                                      " Provided By or Used In", "Financing" +
                                      " Activities, Cash Flows Provided By" +
                                      " or Used In"};
    
    /** Public constructor for creation of the GUI */
    public StockGUI() {		
	setTitle("Financial Data Extractor v1.0");
	setSize(x, y);
	setLocation(400,300);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
	
	add(northPanel, BorderLayout.NORTH);
	add(centerPanel);
	add(eastPanel, BorderLayout.EAST);
	addNorthComponents();
	addCenterComponents();
	addEastComponents();
    }
    

    /** 
     * Displays the stock labels and stock data. This method is called inside
     * the StockModelViewController class.
     *
     * @param stockData an array containing various stock information to be 
     *                  displayed.
     */
    public void displayStockData(String[] stockData) {
	for (int i = 0; i < stockLabels.length; i++) {
            if (i%2 != 0)
		stockLabels[i].setText(stockData[i/2]);
	}
    }

    /**
     * Displays the income statement, balance sheet, and statement of cash
     * flows in a new window.
     */
    public void displayFinancialStatementsData
	(ArrayList<ArrayList<String>> is, ArrayList<ArrayList<String>> bs, 
	 ArrayList<ArrayList<String>> cf) 
    {
	
	/* numOfPeriods represents the number of columns needed to 
	   display the financial data. */
	if (getSelectedButton().equals("annual")) {
	    numOfPeriods = 3;
	} else if (getSelectedButton().equals("quarterly")) {
	    numOfPeriods = 4;
	}

	activeFrame = true;

	backgroundFrame.setVisible(true);
       	backgroundFrame.setLocation(bgX, bgY);

	/* The ability to save and export is only available if the
	   user has built the financial statements */
	fileButton.setEnabled(true);
	exportButton.setEnabled(true);

	// Methods to display the data
	displayIncomeStatement(is);
	displayBalanceSheet(bs);
	displayCashFlows(cf);
    }

    /**
     * Checks if an existing financial statement window is currently
     * running. 
     *
     * @return true if a GUI is currently open, false otherwise.
     */
    public boolean isFrameActive() {
	return activeFrame;
    }
    
    /**
     * Closes the existing financial statement window.
     */
    public void closeRunningFrame() {
	backgroundFrame.dispatchEvent(new WindowEvent(backgroundFrame, 
				      WindowEvent.WINDOW_CLOSING));
	activeFrame = false;
    }

    /**
     * Takes an ActionListener parameter from the model view controller and
     * updates the GUI with financial statement information for a given stock
     * ticker.
     *
     * @param financialButtonListener an ActionListener for the Build 
     *                                Financials button from the mvc.
     */
    public void getFinancialData(ActionListener financialButtonListener) {
	buildFinancialsButton.addActionListener(financialButtonListener);
    }

    /**
     * Takes an ActionListener paramter from the model view controller and
     * stores the user-selected save path from the GUI.
     *
     *@param fileButtonListener an ActonListener for the Save As button 
     *                          from the mvc.
     */
    public void findSavePath(ActionListener fileButtonListener) {
	fileButton.addActionListener(fileButtonListener);
    }

    /**
     * Takes an ActionListener paramter from the model view controller and
     * stores the user-selected exports the financial data to the save path.
     *
     *@param exportButtonListener an ActonListener for the export button 
     *                            from the mvc.
     */
    public void exportFinancialData(ActionListener exportButtonListener) {
	exportButton.addActionListener(exportButtonListener);
    }

    /** 
     * Takes an ActionListener parameter from the model view controller and
     * updates the stock data for a given stock ticker when the 'GO!' button
     * is pressed.
     *
     * @param tickerButtonListener an ActionListener for the GO! button from 
     *                              the mvc.
     */
    public void getStockData(ActionListener tickerButtonListener) {
	tickerButton.addActionListener(tickerButtonListener);
    }

    /** 
     * Getter for the stock ticker symbol input by the user.
     *
     * @return the stock ticker provided as input by the user.
     */
    public String getTicker() {
	return tickerField.getText();
    }

    /**
     * Getter which retrieves the financial statement data directly from the
     * GUI.
     *
     * @return data from the income statement, balance sheet, and statement
     *         of cash flows.
     */
    public ArrayList<ArrayList<String>> getFinancialStatementsData() {
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

	// empty placeholders 
	ArrayList<String> isHolder = new ArrayList<String>();
	ArrayList<String> bsHolder = new ArrayList<String>();
	ArrayList<String> cfHolder = new ArrayList<String>();

	data.add(isHolder);
	data.add(bsHolder);
	data.add(cfHolder);

	for (int i = 0; i < isLabels.length; i++) {
	    data.get(0).add(isLabels[i].getText());
	}

	for (int i = 0; i < bsLabels.length; i++) {
	    data.get(1).add(bsLabels[i].getText());
	}

	for (int i = 0; i < cfLabels.length; i++) {
	    data.get(2).add(cfLabels[i].getText());
	}

	/* Since the data has been slightly modified for viewing purposes
	   (i.e. a new line after each financial statement header), for 
	   exporting to excel, the structure of the data needs to be
	   consistent which is why the data is retrieved from the GUI
	   instead of the FinancialsExtractor class which contains the
	   raw data for the financial statements */

	return data;
    }
    
    /**
     * Allows the user to select a save-path for exporting financial 
     * statements. This method is called by the model view controller.
     *
     * @return a String representing the save-path.
     */
    public String storeSavePath() {
	JFileChooser chooser = new JFileChooser(); 
	chooser.setSelectedFile(new File(getTicker() + getSelectedButton() +
					 ".xls"));
	int returnVal = chooser.showSaveDialog(this);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    return chooser.getSelectedFile().getAbsolutePath();
	} else {
	    System.out.println("Failed selecting a save path");
	    return null;
	}
    }

    /**
     * Returns the type of financial statement option selected.
     *
     * @return the type of financial statement (quarterly or annual).
     */
    public String getSelectedButton() {
	if (annual.isSelected()) {
	    return "annual";
	} else {
	    return "quarterly";
	}
    }
    
    /**
     * Builds the GUI for the financial statements each time the user
     * chooses to.
     */
    public void loadBackgroundFrame(String periodType) {
        backgroundFrame = new JFrame("Financial Statements");
	backgroundFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backgroundFrame.setSize(1000,600);
	backgroundFrame.setResizable(false);

	pane = new JTabbedPane();

	if (periodType.equals("annual")) {
	    numOfPeriods = 3;
	} else if (periodType.equals("quarterly")) {
	    numOfPeriods = 4;
	}

	/* The number of columns is dependent on the type of 
	   financial statement (quarterly or annual) */
	isPanel = new JPanel(new GridLayout(0, numOfPeriods+1));
	bsPanel = new JPanel(new GridLayout(0, numOfPeriods+1));
	cfPanel = new JPanel(new GridLayout(0, numOfPeriods+1));

	pane.addTab("Income Statement", null, isPanel);
	pane.addTab("Balance Sheet", null, bsPanel);
	pane.addTab("Cash Flows", null, cfPanel);

	backgroundFrame.add(pane);
	
	/* Initialize labels for each type of statement */

        for (int i = 0; i < isLabels.length; i++) {
	    isPanel.add(isLabels[i] = new JLabel());
	}

	for (int i = 0; i < bsLabels.length; i++) {
	    bsPanel.add(bsLabels[i] = new JLabel());
	}

	for (int i = 0; i < cfLabels.length; i++) {
	    cfPanel.add(cfLabels[i] = new JLabel());
	}
    }

    /* Begin private methods */

    /** Adds GUI elements to the north JPanel bar */
    private void addNorthComponents() {
	ButtonGroup group = new ButtonGroup();
	annual = new JRadioButton("Annual");
	quarter = new JRadioButton("Quarter");
	tickerButton = new JButton("Go!");
	buildFinancialsButton = new JButton("Build financials");
	northPanel.add(tickerField);
	northPanel.add(tickerButton);
	northPanel.add(buildFinancialsButton);
	group.add(annual);
	group.add(quarter);
	northPanel.add(annual);
	northPanel.add(quarter);
	annual.setSelected(true);
	
    }
    
    /** Adds GUI elements to the center panel. */
    private void addCenterComponents() {
	String[] labels = {"Name: ", "" ,
			   "Price: ", " ", "Change:", " " , "Market Cap:", " ",
			   "EBITDA", " ", "PE Ratio" , " "};
	for (int i = 0; i < stockLabels.length; i++) {
	    centerPanel.add(stockLabels[i] = new JLabel(labels[i]));
	}	
    }

    /** Adds GUI elements to the east panel */
    private void addEastComponents() {
	exportButton = new JButton("Export");
	fileButton = new JButton("Save As");
	eastPanel.add(fileButton);
	eastPanel.add(exportButton);
	fileButton.setEnabled(false);
	exportButton.setEnabled(false);
    }

    /** Updates the GUI with income statement data */
    private void displayIncomeStatement(ArrayList<ArrayList<String>> is) {
	// Remove any empty Strings
	for (int i = 0; i < is.size(); i++) {
	    for (int j = 0; j < is.get(i).size(); j++) {
		if (is.get(i).get(j).isEmpty())
		    is.get(i).remove(j);
	    }
	}
	
	int counter = 0;
	for (int i = 0; i < is.size(); i++) {
	    for (int j = 0; j < is.get(i).size(); j++) {
		String label = is.get(i).get(j);
		for (int k = 0; k < headers.length; k++) {
		    if (label.trim().equals(headers[k])) {
			for (int l = 0; l < numOfPeriods; l++) {
			    // Set the labels to empty for each header
			    is.get(i).add(j+1, " ");
			}
		    }
		}
		
		isLabels[counter].setText(is.get(i).get(j));
		counter++;
	    }
	}
    }

    /** Updates the GUI with balance sheet data */
    private void displayBalanceSheet(ArrayList<ArrayList<String>> bs) {
	for (int i = 0; i < bs.size(); i++) {
	    for (int j = 0; j < bs.get(i).size(); j++) {
		if (bs.get(i).get(j).isEmpty()) 
		    bs.get(i).remove(j);
	    }
	}

	int counter = 0;
	for (int i = 0; i < bs.size(); i++) {
	    for (int j = 0; j < bs.get(i).size(); j++) {
		String label = bs.get(i).get(j);
		for (int k = 0; k < headers.length; k++) {
		    if (label.trim().equals(headers[k])) {
			for (int l = 0; l < numOfPeriods; l++) {
			    bs.get(i).add(j+1, " ");
			}
		    }
		}
		
		bsLabels[counter].setText(bs.get(i).get(j));
		counter++;
	    }
	}
    }

    /** Updates the GUI with cash flow data */
    private void displayCashFlows(ArrayList<ArrayList<String>> cf) {
	for (int i = 0; i < cf.size(); i++) {
	    for (int j = 0; j < cf.get(i).size(); j++) {
		if (cf.get(i).get(j).isEmpty())
		    cf.get(i).remove(j);
	    }
	}

	int counter = 0;
	for (int i = 0; i < cf.size(); i++) {
	    for (int j = 0; j < cf.get(i).size(); j++) {
		String label = cf.get(i).get(j);
		for (int k = 0; k < headers.length; k++) {
		    if (label.trim().equals(headers[k])) {
			for (int l = 0; l < numOfPeriods; l++) {
			    cf.get(i).add(j+1, " ");
			}
		    }
		}
		
		cfLabels[counter].setText(cf.get(i).get(j));
		counter++;
	    }
	}
    }
}


