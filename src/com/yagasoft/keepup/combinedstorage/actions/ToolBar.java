
package com.yagasoft.keepup.combinedstorage.actions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.yagasoft.keepup.App;
import com.yagasoft.keepup._keepup;
import com.yagasoft.logger.Logger;
import com.yagasoft.overcast.container.remote.RemoteFile;


public class ToolBar extends JToolBar implements ActionListener
{

	private enum Actions
	{
		DOWNLOAD,
		UPLOAD
	}

	private static final long	serialVersionUID	= 6667133528600925976L;

	public ToolBar()
	{
		initBar();
	}

	private void initBar()
	{
		addButtons();
	}

	protected void addButtons()
	{
		JButton button = null;

		button = makeNavigationButton("download", Actions.DOWNLOAD + "", "Download selected files.", "Download");
		add(button);

		button = makeNavigationButton("upload", Actions.UPLOAD + "", "Upload files to selected folder.", "Upload");
		add(button);
	}

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText)
	{
		// Look for the image.
		String imgLocation = "images\\" + imageName + ".gif";
		URL imageURL = _keepup.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null)
		{	// image found
			button.setIcon(new ImageIcon(imageURL, altText));
		}
		else
		{	// no image found
			button.setText(altText);
			Logger.post("Resource not found: " + imgLocation);
		}

		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		// Handle each button.
		if (Actions.DOWNLOAD.toString().equals(cmd))
		{
			Logger.newSection("Download, WOOT!");

			for (RemoteFile<?> file : App.mainWindow.getBrowserPanel().getSelectedFiles())
			{
				Logger.post(file.getPath());
			}

			Logger.endSection();

			App.downloadFile(App.mainWindow.getBrowserPanel().getSelectedFiles());
		}
		if (Actions.UPLOAD.toString().equals(cmd))
		{
			Logger.post("Upload, WOOT!");
		}
	}
}
