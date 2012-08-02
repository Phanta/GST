package gst.ui;
/* Class for the Toolbar of the Program
 * 
 * should contain shortcuts for the different tools
 */

import gst.Settings;
import gst.ui.layout.ComponentArrangement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * Toolbar of the main window.
 * @author Enrico Grunitz
 * @version 0.2.2 (02.08.2012)
 */
public class Toolbar extends JToolBar{
	/** serialization ID */								private static final long serialVersionUID = 1L;
	/** singleton instance of this class */				private static final Toolbar myself = new Toolbar();
	
	/** space in pixel between elements */				private final short space = 5;  
	/** number of text labels */						private final short numLabels = 2;
	/** array of text labels */							private JLabel[] labels;
	
	/** button for closing program */					private JButton btnClose;
	/** button for view mode of SignalPanel */			private JButton btnViewMode;
	/** button for view mode index 1 */					private JButton btnViewModeIndex1;
	/** button for view mode index 2 */					private JButton btnViewModeIndex2;
	/** button for close all views */					private JButton btnCloseViews;
	/** button for opening a new view */				private JButton btnNewView;
	
	/**
	 * Returns Singleton instance of this class.
	 * @return the instance
	 */
	static public Toolbar getInstance() {
		return myself;
	}

	/**
	 * Constructor. One. And. Only.
	 */
	private Toolbar() {
		super();
		
		ActionListener al = null;
		labels = new JLabel[numLabels];
		
		this.setVisible(false);
		this.setSize(800,50);
		
		// system section
		labels[0] = new JLabel("System");											// text label
		this.add(labels[0]);
		this.add(Box.createHorizontalStrut(space));									// space
		btnClose = new JButton("Beenden");											// close button
		this.add(btnClose);
		this.add(Box.createHorizontalStrut(space));									// space
		this.addSeparator();														// separator

		// views
		labels[1] = new JLabel("Ansicht");											// text label
		this.add(labels[1]);
		this.add(Box.createHorizontalStrut(space));									// space
		btnViewMode = new JButton();												// mode select button
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alViewModeSelection(ae);
			}
		};
		btnViewMode.addActionListener(al);
		this.setViewModeButtonText();
		this.add(btnViewMode);
		this.add(Box.createHorizontalStrut(space));									// space
		btnViewModeIndex1 = new JButton();											// index 1 button
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alViewModeIndex1Selection(ae);
			}
		};
		btnViewModeIndex1.addActionListener(al);
		this.setViewModeIndex1ButtonText();
		this.add(btnViewModeIndex1);
		this.add(Box.createHorizontalStrut(space));									// space
		btnViewModeIndex2 = new JButton();											// index 2 button
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alViewModeIndex2Selection(ae);
			}
		};
		btnViewModeIndex2.addActionListener(al);
		this.setViewModeIndex2ButtonText();
		this.add(btnViewModeIndex2);
		this.add(Box.createHorizontalStrut(space));									// space
		btnNewView = new JButton("Neu...");											// new view ...
		this.add(btnNewView);
		this.add(Box.createHorizontalStrut(space));									// space
		btnCloseViews = new JButton("alle schlieﬂen [Strg]");						// close all views
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alCloseViews(ae);
			}
		};
		btnCloseViews.addActionListener(al);
		this.add(btnCloseViews);
		this.add(Box.createHorizontalStrut(space));									// space
		
		this.setTextLabelVisibility();
		this.setVisible(true);
	}
	
	/**
	 * Sets the visibility of text labels according to settings.
	 */
	/* package visibility */ void setTextLabelVisibility() {
		for(int i = 0; i < labels.length; i++) {
			labels[i].setVisible(Settings.getInstance().ui.showToolbarLabels());
		}
	}
	
	/**
	 * Registers an {@code ActionListener} that opens a new view.
	 * @param al the {@code ActionListener}
	 * @return true if successful, else false
	 */
	boolean registerNewView(ActionListener al) {
		if(btnNewView == null) {
			System.out.println("ERROR\ttoolbar new view button doesn't exist - cannot register actionlistener");
			return false;
		}
		btnNewView.addActionListener(al);
		return true;
	}
	
	/**
	 * Registers an {@code ActionListener} that closes the program.
	 * @param al the {@code ActionListener}
	 * @return true if successful, else false
	 */
	boolean registerCloseProgram(ActionListener al) {
		if(btnClose == null) {
			System.out.println("ERROR\ttoolbar close button doesn't exist - cannot register closeing action");
			return false;
		}
		btnClose.addActionListener(al);
		return true;
	}
	
	/**
	 * Cycles through the display mode of the {@code SignalPanel}.
	 * @param ae the {@code ActionEvent}
	 */
	private void alViewModeSelection(ActionEvent ae) {
		ComponentArrangement ca = SignalPanel.getInstance().getComponentArrangement();
		switch(ca.getPattern()) {
		case ComponentArrangement.EVENHEIGHTS:
			ca.setPattern(ComponentArrangement.ONEBIG);
			break;
		case ComponentArrangement.ONEBIG:
			ca.setPattern(ComponentArrangement.TWOMEDIUM);
			break;
		case ComponentArrangement.TWOMEDIUM:
		default:
			ca.setPattern(ComponentArrangement.EVENHEIGHTS);
			break;
		}
		this.setViewModeButtonText();
		this.setViewModeIndex1ButtonText();
		this.setViewModeIndex2ButtonText();
		SignalPanel.getInstance().revalidate();
		MainWindow.getInstance().repaint();
		return;
	}
	
	/**
	 * Sets the text of the view mode button according to the selected view mode of {@code SignalPanel}.
	 */
	private void setViewModeButtonText() {
		final String baseText = "Modus "; 
		ComponentArrangement ca = SignalPanel.getInstance().getComponentArrangement();
		switch(ca.getPattern()) {
		case ComponentArrangement.EVENHEIGHTS:
			this.btnViewMode.setText(baseText + "=");
			break;
		case ComponentArrangement.ONEBIG:
			this.btnViewMode.setText(baseText + "1");
			break;
		case ComponentArrangement.TWOMEDIUM:
			this.btnViewMode.setText(baseText + "2");
			break;
		default:
			this.btnViewMode.setText(baseText + "?");
			break;
		}
		return;
	}
	
	/**
	 * Cycle through the possible indices of views.
	 * @param ae the ActionEvent
	 */
	private void alViewModeIndex1Selection(ActionEvent ae) {
		final int[] index = SignalPanel.getInstance().getComponentArrangement().getSelection();
		final int pattern = SignalPanel.getInstance().getComponentArrangement().getPattern();
		final int maxSignals = SignalPanel.getInstance().getNumSignalViews();
		int nextIndex = 0;
		switch(pattern) {
		case ComponentArrangement.EVENHEIGHTS:
			break;
		case ComponentArrangement.ONEBIG:
			nextIndex = index[ComponentArrangement.INDEX_ONEBIG];
			nextIndex++;
			if(nextIndex >= maxSignals) {
				nextIndex = 0;
			}
			SignalPanel.getInstance().getComponentArrangement().select(ComponentArrangement.INDEX_ONEBIG, nextIndex);
			break;
		case ComponentArrangement.TWOMEDIUM :
			nextIndex = index[ComponentArrangement.INDEX1_TWOMEDIUM];
			nextIndex++;
			if(nextIndex >= maxSignals) {
				nextIndex = 0;
			}
			SignalPanel.getInstance().getComponentArrangement().select(ComponentArrangement.INDEX1_TWOMEDIUM, nextIndex);
			break;
		default:
			break;
		}
		this.setViewModeIndex1ButtonText();
		SignalPanel.getInstance().revalidate();
		MainWindow.getInstance().repaint();
		return;
	}
	
	/**
	 * Set the text of first index-button according to selected index and mode.
	 */
	private void setViewModeIndex1ButtonText() {
		final int[] index = SignalPanel.getInstance().getComponentArrangement().getSelection();
		final int pattern = SignalPanel.getInstance().getComponentArrangement().getPattern();
		switch(pattern) {
		case ComponentArrangement.EVENHEIGHTS:
			// no index required
			this.btnViewModeIndex1.setText("--");
			break;
		case ComponentArrangement.ONEBIG:
			this.btnViewModeIndex1.setText("" + index[ComponentArrangement.INDEX_ONEBIG]);
			break;
		case ComponentArrangement.TWOMEDIUM:
			this.btnViewModeIndex1.setText("" + index[ComponentArrangement.INDEX1_TWOMEDIUM]);
			break;
		default:
			this.btnViewModeIndex1.setText("??");
		}
		return;
	}

	/**
	 * Cycle through the possible indices of views.
	 * @param ae the ActionEvent
	 */
	private void alViewModeIndex2Selection(ActionEvent ae) {
		final int[] index = SignalPanel.getInstance().getComponentArrangement().getSelection();
		final int pattern = SignalPanel.getInstance().getComponentArrangement().getPattern();
		final int maxSignals = SignalPanel.getInstance().getNumSignalViews();
		int nextIndex = 0;
		switch(pattern) {
		case ComponentArrangement.EVENHEIGHTS:
		case ComponentArrangement.ONEBIG:
			break;
		case ComponentArrangement.TWOMEDIUM :
			nextIndex = index[ComponentArrangement.INDEX2_TWOMEDIUM];
			nextIndex++;
			if(nextIndex >= maxSignals) {
				nextIndex = 0;
			}
			SignalPanel.getInstance().getComponentArrangement().select(ComponentArrangement.INDEX2_TWOMEDIUM, nextIndex);
			break;
		default:
			break;
		}
		this.setViewModeIndex2ButtonText();
		SignalPanel.getInstance().revalidate();
		MainWindow.getInstance().repaint();
		return;
	}
	/**
	 * Set the text of second index-button according to selected index and mode.
	 */
	private void setViewModeIndex2ButtonText() {
		final int[] index = SignalPanel.getInstance().getComponentArrangement().getSelection();
		final int pattern = SignalPanel.getInstance().getComponentArrangement().getPattern();
		switch(pattern) {
		case ComponentArrangement.EVENHEIGHTS:
		case ComponentArrangement.ONEBIG:
			// no index required
			this.btnViewModeIndex2.setText("--");
			break;
		case ComponentArrangement.TWOMEDIUM:
			this.btnViewModeIndex2.setText("" + index[ComponentArrangement.INDEX2_TWOMEDIUM]);
			break;
		default:
			this.btnViewModeIndex2.setText("??");
		}
		return;
	}

	/**
	 * Closes all views of the {@link gst.ui.SignalPanel} if the modifier ({@link gst.Settings.UI#getCloseViewsModifier()}) is pressed.
	 * @param event the {@link java.awt.event.ActionEvent}
	 */
	private static void alCloseViews(ActionEvent event) {
		if((event.getModifiers() & Settings.getInstance().ui.getCloseViewsModifier()) != 0) {
			SignalPanel.getInstance().removeAllSignals();
		}
	}
}
