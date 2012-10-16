/**
 * DataChangedListener.java created on 08.10.2012
 */

package gst.data;

import java.util.EventListener;

/**
 * Interface for classes that react on changing data.
 * @author Enrico Grunitz
 * @version 0.0.0.2 (16.10.2012)
 */
public interface DataChangeListener extends EventListener {

	public void dataChangeReaction(DataChangeEvent event);
	
}
