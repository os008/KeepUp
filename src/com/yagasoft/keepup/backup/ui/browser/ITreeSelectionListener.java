/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.ui.browser/ITreeSelectionListener.java
 * 
 *			Modified: 12-Jun-2014 (23:22:14)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.ui.browser;


/**
 * The listener interface for receiving ITreeSelection events.
 * The class that is interested in processing a ITreeSelection
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addITreeSelectionListener<code> method. When
 * the ITreeSelection event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ITreeSelectionEvent
 */
@FunctionalInterface
public interface ITreeSelectionListener
{
	
	/**
	 * Local tree selection changed.
	 *
	 * @param path
	 *            Path.
	 */
	void localTreeSelectionChanged(String path);
}
