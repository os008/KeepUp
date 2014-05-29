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
