/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/StatusBar.java
 * 
 *			Modified: 13-Apr-2014 (00:03:31)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.yagasoft.keepup.App;


public class StatusBar extends JPanel
{
	
	private static final long	serialVersionUID	= 899048339739870513L;
	
	private JLabel				labelFreeSpace;
	private JLabel				labelFreeSpaceValues;
	
	/**
	 * Create the panel.
	 */
	public StatusBar()
	{
		
		initGUI();
	}
	
	private void initGUI()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//
		labelFreeSpace = new JLabel("Free space: ");
		add(labelFreeSpace);
		//
		labelFreeSpaceValues = new JLabel("free space values");
		add(labelFreeSpaceValues);
	}
	
	public void updateFreeSpace(HashMap<String, Long> csps)
	{
		String text = "";
		long total = 0;
		
		for (String csp : csps.keySet())
		{
			text += csp + " => " + App.humanReadableSize(csps.get(csp)) + " | ";
			total += csps.get(csp);
		}
		
		text += "Total => " + App.humanReadableSize(total);
		
		labelFreeSpaceValues.setText(text);
	}
	
}
