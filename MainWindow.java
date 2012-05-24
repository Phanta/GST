import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;	// Screenresolution
import java.awt.Dimension;	// Screenresolution

import javax.swing.JFrame;
import javax.swing.UIManager;

import info.monitorenter.gui.chart.Chart2D; // testing JChart2D
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.views.ChartPanel;


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
		this.add(VerticalScrollPane.getInstance());
		
		// add Sidebar
		Sidebar.getInstance().setParent(this);
		
		// testing of JChart2D
		this.add(testJC2D());
		
		// add statusbar
		// TODO: die sollte ich mir nochmal ueberlegen!
		this.add(StatusBar.getInstance(), BorderLayout.PAGE_END);
		
		// show me what u got!
		setVisible(true);
	}
	
	private static ChartPanel testJC2D() {
		Chart2D chart = new Chart2D();
		
		// Create an ITrace:
	    // Note that dynamic charts need limited amount of values!!!
	    // ITrace2D trace = new Trace2DLtd(200);
	    ITrace2D trace = new Trace2DSimple();
	    trace.setColor(Color.RED);

	    // Add the trace to the chart:
	    chart.addTrace(trace);

	    // Add all points, as it is static:
	    double time = System.currentTimeMillis();
	    for (int i = 0; i < 5000; i++) {
	      trace.addPoint(time + 1000 * 60 * i, Math.random() * i);
	    }

	    chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);

	    chart.getAxisY().setPaintScale(false);
	    chart.getAxisX().setPaintScale(true);		
		
		return new ChartPanel(chart);
	}
	
}
