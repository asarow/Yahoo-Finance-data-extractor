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

public class StockGUI extends JFrame {
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel(new GridLayout(DataExtractor.getDataSize(), 1));
	private JPanel southPanel = new JPanel();
	private JTextField tickerField = new JTextField("AAPL", 4);
	private JButton tickerButton, buildFinancialsButton;
	private JLabel[] stockLabels = new JLabel[DataExtractor.getDataSize()*2];
	
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
	
	public String getTicker() {
		return tickerField.getText();
	}
	
	public void displayStockData(String[] stockData) {
		for (int i = 0; i < stockLabels.length; i++) {
			if (i%2 != 0)
			stockLabels[i].setText(stockData[i/2]);
		}
	}
	
	public void displayFinancials() {
		
	}
	private void addNorthComponents() {
		tickerButton = new JButton("Go!");
		buildFinancialsButton = new JButton("Build financials");
		northPanel.add(tickerField);
		northPanel.add(tickerButton);
		northPanel.add(buildFinancialsButton);

		
	}
	
	private void addCenterComponents() {
		String[] labels = {"Name: ", "" ,
				"Price: ", " ", "Change:", " " , "Market Cap:", " ",
				"EBITDA", " ", "PE Ratio" , " "};
		for (int i = 0; i < stockLabels.length; i++) {
			centerPanel.add(stockLabels[i] = new JLabel(labels[i]));
		}
		

	}
	
	private void addSouthComponents() {
		southPanel.add(new JLabel("Test"));
	}
	
	public void getStockData(ActionListener tickerButtonListener) {
		tickerButton.addActionListener(tickerButtonListener);
	}
	
	public void getFinancialData(ActionListener financialButtonListener) {
		buildFinancialsButton.addActionListener(financialButtonListener);
	}
	
/*
 * 	private void displayErrorMessage(String errorMessage) {
		warningLabel.setText(errorMessage);
	}
 */
	
}
