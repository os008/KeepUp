/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.browser/FileTable.java
 *
 *			Modified: 20-Jun-2014 (21:05:19)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.browser.table;


import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.ui.toolbars.FileToolBar;


/**
 * The Class FileTable. It should be used to list files in a folder (selected from a tree).
 * Make sure to store the file object (to be retrieved later with {@link #getSelectedFiles()}) in the first column.
 */
public class FileTable extends JPanel
{

	private static final long					serialVersionUID	= -8729490450147401081L;

	private FileToolBar							toolBarFiles;

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files table fields.
	// ======================================================================================

	/** Scroll pane files. */
	protected JScrollPane						scrollPaneFiles;

	/** Table of the files. */
	protected JTable							tableFiles;

	/** Table model. */
	protected BetterTableModel					tableModel;

	/** Column names. */
	protected String[]							columnNames;

	/** Table data. */
	protected Object[][]						tableData;

	protected int[]								rightAlignedColumns;

	protected float[]							columnsWidthPercent;

	protected Map<Class<?>, TableCellRenderer>	renderers;

	// ======================================================================================
	// #endregion Files table fields.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates a new file table. All the parameters' contents should be in order of display.
	 *
	 * @param columnNames
	 *            Column names.
	 * @param columnsWidthPercent
	 *            Columns width percent. Should sum to 1.0.
	 * @param rightAlignedColumns
	 *            Right aligned columns indexes.
	 * @param renderers
	 *            Renderer for each class saved in the table.
	 *            For example, if the table has a progress bar,
	 *            then pass a renderer than extends JProgressBar and Float.class as its key.
	 */
	public FileTable(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns
			, Map<Class<?>, TableCellRenderer> renderers)
	{
		setLayout(new BorderLayout());

		this.columnNames = columnNames;
		tableData = new String[0][columnNames.length];
		this.columnsWidthPercent = columnsWidthPercent;
		this.rightAlignedColumns = rightAlignedColumns;
		this.renderers = renderers;

		// create model and table from model.
		tableModel = new BetterTableModel(tableData, columnNames);
		tableFiles = new JTable(tableModel);
		scrollPaneFiles = new JScrollPane(tableFiles);
		formatTable();
		add(scrollPaneFiles, BorderLayout.CENTER);

		// re-adjust columns widths when window is resized.
		addComponentListener(new ComponentAdapter()
		{

			@Override
			public void componentResized(ComponentEvent e)
			{
				super.componentResized(e);
				adjustColumns(getWidth());
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				super.componentShown(e);
				adjustColumns(getWidth());
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				super.componentMoved(e);
				adjustColumns(getWidth());
			}
		});

	}

	/**
	 * Adds a tool bar to the view.
	 *
	 * @param toolbar
	 *            the new tool bar
	 */
	public void addToolBar(FileToolBar toolbar)
	{
		if (toolBarFiles != null)
		{
			return;
		}

		toolBarFiles = toolbar;
		add(toolBarFiles, BorderLayout.NORTH);
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Table methods.
	// ======================================================================================

	/**
	 * Set how the table behaves visually.
	 */
	protected void formatTable()
	{
		// set columns to be right aligned.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

		for (int column : rightAlignedColumns)
		{
			tableFiles.getColumnModel().getColumn(column).setCellRenderer(rightRenderer);
		}

		if (renderers != null)
		{
			for (Class<?> renderedClass : renderers.keySet())
			{
				tableFiles.setDefaultRenderer(renderedClass, renderers.get(renderedClass));
			}
		}

		// columns can't be selected.
		tableFiles.setColumnSelectionAllowed(false);
		tableFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		adjustColumns(getWidth());
	}

	/**
	 * Adjust the columns of the table to look best.
	 *
	 * @param width
	 *            Width of the app frame.
	 */
	public void adjustColumns(int width)
	{
		int totalWidth = getWidth();

		for (int i = 0; i < columnNames.length; i++)
		{
			// if it's greater than one, then it's not a percent, but a fixed width
			if (columnsWidthPercent[i] > 1)
			{
				int columnwidth = (int) columnsWidthPercent[i];
				totalWidth -= columnwidth;
				tableFiles.getColumnModel().getColumn(i).setPreferredWidth(columnwidth);
				tableFiles.getColumnModel().getColumn(i).setMinWidth(columnwidth);
			}
		}

		// go another time for percentages
		for (int i = 0; i < columnNames.length; i++)
		{
			// fit it at a % of the remaining size
			if (columnsWidthPercent[i] <= 1)
			{
				int columnwidth = (int) (totalWidth * columnsWidthPercent[i]);
				tableFiles.getColumnModel().getColumn(i).setPreferredWidth(columnwidth);
				tableFiles.getColumnModel().getColumn(i).setMinWidth(columnwidth);
			}
		}
	}

	/**
	 * Update table with the files passed.
	 *
	 * @param fileArray
	 *            File array.
	 */
	public void updateTable(Object[][] tableData)
	{
		this.tableData = tableData;
		tableFiles.setModel(new BetterTableModel(tableData, columnNames));
		tableFiles.revalidate();

		formatTable();
	}

	/**
	 * Gets the selected files. Fetches the object stored in the first column.
	 *
	 * @return the selected files
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> getSelectedFiles()
	{
		// get the data in the table.
		Vector rows = ((DefaultTableModel) tableFiles.getModel()).getDataVector();
		// get selected rows.
		int[] selectedRows = tableFiles.getSelectedRows();

		// files to be returned.
		List<Object> files = new ArrayList<Object>();

		// go through the rows' numbers, fetch them, fetch the file stored there, and put it in the returned list.
		Arrays.stream(selectedRows)
				.forEach(row -> files.add(((Vector) rows.get(row)).get(0)));

		return files;
	}

	/**
	 * Gets all files. Fetches the object stored in the first column.
	 *
	 * @return all files
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getAllFiles()
	{
		// get the data in the table.
		Vector rows = ((DefaultTableModel) tableFiles.getModel()).getDataVector();

		// files to be returned.
		List<Object> files = new ArrayList<Object>();

		// go through the rows, fetch the file stored there, and put it in the returned list.
		rows.stream().forEach(row -> files.add(((Vector) row).get(0)));

		return files;
	}

	// ======================================================================================
	// #endregion Table methods.
	// //////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================

	public FileToolBar getToolBarFiles()
	{
		return toolBarFiles;
	}

	public JTable getTable()
	{
		return tableFiles;
	}

	/**
	 * @return the scrollPaneFiles
	 */
	public JScrollPane getScrollPaneFiles()
	{
		return scrollPaneFiles;
	}

	/**
	 * @return the tableModel
	 */
	public BetterTableModel getTableModel()
	{
		return tableModel;
	}

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * @return the tableData
	 */
	public Object[][] getTableData()
	{
		return tableData;
	}

	/**
	 * @return the rightAlignedColumns
	 */
	public int[] getRightAlignedColumns()
	{
		return rightAlignedColumns;
	}

	/**
	 * @return the columnsWidthPercent
	 */
	public float[] getColumnsWidthPercent()
	{
		return columnsWidthPercent;
	}

	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////

}
