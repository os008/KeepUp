
package com.yagasoft.keepup.backup.ui.browser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.swing.event.TreeSelectionEvent;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup.dialogues.Msg;
import com.yagasoft.keepup.ui.FileTable;
import com.yagasoft.keepup.ui.FileTableController;
import com.yagasoft.overcast.base.container.local.LocalFile;


public class LocalTableController extends FileTableController<LocalFile> implements ITreeSelectionListener
{


	public LocalTableController(FileTable filesTable)
	{
		this(filesTable, null);
	}

	@SuppressWarnings("unchecked")
	public LocalTableController(FileTable filesTable, Function<LocalFile, Object>[] columnFunctions)
	{
		super(filesTable, columnFunctions);

		List<Function<LocalFile, Object>> functions = new ArrayList<Function<LocalFile, Object>>();
		functions.add(file -> file);
		functions.add(file -> App.humanReadableSize(file.getSize()));
		functions.add(file -> file);
		this.columnFunctions = functions.toArray(new Function[functions.size()]);
	}

	@Override
	public LocalFile[] getSelectedFiles()
	{
		return Arrays.stream(view.getSelectedFiles())
				.map(file -> (LocalFile) file)
				.toArray(size -> new LocalFile[size]);
	}

	@Override
	public void localTreeSelectionChanged(String selectedPath)
	{
		try
		{
			updateTable(Files.list(Paths.get(selectedPath)).parallel()
					.filter(path -> !Files.isDirectory(path))
					.map(path -> new LocalFile(path))
					.toArray(size -> new LocalFile[size]));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Msg.showError(e.getMessage());
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{}

}
