package gst.ui;
/* Class for the Toolbar of the Program
 * 
 * should contain shortcuts for the different tools
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar{
	private static final long serialVersionUID = 1L;
	private static Toolbar myself = new Toolbar();
	
	static Toolbar getInstance() {
		return myself;
	}

	// one and only Ctor
	protected Toolbar() {
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
