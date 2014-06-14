/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 * 
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 * 
 *		Project/File: KeepUp/com.yagasoft.keepup.combinedstorage/ContentListener.java
 * 
 *			Modified: 26-May-2014 (22:45:42)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.combinedstorage;


/**
 * The listener interface for receiving content events.
 * The class that is interested in processing a content
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addContentListener<code> method. When
 * the content event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ContentEvent
 */
@FunctionalInterface
public interface ContentListener
{
	
	/**
	 * Folder changed.
	 *
	 * @param folder
	 *            Folder.
	 * @param update
	 *            Update.
	 * @param content
	 *            Content.
	 */
	public void folderChanged(CombinedFolder folder, UpdateType update, CombinedFolder content);
}
