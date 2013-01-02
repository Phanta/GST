package gst.ui;
/** Class for the (Main-) Menus of the Program.
 * 
 * contains all the menus including:
 * 		naming of the items
 * 		accelerators
 * 		function calls
 */

import gst.signalprocessing.LiveSignalProcessor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Menus represent the Menubar of the Application. Implemented as Singleton.
 * 
 * @version 0.1.8.0 (16.10.2012)
 * @author Enrico Grunitz
 */
public class Menus extends JMenuBar implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	/** singleton instance of this class */				private static final Menus myself = new Menus();
	/** constant string for debug messages */			private static final String PREFIX_FAIL = "ERROR\tcouldn't register ";
	
	/** {@code MenuItem} for closing the program */				private JMenuItem miCloseProgram;
	//private JMenuItem miFileLoad;
	/** {@code MenuItem} for saving all datasets*/				private JMenuItem miSaveAll;
	/** {@code MenuItem} for opening dataset manager dialog*/	private JMenuItem miDatasetManager;
	
	/** {@code MenuItem} for annotation channel creation*/		private JMenuItem miCreateNewAnnotation;
	/** {@code MenuItem} for slecting annotation channel*/		private JMenuItem miSelectAnnotation;
	
	/** {@code MenuItem} for starting {@link gst.signalprocessing.rrcalc.RRCalculator} */
																private JMenuItem miFuncRRCalc;
	/** {@code MenuItem} for starting {@link gst.signalprocessing.rrcalc.RRLiveCalculator} */
																private JMenuItem miFuncRRLiveCalc;
	
	/** {@code Menu} for running {@link gst.signalprocessing.LiveSignalProcessor} */
																private JMenu mRunningSP;
	/** collection of {@code MenuItem} for running {@link gst.signalprocessing.LiveSignalProcessor} */
																private ArrayList<RunningSignalProcessorMenuItem> runningSPList;
	
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
		this.runningSPList = new ArrayList<RunningSignalProcessorMenuItem>();
		
		// --- DATEI ----------------------------------------------------------
		m = new JMenu("Datei");
		m.setMnemonic(KeyEvent.VK_D);
		this.miDatasetManager = new JMenuItem("Datensätze...", KeyEvent.VK_D);
		m.add(this.miDatasetManager);
		//miFileLoad = new JMenuItem("Datensatz laden...", KeyEvent.VK_S);
		//m.add(miFileLoad);
		mi = new JMenuItem("Annotationen laden...", KeyEvent.VK_A);
		mi.setEnabled(false);
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
		m.setEnabled(false);
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
		
		// --- FUNKTION ----------------------------------------------------------
		m = new JMenu("Funktion");
		m.setMnemonic(KeyEvent.VK_F);
		this.miFuncRRCalc = new JMenuItem("RR Calculator");
		// quick and dirty actionlistener
		this.miFuncRRCalc.addActionListener(new gst.signalprocessing.rrcalc.ConfigDialog());
		m.add(this.miFuncRRCalc);

		this.miFuncRRLiveCalc = new JMenuItem("RR Calculator (Live)");
		this.miFuncRRLiveCalc.addActionListener(new gst.signalprocessing.rrcalc.LiveConfigDialog());
		m.add(this.miFuncRRLiveCalc);
		this.mRunningSP = new JMenu("laufende Funktionen");
		m.add(this.mRunningSP);
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
		m.setEnabled(false);
		this.add(m);
		
		// --- UNSICHTBARER FILLER ----------------------------------------------------------
		this.add(Box.createHorizontalGlue());
		
		// --- HILFE ----------------------------------------------------------
		m = new JMenu("Hilfe");
		m.setMnemonic(KeyEvent.VK_H);
		m.setEnabled(false);
		mi = new JMenuItem("Test 1");
		m.add(mi);
		mi = new JMenuItem("Test 2");
		m.add(mi);
		this.add(m);
	}
	
	public boolean registerActionListener(MainWindow.ID id, ActionListener al) {
		switch(id) {
		case openFile:
			//miFileLoad.addActionListener(al);
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
	
	/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
	@Override public void actionPerformed(ActionEvent event) {
		RunningSignalProcessorMenuItem rspmi = null;
		Iterator<RunningSignalProcessorMenuItem> it = this.runningSPList.iterator();
		while(it.hasNext()) {
			rspmi = it.next();
			if(rspmi.getMenuItem() == event.getSource()) {
				// if event-source is one of our created menuitems stop the processor and remove the menuitem
				rspmi.getSignalProcessor().stop();
				this.mRunningSP.remove(rspmi.getMenuItem());
				return;
			}
		}
		return;
	}
	
	/**
	 * Registers a {@link gst.signalprocessing.LiveSignalProcessor} to the menu. A new entry is created to enable the user to
	 * stop it.
	 * @param sigProcess the {@code LiveSignalProcessor} to register
	 */
	public void registerStartedLiveSignalProcess(LiveSignalProcessor sigProcess) {
		RunningSignalProcessorMenuItem rspmi = new RunningSignalProcessorMenuItem(sigProcess);
		rspmi.getMenuItem().addActionListener(this);
		this.mRunningSP.add(rspmi.getMenuItem());
		this.runningSPList.add(rspmi);
	}
	
	/**
	 * Simple class to save a running {@link gst.signalprocessing.LiveSignalProcessor} and a {@code JMenuItem} together.
	 * @author Enrico Grunitz
	 * @version 0.0.0.1 (16.10.2012)
	 */
	private class RunningSignalProcessorMenuItem {
		private LiveSignalProcessor sp;
		private JMenuItem mi;
		
		public RunningSignalProcessorMenuItem(LiveSignalProcessor signalProcessor) {
			if(signalProcessor == null) {
				throw new NullPointerException();
			}
			this.sp = signalProcessor;
			this.mi = new JMenuItem("Stoppe " + signalProcessor.toString());
		}
		
		public JMenuItem getMenuItem() {
			return this.mi;
		}
		
		public LiveSignalProcessor getSignalProcessor() {
			return this.sp;
		}
	}
}
