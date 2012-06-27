package gst.ui;
/* Class for the Toolbar of the Program
 * 
 * should contain shortcuts for the different tools
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * Toolbar of the main window.
 * @author Enrico Grunitz
 * @version 0.1 (27.06.2012)
 */
public class Toolbar extends JToolBar{
	/** srialization ID */								private static final long serialVersionUID = 1L;
	/** singleton instance of this class */				private static final Toolbar myself = new Toolbar();
	
	/**
	 * Returns Singleton instance of this class.
	 * @return the instance
	 */
	static Toolbar getInstance() {
		return myself;
	}

	/**
	 * Constructor. One. And. Only.
	 */
	private Toolbar() {
		this.setSize(800,50);
		JButton btn01 = new JButton("Beenden");
		// quick'n'dirty test actionlistener
			ActionListener al = new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					System.exit( 0 );
				}
			};
			btn01.addActionListener(al);		
		this.add(btn01);
	}
	
}
