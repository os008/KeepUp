/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		Modified MIT License (GPL v3 compatible)
 * 			License terms are in a separate file (license.txt)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/QueuePanel.java
 *
 *			Modified: 18-Mar-2014 (16:32:25)
 *			   Using: Eclipse J-EE / JDK 7 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.container.transfer.ITransferProgressListener;
import com.yagasoft.overcast.container.transfer.TransferEvent;
import com.yagasoft.overcast.container.transfer.TransferJob;
import com.yagasoft.overcast.container.transfer.TransferState;


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
		tableQueue = new JTable(new QueueTableModel(
				new String[] { "File", "Destination", "CSP", "Direction", "Status", "Progress", "Cancel" }, 0));
		scrollPaneQueue = new JScrollPane(tableQueue);
		add(scrollPaneQueue);

		tableQueue.setDefaultRenderer(Float.class, new ProgressRenderer());
		// tableQueue.setDefaultRenderer(CancelButton.class, new CancelButtonRenderer());


		//--------------------------------------------------------------------------------------
		// #region Cancel button.

		Action action = new AbstractAction()
		{

			private static final long	serialVersionUID	= 5104056154903292487L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());

				String status = (String) ((QueueTableModel) tableQueue.getModel()).getValueAt(
						modelRow, tableQueue.getColumnModel().getColumnIndex("Status"));

				if ( !(status.toLowerCase().indexOf("failed") >= 0))
				{
					TransferJob<?> job = (TransferJob<?>) ((QueueTableModel) tableQueue.getModel()).getValueAt(
							modelRow, tableQueue.getColumnModel().getColumnIndex("File"));
					cancelTransfer(job);
				}
//				else
//				{
					((DefaultTableModel) table.getModel()).removeRow(modelRow);
//				}
					
				if (((QueueTableModel) tableQueue.getModel()).getDataVector().size() > 0)
				{
					App.mainWindow.getStatusBar().updateFreeSpace();	// update free space display
					App.mainWindow.getBrowserPanel().updateTable();
				}
			}
		};

		ButtonColumn buttonColumn = new ButtonColumn(tableQueue, action, tableQueue.getColumnModel().getColumnIndex("Cancel"));
		tableQueue.setCellEditor(buttonColumn);

		// #endregion Cancel button.
		//--------------------------------------------------------------------------------------

	}

	/**
	 * Adds the transfer job.
	 *
	 * @param job
	 *            the job.
	 * @param direction
	 *            Direction of transfer as a string.
	 */
	public void addTransferJob(TransferJob<?> job, String direction)
	{
		((QueueTableModel) tableQueue.getModel())
				.addRow(
				new Object[] {
						job, job.getParent().getPath(), job.getCsp(), direction, "Queued ...", new Float(0), new CancelButton() });
	}

