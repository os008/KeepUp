/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.dialogues/Msg.java
 *
 *			Modified: 01-Apr-2014 (13:48:24)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
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
 * @author Ahmed
 *
 */
public final class Msg
{
	
	public static void showMessage(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, message
				, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void showWarning(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, "WARNING!"
				, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showError(String message)
	{
		JOptionPane.showMessageDialog(App.combinedStoragePanel, message, "ERROR!"
				, JOptionPane.ERROR_MESSAGE);
	}
	
	public static int showQuestion(String message)
	{
		return JOptionPane.showOptionDialog(App.combinedStoragePanel, message
				, message, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null
				, null, 1);
	}
	
	public static boolean askConfirmation(String message)
	{
		return JOptionPane.showOptionDialog(App.combinedStoragePanel, message
				, message, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null
				, null, 1) == 0 ? true : false;
	}
	
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
