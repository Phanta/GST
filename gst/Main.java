/**
 * Main.java created on 14.06.2012
 */

package gst;

import gst.test.DataTest;
import gst.ui.MainWindow;
import gst.ui.SignalPanel;
import gst.ui.SignalView;
import gst.ui.SignalViewFactory;

/**
 * Class for the public static void main(String[] args) function.
 * @author Enrico Grunitz
 * @version 0.1 (17.07.2012)
 */
public abstract class Main {
	
	private static SignalView[] sv;
	private static final int MAXSIGNALS = 5;
	private static MainWindow main;
	
	/**
	 * I give you three chances to guess the purpose of this function. Hint: !cigam s'tI
	 * @param args command line parameters (not evaluated)
	 */
	public static void main(String[] args) {
		main = new MainWindow();
		
		sv = new SignalView[MAXSIGNALS];
		generateSignalViews(MAXSIGNALS, 2000);
		
		main.revalidate();
		main.repaint();

		DataTest dt = new DataTest();
		dt.testGenerate();
		dt.testLoad();
		dt.loadAndPrintIds();
		dt.loadAndPrintContentClasses();
		dt.loadAndPrintEntryTypes();
		dt.signalControllerTest();
		dt.arrayTest();
		dt.testControlledSignalView();
		main.revalidate();
		main.repaint();
		dt.testValueController();

		return;
	}
	
	private static void generateSignalViews(int numSignals, int numDataPoints) {
		for(int i = 0; i < numSignals; i++) {
			sv[i] = SignalViewFactory.generateRandomChart(numDataPoints);
			SignalPanel.getInstance().addSignal(sv[i]);
		}
		return;
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
