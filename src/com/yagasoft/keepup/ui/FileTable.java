/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui/FileTable.java
 *
 *			Modified: 27-May-2014 (20:17:49)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui;


import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * The Class FileTable. It should be used to list files in a folder (selected from a tree).
 * Make sure to store the file object (to be retrieved later with {@link #getSelectedFiles()}) in the first column.
 */
public class FileTable extends JPanel
{

	private static final long	serialVersionUID	= -8729490450147401081L;

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files table fields.
	// ======================================================================================

	/** Scroll pane files. */
	protected JScrollPane		scrollPaneFiles;

	/** Table of the files. */
	protected JTable			tableFiles;

	/** Table model. */
	protected DefaultTableModel	tableModel;

	/** Column names. */
	protected String[]			columnNames;

	/** Table data. */
	protected Object[][]		tableData;

	protected int[]				rightAlignedColumns;

	protected float[] columnsWidthPercent;

	// ======================================================================================
	// #endregion Files table fields.
	// //////////////////////////////////////////////////////////////////////////////////////

	public FileTable(String[] columnNames, float[] columnsWidthPercent, int[] rightAlignedColumns)
	{
		setLayout(new BorderLayout());

		this.columnNames = columnNames;
		tableData = new String[0][columnNames.length];
		this.columnsWidthPercent = columnsWidthPercent;
		this.rightAlignedColumns = rightAlignedColumns;

		// create model and table from model.
		tableModel = new DefaultTableModel(tableData, columnNames);
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

		});

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

		Arrays.stream(rightAlignedColumns)
				.forEach(column -> tableFiles.getColumnModel().getColumn(column).setCellRenderer(rightRenderer));

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
		for (int i = 0; i < columnNames.length; i++)
		{
			tableFiles.getColumnModel().getColumn(i).setPreferredWidth((int) (getWidth() * columnsWidthPercent[i]));
			tableFiles.getColumnModel().getColumn(i).setMinWidth((int) (getWidth() * columnsWidthPercent[i]));
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
		tableFiles.setModel(new DefaultTableModel(tableData, columnNames));
		tableFiles.revalidate();

		formatTable();
	}

	/**
	 * Gets the selected files.
	 *
	 * @return the selected files
	 */
	@SuppressWarnings("rawtypes")
	public Object[] getSelectedFiles()
	{
		// get the data in the table.
		Vector rows = ((DefaultTableModel) tableFiles.getModel()).getDataVector();
		// get selected rows.
		int[] selectedRows = tableFiles.getSelectedRows();

		// files to be returned.
		Object[] files = new Object[selectedRows.length];
		int index = 0;

		// go through the rows' numbers, fetch them, fetch the file stored there, and put it in the returned list.
		for (int rowIndex : selectedRows)
		{
			files[index] = ((Vector) rows.get(rowIndex)).get(0);
			index++;
		}

		return files;
	}

	// ======================================================================================
	// #endregion Table methods.
	// //////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Getters and setters.
	// ======================================================================================

	public JTable getTable()
	{
		return tableFiles;
	}

	public void setTableFiles(JTable tableFiles)
	{
		this.tableFiles = tableFiles;
	}


	/**
	 * @return the scrollPaneFiles
	 */
	public JScrollPane getScrollPaneFiles()
	{
		return scrollPaneFiles;
	}


	/**
	 * @param scrollPaneFiles the scrollPaneFiles to set
	 */
	public void setScrollPaneFiles(JScrollPane scrollPaneFiles)
	{
		this.scrollPaneFiles = scrollPaneFiles;
	}


	/**
	 * @return the tableModel
	 */
	public DefaultTableModel getTableModel()
	{
		return tableModel;
	}


	/**
	 * @param tableModel the tableModel to set
	 */
	public void setTableModel(DefaultTableModel tableModel)
	{
		this.tableModel = tableModel;
	}


	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}


	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}


	/**
	 * @return the tableData
	 */
	public Object[][] getTableData()
	{
		return tableData;
	}


	/**
	 * @param tableData the tableData to set
	 */
	public void setTableData(Object[][] tableData)
	{
		this.tableData = tableData;
	}


	/**
	 * @return the rightAlignedColumns
	 */
	public int[] getRightAlignedColumns()
	{
		return rightAlignedColumns;
	}


	/**
	 * @param rightAlignedColumns the rightAlignedColumns to set
	 */
	public void setRightAlignedColumns(int[] rightAlignedColumns)
	{
		this.rightAlignedColumns = rightAlignedColumns;
	}


	/**
	 * @return the columnsWidthPercent
	 */
	public float[] getColumnsWidthPercent()
	{
		return columnsWidthPercent;
	}


	/**
	 * @param columnsWidthPercent the columnsWidthPercent to set
	 */
	public void setColumnsWidthPercent(float[] columnsWidthPercent)
	{
		this.columnsWidthPercent = columnsWidthPercent;
	}

	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////

}
