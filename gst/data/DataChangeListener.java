/**
 * DataChangedListener.java created on 08.10.2012
 */

package gst.data;

/**
 * Interface for classes that react on changing data.
 * @author Enrico Grunitz
 * @version 0.0.0.1 (08.10.2012)
 */
public interface DataChangeListener {

	public void dataChangeReaction(DataController source);
	
}
