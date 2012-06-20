/**
 * Main.java created on 14.06.2012
 */

package gst;

import gst.ui.MainWindow;
import gst.ui.SignalPanel;
import gst.ui.SignalView;
import gst.ui.SignalViewFactory;

/**
 * Class for the public static void main(String[] args) function.
 * @author Enrico Grunitz
 * @version 0.1 (20.06.2012)
 */
public abstract class Main {
	
	private static SignalView[] sv;
	private static final int MAXSIGNALS = 16;
	private static MainWindow main;
	
	/**
	 * The run program function.
	 * @param args command line parameters (not evaluated)
	 */
	public static void main(String[] args) {
		main = new MainWindow();
		
		// adding charts to our SignalPanel
		sv = new SignalView[MAXSIGNALS];
		for(int i = 0; i < MAXSIGNALS; i++) {
			sv[i] = SignalViewFactory.generateRandomChart(1000000);
			SignalPanel.getInstance().addSignal(sv[i]);
		}
		
		main.revalidate();
		main.repaint();

		rescale(0, 1000);
		rescale(1000, 11000);
		rescale(11000, 111000);
		rescale(111000, 311000);
		rescale(311000, 811000);
		rescale(0, 1000000);
		
		DataTest dt = new DataTest();
	}
	
	private static void rescale(double min, double max) {
		long tStart, tMid, tEnd, tElapsed;
		tStart = System.currentTimeMillis();
		for(int i = 0; i < MAXSIGNALS; i++) {
			sv[i].getChart().getXYPlot().getDomainAxis().setRange(min, max);
		}
		tMid = System.currentTimeMillis();
		main.revalidate();
		main.repaint();
		tEnd = System.currentTimeMillis();
		tElapsed = tEnd - tStart;
		System.out.println("(" + min + ", " + max + "): " + (tMid - tStart) + " + " + (tEnd - tMid) + " = " + tElapsed);
		return;
	}
}