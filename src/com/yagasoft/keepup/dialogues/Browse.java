
package com.yagasoft.keepup.dialogues;


import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.yagasoft.keepup.App;
import com.yagasoft.overcast.container.local.LocalFile;
import com.yagasoft.overcast.container.local.LocalFolder;


/**
 * @author Ahmed
 * 
 */
public final class Browse
{
	
	public static LocalFolder chooseFolder()
	{
		// only choose directories.
		JFileChooser chooser = new JFileChooser(App.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = chooser.showOpenDialog(App.mainWindow);
		java.io.File selectedFolder = chooser.getSelectedFile();
		
		// if a folder was chosen ...
		if ((result == JFileChooser.APPROVE_OPTION) && (selectedFolder != null))
		{
			return new LocalFolder(Paths.get(selectedFolder.toURI()));
		}
		else
		{
			return null;
		}
	}
	
	public static LocalFile chooseFile()
	{
		JFileChooser chooser = new JFileChooser(App.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		
		int result = chooser.showOpenDialog(App.mainWindow);
		java.io.File selectedFile = chooser.getSelectedFile();
		
		// if a folder was chosen ...
		if ((result == JFileChooser.APPROVE_OPTION) && (selectedFile != null))
		{
			App.setLastDirectory(selectedFile.getParent());
			
			return new LocalFile(Paths.get(selectedFile.toURI()));
		}
		else
		{
			return null;
		}
	}
	
	public static LocalFile[] chooseFiles()
	{
		JFileChooser chooser = new JFileChooser(App.getLastDirectory());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		
		int result = chooser.showOpenDialog(App.mainWindow);
		java.io.File[] selectedFiles = chooser.getSelectedFiles();
		
		// if a folder was chosen ...
		if ((result == JFileChooser.APPROVE_OPTION)
				&& (selectedFiles != null) && (selectedFiles.length > 0))
		{
			App.setLastDirectory(selectedFiles[0].getParent());
			
			LocalFile[] files = new LocalFile[selectedFiles.length];
			
			for (int i = 0; i < files.length; i++)
			{
				files[i] = new LocalFile(Paths.get(selectedFiles[i].toURI()));
			}
			
			return files;
		}
		else
		{
			return null;
		}
	}
}
