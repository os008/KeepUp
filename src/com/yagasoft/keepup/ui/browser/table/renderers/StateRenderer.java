
package com.yagasoft.keepup.ui.browser.table.renderers;


import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.yagasoft.keepup._keepup;
import com.yagasoft.keepup.backup.State;


public class StateRenderer extends DefaultTableCellRenderer
{
	
	private static final long	serialVersionUID	= 4069046002589244426L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected
			, boolean hasFocus, int row, int column)
	{
		String imageName = null;
		
		if (value != null)
		{
			switch ((State) value)
			{
				case ADD:
				case MODIFY:
					imageName = "modified";
					break;
				case DELETE:
					imageName = "deleted";
					break;
				case SYNCED:
					imageName = "syncd";
					break;
				case REMOVE:
				case REMOVE_ALL:
				default:
					imageName = null;
					break;
			}
		}
		
		if (imageName != null)
		{
			// look for the image.
			String imgLocation = "images/states/" + imageName + ".gif";
			URL imageURL = _keepup.class.getResource(imgLocation);
			
			// set image inside cell
			setIcon(new ImageIcon(imageURL));
		}
		else
		{
			setIcon(null);
			setText(null);
		}
		
		return this;
	}
	
}
