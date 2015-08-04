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
	setTitle("Yahoo! Finance Data Extractor");
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
	
	if (getSelectedButton().equals("annual")) {
	    numOfPeriods = 3;
	} else if (getSelectedButton().equals("quarterly")) {
	    numOfPeriods = 4;
	}

	activeFrame = true;

	backgroundFrame.setVisible(true);
       	backgroundFrame.setLocation(bgX, bgY);
	fileButton.setEnabled(true);
	exportButton.setEnabled(true);

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

    public void findSavePath(ActionListener fileButtonListener) {
	fileButton.addActionListener(fileButtonListener);
    }

    public void exportFinancialData(ActionListener exportButtonListener) {
	exportButton.addActionListener(exportButtonListener);
    }

    public ArrayList<ArrayList<String>> getFinancialStatementsData() {
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

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

	return data;
    }

   

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

    
    public String getSelectedButton() {
	if (annual.isSelected()) {
	    return "annual";
	} else {
	    return "quarterly";
	}
    }
    
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
	/* Initialize labels for each type of statement */

	isPanel = new JPanel(new GridLayout(0, numOfPeriods+1));
	bsPanel = new JPanel(new GridLayout(0, numOfPeriods+1));
	cfPanel = new JPanel(new GridLayout(0, numOfPeriods+1));

	pane.addTab("Income Statement", null, isPanel);
	pane.addTab("Balance Sheet", null, bsPanel);
	pane.addTab("Cash Flows", null, cfPanel);

	backgroundFrame.add(pane);
	
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

    private void addEastComponents() {
	exportButton = new JButton("Export");
	fileButton = new JButton("Save As");
	eastPanel.add(fileButton);
	eastPanel.add(exportButton);
	fileButton.setEnabled(false);
	exportButton.setEnabled(false);
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
			for (int l = 0; l < numOfPeriods; l++) {
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


