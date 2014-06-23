/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui/BackupPanel.java
 * 
 *			Modified: 12-Jun-2014 (23:22:00)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui;


import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.yagasoft.keepup.ui.browser.BrowserPanel;
import com.yagasoft.keepup.ui.browser.table.FileTable;


/**
 * The Class BackupPanel.
 */
public class BackupPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -8746598032427301425L;
	
	/** Browser panel. */
	protected BrowserPanel		browserPanel;
	
	/** Button panel. */
	protected JPanel			buttonPanel;
	
	/** Add all button. */
	protected JButton			addAllButton;
	
	/** Add selected button. */
	protected JButton			addSelectedButton;
	
	/** Remove selected button. */
	protected JButton			removeSelectedButton;
	
	/** Remove all button. */
	protected JButton			removeAllButton;
	
	/** Table. */
	protected FileTable			table;
	
	/**
	 * Create the panel.
	 *
	 * @param browserPanel
	 *            Browser panel.
	 * @param table
	 *            Table.
	 */
	public BackupPanel(BrowserPanel browserPanel, FileTable table)
	{
		this.browserPanel = browserPanel;
		this.table = table;
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("", "[50px]",
				"[grow 9999,shrink 0,fill][25px][25px][25px][25px][grow 9999,shrink 0,fill]"));
		
		initWindow();
	}
	
	/**
	 * Construct the panels in the main window.
	 */
	private void initWindow()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(browserPanel);
		
		addAllButton = new JButton(">>");
		buttonPanel.add(addAllButton, "cell 0 1,alignx center,aligny center");
		addSelectedButton = new JButton(">");
		buttonPanel.add(addSelectedButton, "cell 0 2,alignx center,aligny center");
		removeSelectedButton = new JButton("<");
		buttonPanel.add(removeSelectedButton, "cell 0 3,alignx center,aligny center");
		removeAllButton = new JButton("<<");
		buttonPanel.add(removeAllButton, "cell 0 4,alignx center,aligny center");
		
		add(buttonPanel);
		
		add(table);
	}
	
	/**
	 * Adds the button listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void addButtonListener(ActionListener listener)
	{
		addAllButton.addActionListener(listener);
		addSelectedButton.addActionListener(listener);
		removeSelectedButton.addActionListener(listener);
		removeAllButton.addActionListener(listener);
	}
	
	/**
	 * Removes the button listener.
	 *
	 * @param listener
	 *            Listener.
	 */
	public void removeButtonListener(ActionListener listener)
	{
		addAllButton.removeActionListener(listener);
		addSelectedButton.removeActionListener(listener);
		removeSelectedButton.removeActionListener(listener);
		removeAllButton.removeActionListener(listener);
	}
	
}
