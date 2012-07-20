/**
 * MainWindow.java created on 16.05.2012
 */

package gst.ui;

import gst.Settings;

import java.awt.BorderLayout;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution

import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Settings settings = Settings.getInstance();
	
	/**
	 * Constructor of the applications main window. Generates the UI.
	 */
	public MainWindow() {
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
	
}
