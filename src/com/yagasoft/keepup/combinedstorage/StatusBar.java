
package com.yagasoft.keepup.combinedstorage;


import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.yagasoft.keepup.App;
import com.yagasoft.overcast.CSP;
import com.yagasoft.overcast.exception.OperationException;


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

	public void updateFreeSpace()
	{
		String text = "";
		long total = 0;

		for (CSP<?, ?, ?> csp : App.getCspsArray())
		{
			try
			{
				long freeSpace = csp.calculateRemoteFreeSpace();
				total += freeSpace;
				text += csp.getName() + " => " + App.humanReadableSize(freeSpace) + " | ";
			}
			catch (OperationException e)
			{
				e.printStackTrace();
			}
		}

		text += "Total => " + App.humanReadableSize(total);

		labelFreeSpaceValues.setText(text);
	}

}
