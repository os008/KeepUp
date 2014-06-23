/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.ui.menu.panels.options/IOptionsListener.java
 *
 *			Modified: 23-Jun-2014 (17:32:05)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.ui.menu.panels.options;




/**
 * The listener interface for receiving IOptions events.
 * The class that is interested in processing a IOptions
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIOptionsListener<code> method. When
 * the IOptions event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IOptionsEvent
 */
public interface IOptionsListener
{

	/**
	 * Options set.
	 */
	void optionsSet();
}




