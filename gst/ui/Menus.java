package gst.ui;
/** Class for the (Main-) Menus of the Program.
 * 
 * contains all the menus including:
 * 		naming of the items
 * 		accelerators
 * 		function calls
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Menus represent the Menubar of the Application. Implemented as Singleton.
 * 
 * @version 0.1.2 (27.06.2012)
 * @author Enrico Grunitz
 */
public class Menus extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	private static final Menus myself = new Menus();
	
	private JMenuItem miFileLoad;
	
	/**
	 * @return the Instance of the Menubar
	 */
	public static Menus getInstance() {
		return myself;
	}
	
	protected Menus() {
		JMenu m;
		JMenuItem mi;
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		// --- DATEI ----------------------------------------------------------
		m = new JMenu("Datei");
		m.setMnemonic(KeyEvent.VK_D);
		miFileLoad = new JMenuItem("Signal laden...", KeyEvent.VK_S);
		m.add(miFileLoad);
		mi = new JMenuItem("Annotationen laden...", KeyEvent.VK_A);
		m.add(mi);
		m.addSeparator();
		mi = new JMenuItem("Beenden",KeyEvent.VK_B);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		// quick'n'dirty test of accelerator and actionlistener
			ActionListener al = new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					System.exit( 0 );
				}
			};
			mi.addActionListener(al);		
		m.add(mi);
		this.add(m);
		
		// --- BEARBEITEN ----------------------------------------------------------
		m = new JMenu("Bearbeiten");
		m.setMnemonic(KeyEvent.VK_B);
		mi = new JMenuItem("Annotation kopieren");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		m.add(mi);
		mi = new JMenuItem("Annotation einfügen");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		m.add(mi);
		m.addSeparator();
		mi = new JMenuItem("Einstellungen...", KeyEvent.VK_E);
		m.add(mi);
		this.add(m);
		
		// --- ANSICHT ----------------------------------------------------------
		m = new JMenu("Ansicht");
		m.setMnemonic(KeyEvent.VK_A);
		this.add(m);
		
		// --- UNSICHTBARER FILLER ----------------------------------------------------------
		this.add(Box.createHorizontalGlue());
		
		// --- HILFE ----------------------------------------------------------
		m = new JMenu("Hilfe");
		m.setMnemonic(KeyEvent.VK_H);
		mi = new JMenuItem("Test 1");
		m.add(mi);
		mi = new JMenuItem("Test 2");
		m.add(mi);
		this.add(m);
	}
	
	/**
	 * Registers an {@code ActionListener} for opening files.
	 * @param al the {@code ActionListener} to register
	 * @return true if successful else false
	 */
	/* package visibility */boolean registerOpenFileDialog(ActionListener al) {
		if(miFileLoad == null) {
			System.out.println("ERROR\tcouldn't register open-file-dialog");
			return false;
		}
		miFileLoad.addActionListener(al);
		return true;
	}
}
