/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.panels/SearchBar.java
 * 
 *			Modified: 23-Jun-2014 (16:40:32)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.panels;


import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * The Class SearchBar.
 */
public class SearchBar extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= 6758133652665617832L;
	
	/** Text field. */
	private JTextField			textField;
	
	/** Button search. */
	private JButton				buttonSearch;
	
	/**
	 * Instantiates a new search bar.
	 */
	public SearchBar()
	{
		initGUI();
	}
	
	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		textField = new JTextField();
		add(textField);
		
		buttonSearch = new JButton("Search");
		add(buttonSearch);
	}
	
	/**
	 * Gets the search text.
	 *
	 * @return the search text
	 */
	public String getSearchText()
	{
		return textField.getText();
	}
	
	/**
	 * Adds the search listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addSearchListener(ActionListener listener)
	{
		buttonSearch.addActionListener(listener);
		textField.addActionListener(listener);
	}
}
