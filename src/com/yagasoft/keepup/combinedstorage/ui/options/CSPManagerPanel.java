/* 
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.options/CSPManagerPanel.java
 * 
 *			Modified: 11-Mar-2014 (21:14:14)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.options;


import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * The Class CSPManagerPanel.
 */
public class CSPManagerPanel extends JFrame
{
	
	private static final long	serialVersionUID	= -4884612100609411713L;
	/** Content pane. */
	private JPanel				contentPane;
	
	/**
	 * Create the frame.
	 */
	public CSPManagerPanel()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}
	
}
