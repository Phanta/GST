import javax.swing.JFrame;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Toolkit;	// screenresolution
import java.awt.Dimension;	// dito


public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Dimension dimWindow = new Dimension(1024, 768);
	private static String strTitle = "Signal Display Tool";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainWindow();
	}

	MainWindow() {
		// native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Couldn't set native Look and Feel.");
		}
		
		// put window in the middle of screen with default size
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dimScreenResolution = tk.getScreenSize();
		int x = (dimScreenResolution.width - dimWindow.width) / 2;
		int y = (dimScreenResolution.height - dimWindow.height) / 2;
		this.setBounds(x, y, dimWindow.width, dimWindow.height);

		// define default behavior
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle(strTitle);
		
		// add Menus
		this.setJMenuBar(Menus.getInstance());
		
		// add Toolbar
		this.add(Toolbar.getInstance(), BorderLayout.PAGE_START);
		
		// show me what u got!
		setVisible(true);
	}
}
