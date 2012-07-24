/**
 * MainWindow.java created on 16.05.2012
 */

package gst.ui;

import gst.Settings;

import java.awt.BorderLayout;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Settings settings = Settings.getInstance();
	private static final MainWindow myself = new MainWindow();
	
	/** ID for open-file-dialog */				public static final int IDOpenFile = 1;
	/** ID for close program */					public static final int IDCloseProgram = 2;
	
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

		// define default behavior
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle(settings.ui.getMainWindowTitle());
		
		// add Menus
		this.setJMenuBar(Menus.getInstance());
		
		// add Toolbar
		this.add(Toolbar.getInstance(), BorderLayout.PAGE_START);
		
		// add Sidebar
		this.add(Sidebar.getInstance(), BorderLayout.LINE_START);
		
		// basic chart of JFreeChart
		this.add(SignalPanel.getInstance(), BorderLayout.CENTER);
		
		// add statusbar
		// FIXME i don't like this style of status bar, maybe replace it with some homebrew
		this.add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		
		// show me what u got!
		this.setVisible(true);
		//this.revalidate();
	}
	
	/**
	 * Registers an open-file-dialog to this window and all it's sub-components. 
	 * @param al the {@code ActionListener}
	 * @return true if successful
	 */
	@Deprecated
	public boolean registerOpenFileDialog(ActionListener al) {
		if(al == null) {
			throw new NullPointerException("ActionListener must be non-null");
		}
		boolean retVal = true;
		retVal &= Menus.getInstance().registerOpenFileDialog(al);
		return retVal;
	}
	
	/**
	 * Registers an {@code ActioinListener} to this window and all it's sub components for the specified action-ID.
	 * @param ID ID specifying the action to perform
	 * @param al the {@code ActionListener}
	 * @return true if registering was successful, else false
	 */
	public boolean registerActionListener(int ID, ActionListener al) {
		if(al == null) {
			throw new NullPointerException("ActionListener must be non-null");
		}
		boolean retVal = true;
		switch(ID) {
		case IDOpenFile:
			retVal &= Menus.getInstance().registerOpenFileDialog(al);
			break;
		case IDCloseProgram:
			retVal &= Menus.getInstance().registerCloseProgram(al);
			retVal &= Toolbar.getInstance().registerCloseProgram(al);
			break;
		default:
			retVal = false;
			break;
		}
		return retVal;
	}
}
