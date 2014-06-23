/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.menu.panels.options/CSPPanel.java
 * 
 *			Modified: 23-Jun-2014 (21:17:17)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.menu.panels.options;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.yagasoft.keepup.CSPInfo;


/**
 * The Class CSPPanel.
 */
public class CSPPanel extends JPanel
{
	
	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -8799130122924454849L;
	
	/** Check box enable. */
	private JCheckBox			checkBoxEnable;
	
	/** Label cspname. */
	private JLabel				labelCspname;
	
	/** Button reset. */
	private JButton				buttonReset;
	
	/** Label. */
	private JLabel				label;
	
	/** Label id. */
	private JLabel				labelId;
	
	/** Text field. */
	private JTextField			textField;
	
	/** Csp info. */
	private CSPInfo				cspInfo;
	
	/**
	 * Create the panel.
	 *
	 * @param cspInfo
	 *            Csp info.
	 */
	public CSPPanel(CSPInfo cspInfo)
	{
		this.cspInfo = cspInfo;
		
		initGUI();
	}
	
	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 75, 150, 4, 17, 150, 65, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		//
		checkBoxEnable = new JCheckBox("Enable", cspInfo.isEnabled());
		GridBagConstraints checkBoxEnableGridBagConstraints = new GridBagConstraints();
		checkBoxEnableGridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		checkBoxEnableGridBagConstraints.insets = new Insets(0, 0, 0, 5);
		checkBoxEnableGridBagConstraints.gridx = 0;
		checkBoxEnableGridBagConstraints.gridy = 0;
		add(checkBoxEnable, checkBoxEnableGridBagConstraints);
		//
		labelCspname = new JLabel(cspInfo.getCspName());
		GridBagConstraints labelCspnameGridBagConstraints = new GridBagConstraints();
		labelCspnameGridBagConstraints.anchor = GridBagConstraints.WEST;
		labelCspnameGridBagConstraints.insets = new Insets(0, 0, 0, 5);
		labelCspnameGridBagConstraints.gridx = 1;
		labelCspnameGridBagConstraints.gridy = 0;
		add(labelCspname, labelCspnameGridBagConstraints);
		//
		label = new JLabel(",");
		GridBagConstraints labelGridBagConstraints = new GridBagConstraints();
		labelGridBagConstraints.anchor = GridBagConstraints.WEST;
		labelGridBagConstraints.insets = new Insets(0, 0, 0, 5);
		labelGridBagConstraints.gridx = 2;
		labelGridBagConstraints.gridy = 0;
		add(label, labelGridBagConstraints);
		//
		labelId = new JLabel("ID:");
		GridBagConstraints labelIdGridBagConstraints = new GridBagConstraints();
		labelIdGridBagConstraints.anchor = GridBagConstraints.WEST;
		labelIdGridBagConstraints.insets = new Insets(0, 0, 0, 5);
		labelIdGridBagConstraints.gridx = 3;
		labelIdGridBagConstraints.gridy = 0;
		add(labelId, labelIdGridBagConstraints);
		//
		textField = new JTextField(cspInfo.getUserId());
		GridBagConstraints textFieldGridBagConstraints = new GridBagConstraints();
		textFieldGridBagConstraints.anchor = GridBagConstraints.WEST;
		textFieldGridBagConstraints.insets = new Insets(0, 0, 0, 5);
		textFieldGridBagConstraints.gridx = 4;
		textFieldGridBagConstraints.gridy = 0;
		add(textField, textFieldGridBagConstraints);
		textField.setColumns(15);
		//
		buttonReset = new JButton("Reset");
		GridBagConstraints buttonResetGridBagConstraints = new GridBagConstraints();
		buttonResetGridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		buttonResetGridBagConstraints.gridx = 5;
		buttonResetGridBagConstraints.gridy = 0;
		add(buttonReset, buttonResetGridBagConstraints);
	}
	
	/**
	 * Sets the settings.
	 */
	public void setSettings()
	{
		cspInfo.setEnabled(checkBoxEnable.isSelected());
		cspInfo.setUserId(textField.getText());
	}
}
