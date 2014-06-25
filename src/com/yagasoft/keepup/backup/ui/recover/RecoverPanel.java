/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.recover/RecoverPanel.java
 *
 *			Modified: 24-Jun-2014 (19:45:01)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.recover;


import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.yagasoft.keepup.DB;
import com.yagasoft.keepup.DB.Table;
import com.yagasoft.keepup.Operation;
import com.yagasoft.keepup.Transfer;
import com.yagasoft.keepup.ui.browser.table.FileTable;
import com.yagasoft.overcast.base.container.File;
import com.yagasoft.overcast.base.container.local.LocalFile;
import com.yagasoft.overcast.base.container.local.LocalFolder;
import com.yagasoft.overcast.base.container.transfer.event.ITransferProgressListener;
import com.yagasoft.overcast.base.container.transfer.event.TransferState;


/**
 * The Class RecoverPanel.
 */
public class RecoverPanel extends JPanel
{

	/** Constant: SerialVersionUID. */
	private static final long		serialVersionUID	= -29571820782448947L;

	private JFrame					frame;

	/** Text field path. */
	private JTextField				textFieldPath;

	/** Button restore. */
	private JButton					buttonRestore;

	private FileTable				fileTable;

	private RecoverTableController	recoverTableController;

	private File<?>					recoveringFile;

	/**
	 * Instantiates a new recover panel.
	 *
	 * @param columnNames
	 *            Column names.
	 * @param columnsWidthPercent
	 *            Columns width percent.
	 * @param rightAlignedColumns
	 *            Right aligned columns.
	 * @param renderers
	 *            Renderers.
	 */
	public RecoverPanel(File<?> recoveringFile, List<File<?>> result)
	{
		this.recoveringFile = recoveringFile;
		initGUI();
		recoverTableController.updateTable(result);
	}

	/**
	 * Inits the gui.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		//
		textFieldPath = new JTextField(recoveringFile.getPath());
		textFieldPath.setEditable(false);
		add(textFieldPath, BorderLayout.NORTH);
		//
		buttonRestore = new JButton("Restore");
		buttonRestore.addActionListener(event -> recoverFile());
		add(buttonRestore, BorderLayout.SOUTH);

		fileTable = new FileTable(new String[] { "Revision", "Date" }
				, new float[] { 1f, 140f }
				, new int[] { 1 }
				, null);

		// table controller
		List<Function<File<?>, Object>> columnFunctions = new ArrayList<Function<File<?>, Object>>();
		columnFunctions.add(file -> file);
		columnFunctions.add(file ->
		{
			String[][] dates = DB.getRecord(Table.backup_revisions
					, new String[] { "date" }
					, "revision = '" + file.getName() + "'");

			if (dates.length > 0)
			{
				return DateFormat.getDateTimeInstance().format(new Date(Long.parseLong(dates[0][0])));
			}
				else
				{
					return "";
				}

			});
		recoverTableController = new RecoverTableController(fileTable, columnFunctions);

		add(fileTable, BorderLayout.CENTER);
	}

	private void recoverFile()
	{
		File<?> selectedFile = recoverTableController.getSelectedFiles().parallelStream().findFirst().orElse(null);

		// nothing to recover
		if (selectedFile == null)
		{
			return;
		}

		// create a listener to perform actions after the file is downloaded, like returning it to its original name.
		ITransferProgressListener downloadListener = (event ->
		{
			if (event.getState() == TransferState.COMPLETED)
			{
				try
				{
					LocalFile existingFile = new LocalFile(recoveringFile.getPath());
					Operation.deleteFiles(Collections.singletonList(existingFile));

					Thread.sleep(2000);

					LocalFile recoveredFile = new LocalFile(recoveringFile.getParent().getPath() + "/" + selectedFile.getName());
					Operation.renameFile(Collections.singletonList(recoveredFile), recoveringFile.getName());

					String[][] dates = DB.getRecord(Table.backup_revisions
							, new String[] { "date" }
							, "revision = '" + selectedFile.getName() + "'");

					Thread.sleep(2000);

					recoveredFile.setDate(Long.parseLong(dates[0][0]));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

			}
		});

		// start a download thread and close the window.
		new Thread(() ->
		{
			Transfer.downloadFile(selectedFile, downloadListener
					, (LocalFolder) recoveringFile.getParent());
		}).start();

		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame()
	{
		return frame;
	}

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

}
