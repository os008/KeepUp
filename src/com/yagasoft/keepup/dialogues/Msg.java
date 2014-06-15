/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.dialogues/Msg.java
 *
 *			Modified: 15-Jun-2014 (19:27:03)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.dialogues;


import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import com.yagasoft.keepup.App;


/**
 * The Class Msg.
 *
 * @author Ahmed
 */
public final class Msg
{
	
	/**
	 * Show message to show.
	 *
	 * @param message
	 *            Message.
	 */
	public static void showMessage(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, "Infomation."
				, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Show warning.
	 *
	 * @param message
	 *            Message to show.
	 */
	public static void showWarning(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, "WARNING!"
				, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Show an error.
	 *
	 * @param message
	 *            Message to show.
	 */
	public static void showError(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, "ERROR!"
				, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Show an error and wait for a confirmation by button press.
	 *
	 * @param message
	 *            Message to show.
	 */
	public static void showErrorAndConfirm(String message)
	{
		if (JOptionPane.showOptionDialog(App.combinedStoragePanel, message
				, "ERROR!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null
				, new String[] { "OK" }, 0) == 0)
		{
			;
		}
	}
	
	/**
	 * Ask a yes or no question.
	 *
	 * @param message
	 *            Message to show.
	 * @return true for 'Yes', and false for 'No'.
	 */
	public static boolean askQuestion(String message)
	{
		return JOptionPane.showOptionDialog(App.combinedStoragePanel, message
				, "Question.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null
				, null, 1) == 0 ? true : false;
	}
	
	/**
	 * Ask confirmation.
	 *
	 * @param message
	 *            Message to show.
	 * @return true, if confirmed
	 */
	public static boolean askConfirmation(String message)
	{
		return JOptionPane.showOptionDialog(App.combinedStoragePanel, message
				, "Please confirm.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null
				, null, 1) == 0 ? true : false;
	}
	
	/**
	 * Gets the input.
	 *
	 * @param message
	 *            Message to show.
	 * @return the input as a string
	 */
	public static String getInput(String message)
	{
		return JOptionPane.showInputDialog(App.combinedStoragePanel, message);
	}
	
	/**
	 * Gets the password.<br />
	 * <br />
	 * Credit: Mark A. Ziesemer
	 * (http://blogger.ziesemer.com/2007/03/java-password-dialog.html)
	 *
	 * @param message
	 *            the message.
	 * @return the password
	 */
	public static String getPassword(String message)
	{
		final JPasswordField jpf = new JPasswordField();
		
		JOptionPane jop = new JOptionPane(jpf
				, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = jop.createDialog(message);
		
		dialog.addComponentListener(new ComponentAdapter()
		{
			
			@Override
			public void componentShown(ComponentEvent e)
			{
				SwingUtilities.invokeLater(() -> jpf.requestFocusInWindow());
			}
		});
		
		dialog.setVisible(true);
		dialog.dispose();
		char[] pass = jpf.getPassword();
		
		return new String(pass);
	}
}
