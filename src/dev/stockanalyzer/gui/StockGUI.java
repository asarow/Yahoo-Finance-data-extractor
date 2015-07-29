package dev.stockanalyzer.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
    private JTextField tickerField = new JTextField("AAPL", 4);
    private JButton tickerButton, buildFinancialsButton;
    private JLabel[] stockLabels = new JLabel[DataExtractor.getDataSize()*2];
    
    /** Public constructor for creation of the GUI */
    public StockGUI() {		
	setTitle("Stock Analyzer");
	setSize(300,300);
	setLocationRelativeTo(null);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
	
	add(northPanel, BorderLayout.NORTH);
	add(centerPanel);
	add(southPanel, BorderLayout.SOUTH);
	addNorthComponents();
	addCenterComponents();
	addSouthComponents();
    }
    
    /** 
     * Getter for the stock ticker symbol input by the user.
     * @return the stock ticker provided as input by the user
     */
    public String getTicker() {
	return tickerField.getText();
    }
    
    /** 
     * Displays the stock labels and stock data. This method is called inside
     * the StockModelViewController class.
     * @param an array containing various stock information to be displayed
     */
    public void displayStockData(String[] stockData) {
	for (int i = 0; i < stockLabels.length; i++) {
            if (i%2 != 0)
		stockLabels[i].setText(stockData[i/2]);
	}
    }
    
    //TODO: Display financial statement information
    public void displayFinancials() {
	
    }
    
    /** 
     * Takes an ActionListener parameter from the model view controller and
     * updates the stock data for a given stock ticker when the 'GO!' button
     * is pressed.
     * @param an ActionListener for the GO! button from the mvc
     */
    public void getStockData(ActionListener tickerButtonListener) {
	tickerButton.addActionListener(tickerButtonListener);
    }
    
    /**
     * Takes an ActionListener parameter from the model view controller and
     * updates the GUI with financial statement information for a given stock
     * ticker.
     * @param an ActionListener for the Build Financials button
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
    
    /** Adds GUI elements to the center panel */
    private void addCenterComponents() {
	String[] labels = {"Name: ", "" ,
			   "Price: ", " ", "Change:", " " , "Market Cap:", " ",
			   "EBITDA", " ", "PE Ratio" , " "};
	for (int i = 0; i < stockLabels.length; i++) {
	    centerPanel.add(stockLabels[i] = new JLabel(labels[i]));
	}	
    }
    
    /** Test panel */
    private void addSouthComponents() {
	southPanel.add(new JLabel("Test"));
    }
}
