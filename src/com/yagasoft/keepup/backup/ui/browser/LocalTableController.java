/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.browser/LocalTableController.java
 * 
 *			Modified: 12-Jun-2014 (23:22:42)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.browser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.event.TreeSelectionEvent;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.keepup.ui.FileTable;
import com.yagasoft.keepup.ui.FileTableController;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;


/**
 * The Class LocalTableController.
 */
public class LocalTableController extends FileTableController implements ITreeSelectionListener
{
	
	/**
	 * Instantiates a new local table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 */
	public LocalTableController(FileTable filesTable)
	{
		this(filesTable, null);
	}
	
	/**
	 * Instantiates a new local table controller.
	 *
	 * @param filesTable
	 *            Files table.
	 * @param columnFunctions
	 *            Column functions.
	 */
	@SuppressWarnings("unchecked")
	public LocalTableController(FileTable filesTable, Function<File<?>, Object>[] columnFunctions)
	{
		super(filesTable, columnFunctions);
		
		List<Function<LocalFile, Object>> functions = new ArrayList<Function<LocalFile, Object>>();
		functions.add(file -> file);
		functions.add(file -> App.humanReadableSize(file.getSize()));
		functions.add(file -> file);	// TODO represent the state
		this.columnFunctions = functions.toArray(new Function[functions.size()]);
	}
	
	/**
	 * @see com.yagasoft.keepup.backup.ui.browser.ITreeSelectionListener#localTreeSelectionChanged(java.lang.String)
	 */
	@Override
	public void localTreeSelectionChanged(String selectedPath)
	{
		try
		{
			updateTable(Files.list(Paths.get(selectedPath)).parallel()
					.filter(path -> !Files.isDirectory(path))
					.map(path -> new LocalFile(path))
					.collect(Collectors.toList()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}
	
	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{}
	
}
