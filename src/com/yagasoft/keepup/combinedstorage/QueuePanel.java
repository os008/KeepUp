/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/QueuePanel.java
 *
 *			Modified: 12-Mar-2014 (21:45:54)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.awt.Component;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.container.Folder;
import com.yagasoft.overcast.container.transfer.DownloadJob;
import com.yagasoft.overcast.container.transfer.ITransferProgressListener;
import com.yagasoft.overcast.container.transfer.TransferEvent;
import com.yagasoft.overcast.container.transfer.UploadJob;


/**
 * The Class QueuePanel.
 */
public class QueuePanel extends JPanel implements ITransferProgressListener
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -7487540524979826459L;

	/** Table queue. */
	private JTable				tableQueue;

	/** Scroll pane queue. */
	private JScrollPane			scrollPaneQueue;

	/**
	 * Create the panel.
	 */
	public QueuePanel()
	{

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		tableQueue = new JTable(new QueueTableModel(new String[] { "File", "Destination", "CSP", "Direction", "Progress" }, 0));
		scrollPaneQueue = new JScrollPane(tableQueue);
		add(scrollPaneQueue);

		tableQueue.setDefaultRenderer(Float.class, new ProgressRenderer());
	}

	/**
	 * Adds the transfer job.
	 *
	 * @param path
	 *            Path.
	 * @param destination
	 *            Destination.
	 * @param csp
	 *            Csp.
	 * @param direction
	 *            Direction.
	 */
	private void addTransferJob(String path, String destination, String csp, String direction)
	{
		((QueueTableModel) tableQueue.getModel()).addRow(new Object[] { path, destination, csp, direction, new Float(0) });
	}

	/**
	 * Adds an upload job to the visual queue.
	 *
	 * @param job
	 *            The job.
	 * @param destination
	 *            Destination.
	 */
	public void addTransferJob(UploadJob<?, ?> job, Folder<?> destination)
	{
		addTransferJob(job.getLocalFile().getPath(), destination.getPath(), job.getRemoteFile().getCsp().getName(), "Upload");
	}

	/**
	 * Adds a download job to the visual queue.
	 *
	 * @param job
	 *            The job.
	 * @param destination
	 *            Destination.
	 */
	public void addTransferJob(DownloadJob<?> job, Folder<?> destination)
	{
		addTransferJob(job.getRemoteFile().getPath(), destination.getPath(), job.getRemoteFile().getCsp().getName(), "Download");
	}

	/**
	 * @see com.yagasoft.overcast.container.transfer.ITransferProgressListener#progressChanged(com.yagasoft.overcast.container.transfer.TransferEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void progressChanged(TransferEvent event)
	{
		Logger.post("PROGRESS!: " + event.getState() + " => " + event.getProgress());

		switch (event.getState())
		{
			case INITIALISED:
			case IN_PROGRESS:
				// get the data in the table.
				Vector rows = ((QueueTableModel) tableQueue.getModel()).getDataVector();

				// go through the rows searching for the matching row ...
				for (int i = 0; i < rows.size(); i++)
				{
					Vector row = (Vector) rows.get(i);

					// if the file at that row has the same path ...
					if (row.contains(event.getContainer().getPath()))
					{
						// update the progress.
						setProgress(i, event.getProgress());
						break;		// done!
					}
				}
				break;
			case FAILED:
				break;
			case COMPLETED:
				Vector rows1 = ((QueueTableModel) tableQueue.getModel()).getDataVector();

				for (int i = 0; i < rows1.size(); i++)
				{
					Vector row = (Vector) rows1.get(i);

					if (row.contains(event.getContainer().getPath()))
					{
						((QueueTableModel) tableQueue.getModel()).removeRow(i);
						break;
					}
				}
				break;
			default:
				break;

		}
	}

	/**
	 * Updates the progress of the progress bar for passed row.<br />
	 * This only updates the attached data value for that cell; the bar is updated by an event automatically.
	 *
	 * @param row
	 *            The row number.
	 * @param progress
	 *            The progress between 0 and 1.
	 */
	private void setProgress(int row, float progress)
	{
		((QueueTableModel) tableQueue.getModel()).setValueAt(new Float(progress), row, tableQueue.getColumnModel().getColumnIndex("Progress"));
	}
}


/**
 * Defines how to handle the progress bar inside the table.
 */
class ProgressRenderer extends JProgressBar implements TableCellRenderer
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -4106456853733016017L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value
			, boolean isSelected, boolean hasFocus, int row, int col)
	{
		Float v = (Float) value;

		// set the range.
		setMinimum(0);
		setMaximum(100);

//		if (v > 0)
//		{
		// update progress bar's position.
		setIndeterminate(false);
		setStringPainted(true);
		setValue((int) (v.floatValue() * 100));
//		}
//		else
//		{
//			setIndeterminate(true);
//			setStringPainted(false);
//		}

		return this;
	}
}


/**
 * An extension of {@link DefaultTableModel}, which is used to overcome the limitation of treating all cells as {@link String} --
 * for the progress bar problem.
 */
@SuppressWarnings("rawtypes")
class QueueTableModel extends DefaultTableModel
{

	/** Constant: SerialVersionUID. */
	private static final long	serialVersionUID	= -4057598058867765548L;

	/**
	 *
	 */
	public QueueTableModel()
	{
		super();
	}

	/**
	 * @param rowCount
	 * @param columnCount
	 */
	public QueueTableModel(int rowCount, int columnCount)
	{
		super(rowCount, columnCount);
	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public QueueTableModel(Object[] columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public QueueTableModel(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);
	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public QueueTableModel(Vector columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}

	/**
	 * @param data
	 * @param columnNames
	 */
	public QueueTableModel(Vector data, Vector columnNames)
	{
		super(data, columnNames);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}
}
