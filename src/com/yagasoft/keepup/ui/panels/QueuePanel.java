/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.panels/QueuePanel.java
 *
 *			Modified: 20-Jun-2014 (18:57:56)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.panels;


import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.ui.browser.table.BetterTableModel;
import com.yagasoft.keepup.ui.browser.table.ButtonColumn;
import com.yagasoft.keepup.ui.browser.table.renderers.ProgressRenderer;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.base.container.transfer.TransferJob;
import com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener;
import com.yagasoft.overcast.base.container.transfer.event.TransferEvent;
import com.yagasoft.overcast.base.container.transfer.event.TransferState;


// TODO convert QueuePanel to MVC.

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
		tableQueue = new JTable(new BetterTableModel(
				new String[] { "File", "Destination", "CSP", "Direction", "Status", "Progress", "Cancel" }, 0));
		scrollPaneQueue = new JScrollPane(tableQueue);
		add(scrollPaneQueue);

		tableQueue.setDefaultRenderer(Float.class, new ProgressRenderer());

		// --------------------------------------------------------------------------------------
		// #region Cancel button.

		Action action = new AbstractAction()
		{

			private static final long	serialVersionUID	= 5104056154903292487L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());		// get row clicked.

				// get the status string in that row.
				String status = (String) ((BetterTableModel) tableQueue.getModel()).getValueAt(
						modelRow, tableQueue.getColumnModel().getColumnIndex("Status"));

				// if it's in progress, then cancel the job.
				if ( !(status.toLowerCase().contains("failed")))
				{
					TransferJob<?> job = (TransferJob<?>) ((BetterTableModel) tableQueue.getModel()).getValueAt(
							modelRow, tableQueue.getColumnModel().getColumnIndex("File"));
					cancelTransfer(job);
				}


				// if it's failed or initialised, remove. Those two aren't removed automatically anyway.
				if (status.toLowerCase().contains("failed") || status.toLowerCase().contains("queued"))
				{
					((DefaultTableModel) table.getModel()).removeRow(modelRow);		// remove row.
				}
			}
		};

		ButtonColumn buttonColumn = new ButtonColumn(tableQueue, action, tableQueue.getColumnModel().getColumnIndex("Cancel"));
		tableQueue.setCellEditor(buttonColumn);

		// #endregion Cancel button.
		// --------------------------------------------------------------------------------------

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
		((BetterTableModel) tableQueue.getModel())
				.addRow(
				new Object[] {
						job, job.getParent().getPath(), job.getCsp(), direction, "Queued ...", new Float(0), new CancelButton() });
	}

	/**
	 * @see com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener#transferProgressChanged(com.yagasoft.overcast.base.container.transfer.event.TransferEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void transferProgressChanged(TransferEvent event)
	{
		Logger.info("PROGRESS " + event.getContainer().getPath() + ": " + event.getState() + " => " + event.getProgress());

		// get the data in the table.
		Vector rows = ((BetterTableModel) tableQueue.getModel()).getDataVector();

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
						App.updateTable();		// redraw the table after fetching file list.
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
							App.updateFreeSpace();	// update free space display
							App.updateTable();
						}

						((BetterTableModel) tableQueue.getModel()).removeRow(i);
						break;
					}
				}

				break;

			case CANCELLED:
				for (int i = 0; i < rows.size(); i++)
				{
					Vector row = (Vector) rows.get(i);

					if (row.contains(event.getJob()))
					{
						((BetterTableModel) tableQueue.getModel()).removeRow(i);
						break;
					}
				}

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
		((BetterTableModel) tableQueue.getModel()).setValueAt(new Float(progress), row, tableQueue.getColumnModel()
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
		((BetterTableModel) tableQueue.getModel()).setValueAt(status, row, tableQueue.getColumnModel().getColumnIndex("Status"));
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
		Vector rows = ((BetterTableModel) tableQueue.getModel()).getDataVector();

		// look for the job in the queue
		for (int i = 0; i < rows.size(); i++)
		{
			Vector row = (Vector) rows.get(i);

			// if found, then fetch the csp and invoke cancel
			if (row.contains(job))
			{
				job.getCsp().cancelTransfer(job);
				break;
			}
		}
	}

	/**
	 * How the button will appear in the cancel column.
	 */
	private class CancelButton extends ImageIcon
	{

		private static final long	serialVersionUID	= 2710162133669731329L;

		public CancelButton()
		{
			super(_keepup.class.getResource("images/cancel.gif"));
		}

	}

}
