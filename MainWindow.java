import java.awt.BorderLayout;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Settings settings = new Settings();
	
	/**
	 * The main of the application. At this time no handling of command line parameters... And I'm not planning to do it in the future.
	 * @version 0.0.0.1 or lower (23.05.2012)
	 * @param args evaluation not (yet) implemented
	 */
	public static void main(String[] args) {
		new MainWindow();
	}

	/**
	 * Constructor of the applications main window. Generates the UI.
	 */
	MainWindow() {
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
		
		// add vertical scrollpane
		// at this time no need for this one
		// this.add(VerticalScrollPane.getInstance());
		
		// add Sidebar
		Sidebar.getInstance().setParent(this);
		
		// basic chart of JFreeChart
		this.add(helpJFreeChart(), BorderLayout.CENTER);
		
		// add statusbar
		// TODO: die sollte ich mir nochmal ueberlegen!
		this.add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		
		// show me what u got!
		setVisible(true);
	}
	
	private ChartPanel helpJFreeChart() {
		TimeSeries ts = new TimeSeries("Series Name");
		TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
		// data "creation"
		for(int i = 0; i < 2000; i++) {
			ts.add(new FixedMillisecond(i), Math.random());
		}
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Testing", null, "Value", dataset, false, false, false);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}
	
}
