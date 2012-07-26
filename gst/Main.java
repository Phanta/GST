/**
 * Main.java created on 14.06.2012
 */

package gst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import gst.data.DataController;
import gst.data.UnisensDataset;

import gst.test.DataTest;

import gst.ui.MainWindow;
import gst.ui.Menus;
import gst.ui.Sidebar;
import gst.ui.SignalPanel;
import gst.ui.SignalView;
import gst.ui.SignalViewFactory;
import gst.ui.StatusBar;
import gst.ui.Toolbar;

/**
 * Class for the public static void main(String[] args) function.
 * @author Enrico Grunitz
 * @version 0.1 (19.07.2012)
 */
public abstract class Main {
	
	private static SignalView[] sv;
	private static final int MAXSIGNALS = 0;
	private static MainWindow main;
	private static ArrayList<UnisensDataset> datasets;
	
	/**
	 * I give you three chances to guess the purpose of this function. Hint: !cigam s'tI
	 * @param args command line parameters (not evaluated)
	 */
	public static void main(String[] args) {
		datasets = new ArrayList<UnisensDataset>();
		main = MainWindow.getInstance();
		
		main.registerActionListener(MainWindow.IDOpenFile, new ActionListener() {
																public void actionPerformed(ActionEvent ae) {
																	loadUnisensData(ae);
																}
															});
		main.registerActionListener(MainWindow.IDCloseProgram, new ActionListener() {
																	public void actionPerformed(ActionEvent ae) {
																		closeProgram(ae);
																	}
																});
		
		sv = new SignalView[MAXSIGNALS];
		generateSignalViews(MAXSIGNALS, 2000);
		
		main.revalidate();
		main.repaint();

		DataTest dt = new DataTest();
		dt.arrayTest();
		dt.testGenerate();
		dt.testLoad();
		dt.loadAndPrintIds();
		dt.loadAndPrintContentClasses();
		dt.loadAndPrintEntryTypes();
		dt.signalControllerTest();
		dt.testControlledSignalView();
		dt.testAnnotationZoom();
		dt.testValueController();
		dt.testAnnotationController();
		dt.testMultiController();
		main.revalidate();
		main.repaint();
		
		return;
	}
	
	/**
	 * Displays an open-file-dialog and loads the received dataset.
	 * @param ae {@code ActionEvent} of the triggering Action
	 */
	private static void loadUnisensData(ActionEvent ae) {
		String userHomeDir = System.getProperty("user.home");
		JFileChooser dialog = new JFileChooser(new File(userHomeDir));
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("XML Datei", "xml");
		dialog.addChoosableFileFilter(filter);
		int dialogReturnValue = dialog.showOpenDialog(main);
		if(dialogReturnValue == JFileChooser.APPROVE_OPTION) {
			String path;
			// selecting the directory which contains the unisens.xml
			if(dialog.getSelectedFile().isDirectory() ==  false) {
				path = dialog.getSelectedFile().getParentFile().getAbsolutePath();
			} else {
				path = dialog.getSelectedFile().getAbsolutePath();
			}
			UnisensDataset usds = new UnisensDataset(path, true);
			datasets.add(usds);
			createSignalViews(usds);
			main.revalidate();
			main.repaint();
		}
		return;
	}
	
	/**
	 * Creates {@link gst.ui.SignalView}s for all {@code Entry}s of the given {@link gst.data.Unisensdataset} and adds them to the
	 * {@link gst.ui.SignalPanel}.
	 * @param ds {@code UnisensDataset} to use
	 */
	private static void createSignalViews(UnisensDataset ds) {
		List<DataController> ctrlList = ds.createControllers();
		Iterator<DataController> it = ctrlList.iterator();
		while(it.hasNext()) {
			SignalPanel.getInstance().addSignal(SignalView.createControlledView(it.next()));
		}
	}
	
	/**
	 * Exits the program without saving changes to unisens databases.
	 * @param ae {@code ActionEvent} causing this action
	 */
	private static void closeProgram(ActionEvent ae) {
		Iterator<UnisensDataset> it = datasets.iterator();
		while(it.hasNext()) {
			it.next().close();
		}
		System.exit(0);
	}
	
	@Deprecated
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