//	/**
//	 * Adds an upload job to the visual queue.
//	 *
//	 * @param job
//	 *            The job.
//	 * @param destination
//	 *            Destination.
//	 */
//	public void addTransferJob(UploadJob<?, ?> job, Folder<?> destination)
//	{
//		addTransferJob(job.getLocalFile().getPath(), destination.getPath(), job.getRemoteFile().getCsp().getName(), "Upload");
//	}
//
//	/**
//	 * Adds a download job to the visual queue.
//	 *
//	 * @param job
//	 *            The job.
//	 * @param destination
//	 *            Destination.
//	 */
//	public void addTransferJob(DownloadJob<?> job, Folder<?> destination)
//	{
//		addTransferJob(job.getRemoteFile().getPath(), destination.getPath(), job.getRemoteFile().getCsp().getName(), "Download");
//	}

	/**
	 * @see com.yagasoft.overcast.container.transfer.ITransferProgressListener#transferProgressChanged(com.yagasoft.overcast.container.transfer.TransferEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void transferProgressChanged(TransferEvent event)
	{
		Logger.post("PROGRESS!: " + event.getState() + " => " + event.getProgress());

		// get the data in the table.
		Vector rows = ((QueueTableModel) tableQueue.getModel()).getDataVector();

		String statusString = null;

		switch (event.getState())
		{
			case INITIALISED:
			case IN_PROGRESS:
				statusString = event.getState() == TransferState.IN_PROGRESS ? "In progress ..." : "Initialised ...";

				// go through the rows searching for the matching row ...
				for (int i = 0; i < rows.size(); i++)
				{
					Vector row = (Vector) rows.get(i);

					// if the file at that row has the same path ...
					if (row.contains(event.getJob()))
					{
						// update the progress and status
						setProgress(i, event.getProgress());
						setStatus(i, statusString);
						break;		// done!
					}
				}
				break;

			case FAILED:
				statusString = "FAILED!";

				for (int i = 0; i < rows.size(); i++)
				{
					Vector row = (Vector) rows.get(i);

					if (row.contains(event.getJob()))
					{
						setStatus(i, statusString);
						App.mainWindow.getBrowserPanel().updateTable();		// redraw the table after fetching file list.
						break;
					}
				}

				break;

			case COMPLETED:
				for (int i = 0; i < rows.size(); i++)
				{
					Vector row = (Vector) rows.get(i);

					if (row.contains(event.getJob()))
					{
						if (rows.size() == 1)
						{
							App.mainWindow.getStatusBar().updateFreeSpace();	// update free space display
							App.mainWindow.getBrowserPanel().updateTable();
						}

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
		((QueueTableModel) tableQueue.getModel()).setValueAt(new Float(progress), row, tableQueue.getColumnModel()
				.getColumnIndex("Progress"));
	}

	/**
	 * Updates the status of the file in the queue.<br />
	 *
	 * @param row
	 *            The row number.
	 * @param progress
	 *            The progress between 0 and 1.
	 */
	private void setStatus(int row, String status)
	{
		((QueueTableModel) tableQueue.getModel()).setValueAt(status, row, tableQueue.getColumnModel().getColumnIndex("Status"));
	}

	/**
	 * Cancel transfer related to the job passed.
	 *
	 * @param job
	 *            the job.
	 */
	@SuppressWarnings("rawtypes")
	public void cancelTransfer(TransferJob<?> job)
	{
		// get the data in the table.
		Vector rows = ((QueueTableModel) tableQueue.getModel()).getDataVector();

		for (int i = 0; i < rows.size(); i++)
		{
			Vector row = (Vector) rows.get(i);

			if (row.contains(job))
			{
				job.getCsp().cancelUpload(job);
//				((QueueTableModel) tableQueue.getModel()).removeRow(i);
				break;
			}
		}
	}

	/**
	 * Defines how to handle the progress bar inside the table.
	 */
	private class ProgressRenderer extends JProgressBar implements TableCellRenderer
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

//			if (v > 0)
			setIndeterminate(false);
			setStringPainted(true);
			setValue((int) (v.floatValue() * 100));
//			}

			return this;
		}
	}

	private class CancelButton extends ImageIcon
	{

		private static final long	serialVersionUID	= 2710162133669731329L;

		public CancelButton()
		{
			super(_keepup.class.getResource("images\\cancel.gif"));
		}

	}

//	/**
//	 * Defines how to handle the cancel button inside the table.
//	 */
//	private class CancelButtonRenderer extends JButton implements TableCellRenderer
//	{
//
//		/** Constant: SerialVersionUID. */
//		private static final long	serialVersionUID	= -4106456853733016017L;
//
//		@Override
//		public Component getTableCellRendererComponent(JTable table, Object value
//				, boolean isSelected, boolean hasFocus, int row, int col)
//		{
//			setIcon(new ImageIcon(_keepup.class.getResource("images\\cancel.gif")));
//			return this;
//		}
//	}

	/**
	 * An extension of {@link DefaultTableModel}, which is used to overcome the limitation of treating all cells as {@link String}
	 * --
	 * for the progress bar problem.
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private class QueueTableModel extends DefaultTableModel
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
}
