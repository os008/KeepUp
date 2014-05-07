
package com.yagasoft.keepup.combinedstorage.ui.browser.table;


import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.combinedstorage.ui.actions.FileToolBar;


public class FilesTable extends JPanel
{

	private static final long	serialVersionUID	= -8729490450147401081L;

	// //////////////////////////////////////////////////////////////////////////////////////
	// #region Files table fields.
	// ======================================================================================

	/** Files tool bar. */
	private FileToolBar			toolBarFiles;

	/** Scroll pane files. */
	private JScrollPane			scrollPaneFiles;

	/** Table of the files. */
	private JTable				tableFiles;

	/** Table model. */
	private DefaultTableModel	tableModel;

	/** Column names. */
	private String[]			columnNames;

	/** Table data. */
	private Object[][]			tableData;

	// ======================================================================================
	// #endregion Files table fields.
	// //////////////////////////////////////////////////////////////////////////////////////

	public FilesTable()
	{
		setLayout(new BorderLayout());

		columnNames = new String[] { "Name", "Size", "CSP" };
		tableData = new String[0][3];

		// create model and table from model.
		tableModel = new DefaultTableModel(tableData, columnNames);
		tableFiles = new JTable(tableModel);
		scrollPaneFiles = new JScrollPane(tableFiles);
		formatTable();
		add(scrollPaneFiles, BorderLayout.CENTER);

		toolBarFiles = new FileToolBar();
		add(toolBarFiles, BorderLayout.NORTH);

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
	private void formatTable()
	{
		// set columns to be right aligned.
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		tableFiles.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

		// columns can't be selected.
		tableFiles.setColumnSelectionAllowed(false);
		tableFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		adjustColumns(App.mainWindow == null ? 768 : App.mainWindow.getWidth());
	}

	/**
	 * Adjust the columns of the table to look best.
	 *
	 * @param width
	 *            Width of the app frame.
	 */
	public void adjustColumns(int width)
	{
		tableFiles.getColumnModel().getColumn(0).setPreferredWidth(width - 530);

		tableFiles.getColumnModel().getColumn(1).setPreferredWidth(40);
		tableFiles.getColumnModel().getColumn(1).setMinWidth(40);

		tableFiles.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableFiles.getColumnModel().getColumn(2).setMinWidth(100);
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

	public FileToolBar getToolBarFiles()
	{
		return toolBarFiles;
	}

	public void setToolBarFiles(FileToolBar toolBarFiles)
	{
		this.toolBarFiles = toolBarFiles;
	}

	public JTable getTableFiles()
	{
		return tableFiles;
	}

	public void setTableFiles(JTable tableFiles)
	{
		this.tableFiles = tableFiles;
	}

	// ======================================================================================
	// #endregion Getters and setters.
	// //////////////////////////////////////////////////////////////////////////////////////
}
