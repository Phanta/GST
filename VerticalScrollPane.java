/* Class for the vertical Scrollpane of the Main Window
 * 
 */

import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class VerticalScrollPane extends JScrollPane{
	private static final long serialVersionUID = 1L;
	private static VerticalScrollPane myself = new VerticalScrollPane();
	
	static VerticalScrollPane getInstance() {
		return myself;
	}

	// one and only Ctor
	protected VerticalScrollPane() {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		// TODO: define Insets for border
	}
}
