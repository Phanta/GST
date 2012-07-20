package gst.ui;
/* Class for the Toolbar of the Program
 * 
 * should contain shortcuts for the different tools
 */

import gst.Settings;
import gst.ui.layout.ComponentArrangement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * Toolbar of the main window.
 * @author Enrico Grunitz
 * @version 0.1 (27.06.2012)
 */
public class Toolbar extends JToolBar{
	/** serialization ID */								private static final long serialVersionUID = 1L;
	/** singleton instance of this class */				private static final Toolbar myself = new Toolbar();
	
	/** number of text labels */						private final short numLabels = 2;
	/** array of text labels */							private JLabel[] labels;
	
	/** button for view mode of SignalPanel */			private JButton btnViewMode;
	/** button for view mode index 1 */					private JButton btnViewModeIndex1;
	/** button for view mode index 2 */					private JButton btnViewModeIndex2;
	
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
		labels[0] = new JLabel("System");
		this.add(labels[0]);
		JButton btnClose = new JButton("Beenden");
		// quick'n'dirty test actionlistener
		al = new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		};
		btnClose.addActionListener(al);		
		this.add(btnClose);
		this.addSeparator();
		// views
		labels[1] = new JLabel("Ansicht");
		this.add(labels[1]);
		btnViewMode = new JButton();
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alViewModeSelection(ae);
			}
		};
		btnViewMode.addActionListener(al);
		this.setViewModeButtonText();
		this.add(btnViewMode);
		btnViewModeIndex1 = new JButton();
		al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alViewModeIndex1Selection(ae);
			}
		};
		btnViewModeIndex1.addActionListener(al);
		this.setViewModeIndex1ButtonText();
		this.add(btnViewModeIndex1);
		
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
	 * Cycles throw the display mode of the {@code SignalPanel}.
	 * @param ae the {@code ActionEvent}
	 */
	private void alViewModeSelection(ActionEvent ae) {
		//DEBUG
		System.out.println(javax.swing.SwingUtilities.isEventDispatchThread());
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
		SignalPanel.getInstance().revalidate();
		return;
	}
	
	/**
	 * Sets the text of the view mode button according to the selected view mode of {@code SignalPanel}.
	 */
	private void setViewModeButtonText() {
		ComponentArrangement ca = SignalPanel.getInstance().getComponentArrangement();
		switch(ca.getPattern()) {
		case ComponentArrangement.EVENHEIGHTS:
			this.btnViewMode.setText("=");
			break;
		case ComponentArrangement.ONEBIG:
			this.btnViewMode.setText("1");
			break;
		case ComponentArrangement.TWOMEDIUM:
			this.btnViewMode.setText("2");
			break;
		default:
			this.btnViewMode.setText("?");
			break;
		}
		return;
	}
	
	private void alViewModeIndex1Selection(ActionEvent ae) {
		return;
	}
	
	private void setViewModeIndex1ButtonText() {
		return;
	}
	
}
