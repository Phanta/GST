/**
 * MainWindow.java created on 16.05.2012
 */

package gst.ui;

import gst.Settings;
import gst.test.Debug;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
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
		Debug.println(Debug.mainWindow, "MainWindow.size: " + this.getWidth() + ", " + this.getHeight());
		// register listener for event-forwarding
		//this.addMouseListener(new MainWindowMouseForwarder());

		// define default behavior
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle(settings.ui.getMainWindowTitle());
		
		// add Menus
		this.setJMenuBar(Menus.getInstance());
		Menus.getInstance().addMouseListener(new NamedMouseAdapter("Menus"));
		
		// add Toolbar
		this.add(Toolbar.getInstance(), BorderLayout.PAGE_START);
		//Toolbar.getInstance().addMouseListener(new NamedMouseAdapter("Toolbar"));
		
		// add Sidebar
		// DEBUG debugpanel for west (SideBar replacement) 
		// FIXME Sidebar causes MouseEvent loss on CENTER component
				JPanel debugPanelWest = new JPanel();
				debugPanelWest.setBackground(Color.red);
				debugPanelWest.addMouseListener(new NamedMouseAdapter("DebugPanelWest"));
				debugPanelWest.setSize(200, 400);
				this.add(debugPanelWest, BorderLayout.LINE_START);
		//this.add(Sidebar.getInstance(), BorderLayout.LINE_START);
		//Sidebar.getInstance().addMouseListener(new NamedMouseAdapter("Sidebar"));
		
		// basic chart of JFreeChart
		//this.getContentPane().add(SignalPanel.getInstance());
		// DEBUG debugpanel for center
//				JPanel debugPanelCent = new JPanel();
//				debugPanelCent.setBackground(Color.green);
//				debugPanelCent.addMouseListener(new NamedMouseAdapter("DebugPanelCenter"));
//				this.add(debugPanelCent, BorderLayout.CENTER);
		Debug.println(Debug.mainWindow, "SignalPanel: " + SignalPanel.getInstance().hashCode());
		this.add(SignalPanel.getInstance(), BorderLayout.CENTER);
		SignalPanel.getInstance().addMouseListener(new NamedMouseAdapter("SignalPanel"));

		// add statusbar
		// FIXME i don't like this style of status bar, maybe replace it with some homebrew
		this.add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		StatusBar.getInstance().addMouseListener(new NamedMouseAdapter("StatusBar"));
		
		// show me what u got!
		this.setVisible(true);
		return;
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
	
	/**
	 * {@code MouseAdapter} for the main window forwards the mouse events to it's child components.
	 * @author Enrico Grunitz
	 * @version 0.1 (30.07.2012)
	 */
	@Deprecated
	private class MainWindowMouseForwarder extends NamedMouseAdapter {
		/** basic constructor */ 
		public MainWindowMouseForwarder() {
			super("MainWindow");
		}
		/** @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent) */
		@Override
		public void mouseClicked(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseClicked on " + this.getComponentName() +
															  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent) */
		@Override
		public void mouseDragged(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseDragged on " + this.getComponentName() +
															  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see gst.ui.NamedMouseAdapter#mouseEntered(java.awt.event.MouseEvent) */
		@Override
		public void mouseEntered(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseEntered on " + this.getComponentName() +
						  									  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see gst.ui.NamedMouseAdapter#mouseExited(java.awt.event.MouseEvent) */
		@Override
		public void mouseExited(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseExited on " + this.getComponentName() +
						  									  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see gst.ui.NamedMouseAdapter#mouseMoved(java.awt.event.MouseEvent) */
		@Override
		public void mouseMoved(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseMoved on " + this.getComponentName() +
						  									  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent) */
		@Override
		public void mousePressed(MouseEvent event) {
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MousePressed on " + this.getComponentName() +
						  									  " NOT forwarded! Event: " + event.toString());
			}
		}
		/** @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent) */
		@Override
		public void mouseReleased(MouseEvent event) {
			Debug.println(Debug.mainWindowMouseForwarder, "Mouse released @" + event.getX() + "," + event.getY());
			Debug.println(Debug.mainWindowMouseForwarder, "SignalPanel position: " + SignalPanel.getInstance().getBounds().toString());
			if(forward(event) != true) {
				Debug.println(Debug.mainWindowMouseForwarder, "MouseReleased on " + this.getComponentName() +
						  									  " NOT forwarded! Event: " + event.toString());
			}
		}
				
		/**
		 * Tries to forward the {@code MouseEvent} to the deepest displayed {@code Component}.
		 * @param event the {@code MouseEvent} to forward
		 * @return true if forward is successful, false if deepest {@code Component} is {@link gst.ui.MainWindow} 
		 */
		private boolean forward(MouseEvent event) {
			
			Component target = SwingUtilities.getDeepestComponentAt(MainWindow.getInstance(), event.getPoint().x, event.getPoint().y);
			if(target != (Component)MainWindow.getInstance()) {
				if(target != null) {
					Debug.println(Debug.mainWindowMouseForwarder, "forwarding " + event.toString() + "\n\tto " + target.toString());
					//event.setSource(this);
					target.dispatchEvent(event);
					return true;
				} else {
					Debug.println(Debug.mainWindowMouseForwarder, "target is null");
					return false;
				}
			}
			return false;
		}
	}
}
