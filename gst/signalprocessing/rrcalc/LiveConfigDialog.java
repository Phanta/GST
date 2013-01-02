/**
 * LiveConfigDialog.java created on 16.10.2012
 */

package gst.signalprocessing.rrcalc;

import gst.data.BufferedValueController;
import gst.data.DataController;
import gst.signalprocessing.LiveSignalProcessor;
import gst.ui.Menus;

/**
 * Configuration dialog for {@link gst.signalprocessing.rrcalc.RRLiveCalculator}
 * @author Enrico Grunitz
 * @version 0.0.0.1 (16.10.2012)
 */
public class LiveConfigDialog extends ConfigDialog {
	@Override protected void createSignalProcessor(DataController source, BufferedValueController target) {
		LiveSignalProcessor processor = new RRLiveCalculator(source, target);
		processor.start();
		Menus.getInstance().registerStartedLiveSignalProcess(processor);
	}
}
