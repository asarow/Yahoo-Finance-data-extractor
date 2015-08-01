package dev.stockanalyzer.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.util.*;

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
    private JPanel isPanel, bsPanel, cfPanel;
    private JTextField tickerField = new JTextField("AAPL", 4);
    private JTabbedPane pane;
    private JButton tickerButton, buildFinancialsButton;
    private JLabel[] stockLabels = new JLabel[DataExtractor.getDataSize()*2];
    private JLabel[] isLabels = new JLabel[150];
    private JLabel[] bsLabels = new JLabel[200];
    private JLabel[] cfLabels = new JLabel[150];
    private boolean activeFrame = false;
    private JFrame backgroundFrame;
    private final int bgX = 800, bgY = 300;
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
	setTitle("Yahoo! Finance Data Extractor");
	setSize(300,300);
	setLocation(500,300);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
	
	add(northPanel, BorderLayout.NORTH);
	add(centerPanel);
	add(southPanel, BorderLayout.SOUTH);
	addNorthComponents();
	addCenterComponents();
	addSouthComponents();

	loadBackgroundFrame();
    }
    
    /** 
     * Getter for the stock ticker symbol input by the user.
     *
     * @return the stock ticker provided as input by the user.
     */
    public String getTicker() {
	return tickerField.getText();
    }

    public boolean isFrameActive() {
	return activeFrame;
    }
    
    public void closeRunningFrame() {
	backgroundFrame.dispatchEvent(new WindowEvent(backgroundFrame, 
				      WindowEvent.WINDOW_CLOSING));
	activeFrame = false;
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

    public void displayFinancialStatementsData
	(ArrayList<ArrayList<String>> is, ArrayList<ArrayList<String>> bs, 
	 ArrayList<ArrayList<String>> cf) 
    {
	activeFrame = true;
	backgroundFrame.setVisible(true);
       	backgroundFrame.setLocation(bgX, bgY);

	/* Trim */
	//ignore

	displayIncomeStatement(is);
	displayBalanceSheet(bs);
	displayCashFlows(cf);

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
    
    /* Begin private methods */

    /** Adds GUI elements to the north JPanel bar */
    private void addNorthComponents() {
	tickerButton = new JButton("Go!");
	buildFinancialsButton = new JButton("Build financials");
	northPanel.add(tickerField);
	northPanel.add(tickerButton);
	northPanel.add(buildFinancialsButton);
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
    
    /** Test panel. */
    private void addSouthComponents() {
	southPanel.add(new JLabel("Test"));
    }

    private void loadBackgroundFrame() {
        backgroundFrame = new JFrame("Financial Statements");
	backgroundFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backgroundFrame.setSize(1000,600);
	backgroundFrame.setResizable(false);

	pane = new JTabbedPane();

	backgroundFrame.add(pane);

	isPanel = new JPanel(new GridLayout(0, 5));
	bsPanel = new JPanel(new GridLayout(0, 5));
	cfPanel = new JPanel(new GridLayout(0, 5));
	pane.addTab("Income Statement", null, isPanel);
	pane.addTab("Balance Sheet", null, bsPanel);
	pane.addTab("Cash Flows", null, cfPanel);

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

    private void displayIncomeStatement(ArrayList<ArrayList<String>> is) {
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
			for (int l = 0; l < 4; l++) {
			    is.get(i).add(j+1, " ");
			}
		    }
		}
		
		isLabels[counter].setText(is.get(i).get(j));
		counter++;
	    }
	}
    }

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
			for (int l = 0; l < 4; l++) {
			    bs.get(i).add(j+1, " ");
			}
		    }
		}
		
		bsLabels[counter].setText(bs.get(i).get(j));
		counter++;
	    }
	}
    }

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
			for (int l = 0; l < 4; l++) {
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


