/**
 * MainWindow.java created on 16.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.test.Debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * The main window of the application.
 * @author Enrico Grunitz
 * @version 0.2.4.2 (15.10.2012)
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	/** instance of settings*/							private static Settings settings = Settings.getInstance();
	/** singleton instance of this class */				private static final MainWindow myself = new MainWindow();
	
	public static enum ID {
		openFile,
		closeProgram,
		openNewView,
		saveAllDatasets,
		newAnnotationFile,
		selectAnnotationFile,
		datasetManager;
	}
	
	public static MainWindow getInstance() {
		return myself;
	}
	
	/**
	 * Constructor of the applications main window. Generates the UI.
	 */
	private MainWindow() {
		// native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Master, I couldn't activate native Look'n'Feel - I'm unworthy.");
		}
		
		// put window in the middle of screen with default size
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dimScreenResolution = tk.getScreenSize();
		int x = (dimScreenResolution.width - settings.ui.getMainWindowDimension().width) / 2;
		int y = (dimScreenResolution.height - settings.ui.getMainWindowDimension().height) / 2;
		this.setBounds(x, y, settings.ui.getMainWindowDimension().width, settings.ui.getMainWindowDimension().height);
		Debug.println(Debug.mainWindow, "MainWindow.size: " + this.getWidth() + ", " + this.getHeight());

		// define default behavior
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle(settings.ui.getMainWindowTitle());
		
		// set layout
		this.getContentPane().setLayout(new BorderLayout());
		
		// add Menus
		this.setJMenuBar(Menus.getInstance());
		Menus.getInstance().addMouseListener(new NamedMouseAdapter("Menus"));
		
		// add Toolbar
		this.getContentPane().add(Toolbar.getInstance(), BorderLayout.PAGE_START);
		
		// add Sidebar
		JPanel debugPanelWest = new JPanel();
		debugPanelWest.setBackground(Color.red);
		debugPanelWest.addMouseListener(new NamedMouseAdapter("DebugPanelWest"));
		debugPanelWest.setSize(200, 400);
		this.getContentPane().add(debugPanelWest, BorderLayout.LINE_START);
		
		// add SignalPanel
		Debug.println(Debug.mainWindow, "SignalPanel: " + SignalPanel.getInstance().hashCode());
		this.getContentPane().add(SignalPanel.getInstance(), BorderLayout.CENTER);
		SignalPanel.getInstance().addMouseListener(new NamedMouseAdapter("SignalPanel"));

		// add statusbar
		// FIXME i don't like this style of status bar, maybe replace it with some homebrew
		this.getContentPane().add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		StatusBar.getInstance().addMouseListener(new NamedMouseAdapter("StatusBar"));
		
		// show me what u got!
		this.setVisible(true);
		return;
	}
	
	/**
	 * Registers an {@code ActioinListener} to this window and all it's sub components for the specified action-ID.
	 * @param id ID specifying the action to perform
	 * @param al the {@code ActionListener}
	 * @return true if registering was successful, else false
	 */
	public boolean registerActionListener(ID id, ActionListener al) {
		if(al == null) {
			throw new NullPointerException("ActionListener must be non-null");
		}
		// retVal makes no sense using unified registration
		Menus.getInstance().registerActionListener(id, al);
		boolean retVal = true;
		switch(id) {
		case openFile:
			//retVal &= Menus.getInstance().registerOpenFileDialog(al);
			break;
		case closeProgram:
			//retVal &= Menus.getInstance().registerCloseProgram(al);
			retVal &= Toolbar.getInstance().registerCloseProgram(al);
			break;
		case openNewView:
			retVal &= Toolbar.getInstance().registerNewView(al);
			break;
		case saveAllDatasets:
			//retVal &= Menus.getInstance().registerSaveAllData(al);
			break;
		default:
			retVal = false;
			break;
		}
		return retVal;
	}
}
