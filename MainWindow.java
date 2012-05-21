import javax.swing.JFrame;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Toolkit;	// screenresolution
import java.awt.Dimension;	// dito

import org.jfree.chart.*;	// just for testing purpose
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;


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
		
		// add vertical scrollpane
		this.add(VerticalScrollPane.getInstance());
		
		this.add(testJFC());
		
		// add statusbar
		// TODO: die sollte ich mir nochmal ueberlegen!
		this.add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		
		// show me what u got!
		setVisible(true);
	}
	
	private static ChartPanel testJFC() {
		// generate dataset
		final DefaultXYDataset ds = new DefaultXYDataset();
		ds.addSeries("testSeries", new double[][] {{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8}, {1.0, 1.1, 2.1, 3.1, 3.4, 2.7, 1.6, 1.3, 1.1}});
		
		// generate chart
		JFreeChart theChart = ChartFactory.createXYLineChart("Titel", "XLabel", "YLabel", ds, PlotOrientation.VERTICAL, false, false, false);
		
		// generate swing component
		ChartPanel pan = new ChartPanel(theChart,true);
		return pan;
	}
	
}
