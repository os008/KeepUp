/*
 * Copyright (C) 2011-2014 by Ahmed Osama el-Sawalhy
 *
 *		The Modified MIT Licence (GPL v3 compatible)
 * 			Licence terms are in a separate file (LICENCE.md)
 *
 *		Project/File: KeepUp/com.yagasoft.keepup.backup.watcher/WatchListener.java
 *
 *			Modified: 12-Jun-2014 (23:23:40)
 *			   Using: Eclipse J-EE / JDK 8 / Windows 8.1 x64
 */

package com.yagasoft.keepup.backup.watcher;


import java.nio.file.WatchEvent;

import com.yagasoft.keepup.backup.State;
import com.yagasoft.overcast.base.container.Container;


/**
 * The listener interface for receiving watch events.
 * The class that is interested in processing a watch
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addWatchListener<code> method. When
 * the watch event occurs, that object's appropriate
 * method is invoked.
 *
 * @see WatchEvent
 */
public interface IWatchListener
{
	
	/**
	 * Watch list has changed.
	 *
	 * @param container
	 *            Container.
	 * @param state
	 *            State.
	 */
	void watchListChanged(Container<?> container, State state);
}
