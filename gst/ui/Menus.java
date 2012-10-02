package gst.ui;
/** Class for the (Main-) Menus of the Program.
 * 
 * contains all the menus including:
 * 		naming of the items
 * 		accelerators
 * 		function calls
 */

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
 * @version 0.1.7 (02.10.2012)
 * @author Enrico Grunitz
 */
public class Menus extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	private static final Menus myself = new Menus();
	private static final String PREFIX_FAIL = "ERROR\tcouldn't register ";
	
	private JMenuItem miCloseProgram;
	private JMenuItem miFileLoad;
	private JMenuItem miSaveAll;
	private JMenuItem miDatasetManager;
	
	private JMenuItem miCreateNewAnnotation;
	private JMenuItem miSelectAnnotation;
	
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
		this.miDatasetManager = new JMenuItem("Datensätze...", KeyEvent.VK_D);
		m.add(this.miDatasetManager);
		miFileLoad = new JMenuItem("Datensatz laden...", KeyEvent.VK_S);
		m.add(miFileLoad);
		mi = new JMenuItem("Annotationen laden...", KeyEvent.VK_A);
		m.add(mi);
		m.addSeparator();
		miSaveAll = new JMenuItem("alles speichern", KeyEvent.VK_S);
		miSaveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		m.add(miSaveAll);
		m.addSeparator();
		miCloseProgram = new JMenuItem("Beenden",KeyEvent.VK_B);
		miCloseProgram.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		m.add(miCloseProgram);
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
		
		// --- ANNOTATIONEN ----------------------------------------------------------
		m = new JMenu("Annotationen");
		m.setMnemonic(KeyEvent.VK_N);
		this.miCreateNewAnnotation = new JMenuItem("Kanal hinzufügen...");
		m.add(this.miCreateNewAnnotation);
		this.miSelectAnnotation = new JMenuItem("Kanal auswählen...");
		m.add(miSelectAnnotation);
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
	
	public boolean registerActionListener(MainWindow.ID id, ActionListener al) {
		switch(id) {
		case openFile:
			miFileLoad.addActionListener(al);
			return true;
		case closeProgram:
			miCloseProgram.addActionListener(al);
			return true;
		case saveAllDatasets:
			miSaveAll.addActionListener(al);
			return true;
		case newAnnotationFile:
			this.miCreateNewAnnotation.addActionListener(al);
			return true;
		case selectAnnotationFile:
			this.miSelectAnnotation.addActionListener(al);
			return true;
		case datasetManager:
			this.miDatasetManager.addActionListener(al);
			return true;
		default:
			return false;
		}
	}

}
