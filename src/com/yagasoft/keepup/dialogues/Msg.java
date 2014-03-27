
package com.yagasoft.keepup.dialogues;


import javax.swing.JOptionPane;

import com.yagasoft.keepup.App;


/**
 * @author Ahmed
 * 
 */
public final class Msg
{
	
	public static void showMessage(String message)
	{
		JOptionPane.showMessageDialog(App.mainWindow, message, message
				, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void showWarning(String message)
	{
		JOptionPane.showMessageDialog(App.mainWindow, message, "WARNING!"
				, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showError(String message)
	{
		JOptionPane.showMessageDialog(App.mainWindow, message, "ERROR!"
				, JOptionPane.ERROR_MESSAGE);
	}
	
	public static int showQuestion(String message)
	{
		return JOptionPane.showOptionDialog(App.mainWindow, message
				, message, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null
				, null, 1);
	}
	
	public static String getInput(String message)
	{
		return JOptionPane.showInputDialog(App.mainWindow, message);
	}
}
