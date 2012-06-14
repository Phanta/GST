/**
 * Main.java created on 14.06.2012
 */

package gst;

import gst.ui.MainWindow;
import gst.ui.SignalPanel;
import gst.ui.SignalViewFactory;

/**
 * Class for the public static void main(String[] args) function.
 * @author Enrico Grunitz
 * @version 0.1 (14.06.2012)
 */
public abstract class Main {
	/**
	 * The run program function.
	 * @param args command line parameters (not evaluated)
	 */
	public static void main(String[] args) {
		MainWindow main = new MainWindow();
		
		// adding charts to our SignalPanel
		SignalPanel.getInstance().addSignal(SignalViewFactory.generateRandomChart(2000));
		SignalPanel.getInstance().addSignal(SignalViewFactory.generateRandomChart(2000));
		SignalPanel.getInstance().addSignal(SignalViewFactory.generateRandomChart(2000));
		SignalPanel.getInstance().addSignal(SignalViewFactory.generateRandomChart(1000));
		// SignalPanel.getInstance().addSignal(SignalViewFactory.generateRandomCombinedChart(2000, 3, new int[]{2, 6, 2}));
		main.revalidate();
		main.repaint();
	}
}
