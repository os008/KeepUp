/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage.ui.search/SearchPanel.java
 *
 *			Modified: 20-Jun-2014 (20:27:09)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage.ui.search;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.Util;
import com.yagasoft.keepup.ui.browser.table.FileTable;
import com.yagasoft.keepup.ui.browser.table.renderers.FilePathRenderer;
import com.yagasoft.overcast.base.container.Container;
import com.yagasoft.overcast.base.container.File;


/**
 * The Class SearchPanel.
 */
public class SearchPanel extends JPanel
{

	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= -8003399638575844386L;
	private FileTable				searchedFilesTable;
	private SearchTableController	searchTableController;

	/**
	 * Create the panel.
	 */
	public SearchPanel(List<File<?>> result)
	{
		initGUI();
		searchTableController.updateTable(result, Container.getPathComparator());
	}

	/**
	 * Initialises the panel.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout());

		// table view
		Map<Class<?>, TableCellRenderer> renderers = new HashMap<Class<?>, TableCellRenderer>();
		renderers.put(File.class, new FilePathRenderer());		// render a file as its path
		searchedFilesTable = new FileTable(
				new String[] { "Path", "Size", "CSP" }
				, new float[] { 1f, 65f, 80f }
				, new int[] { 1 }
				, renderers);

		// table controller
		List<Function<File<?>, Object>> columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file -> Util.humanReadableSize(file.getSize()));
		columnFunctions.add(file -> file.getCsp());
		searchTableController = new SearchTableController(searchedFilesTable, columnFunctions);

		// add a tool bar to the view
		searchedFilesTable.addToolBar(new SearchToolBar(searchTableController));

		add(searchedFilesTable);
	}
}
