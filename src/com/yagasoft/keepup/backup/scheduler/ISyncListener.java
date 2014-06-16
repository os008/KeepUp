/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.scheduler/ISyncListener.java
 *
 *			Modified: 16-Jun-2014 (00:05:00)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.scheduler;


import com.yagasoft.overcast.base.container.Container;


/**
 * The listener interface for receiving ISync events.
 * The class that is interested in processing a ISync
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addISyncListener<code> method. When
 * the ISync event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ISyncEvent
 */
public interface ISyncListener
{

	/**
	 * Container has been sync'd.
	 *
	 * @param container            Container.
	 * @param revision Revision of file just sync'd.
	 */
	void containerSynced(Container<?> container, String revision);
}
