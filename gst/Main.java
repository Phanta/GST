/**
 * Main.java created on 14.06.2012
 */

package gst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import gst.data.AnnotationController;
import gst.data.AnnotationManager;
import gst.data.DataController;
import gst.data.DatasetList;
import gst.data.UnisensDataset;

import gst.test.DataTest;
import gst.test.Debug;

import gst.ui.MainWindow;
import gst.ui.SignalPanel;
import gst.ui.SignalView;
import gst.ui.SignalViewFactory;
import gst.ui.StatusBar;
import gst.ui.dialog.AnnotationSelectionDialog;
import gst.ui.dialog.DataSelectionDialog;
import gst.ui.dialog.DatasetManagerDialog;
import gst.ui.dialog.DatasetSelectionDialog;
import gst.ui.dialog.EnterFileNameDialog;

/**
 * Entry Point of the program. Class for the {@code public static void main(String[] args)} method.
 * @author Enrico Grunitz
 * @version 0.1.6.2 (08.10.2012)
 */
public abstract class Main {
	
	/** old variable for {@link gst.ui.SignalView} handling */			@Deprecated private static SignalView[] sv;
	/** constant number of random signal {@link gst.ui.SignalView} */	@Deprecated private static final int MAXSIGNALS = 0;

	/** instance of {@link gst.ui.MainWindow} */						private static MainWindow main;
	/** instance of {@link gst.data.DatasetList} */						private static DatasetList datasetList;
	/** instance of {@link gst.data.AnnotationManager} */				private static AnnotationManager annotationManager;
	
	/**
	 * I give you three chances to guess the purpose of this function. Hint: !cigam s'tI
	 * @param args command line parameters (not evaluated)
	 */
	public static void main(String[] args) {
		//datasets = new ArrayList<UnisensDataset>();
		datasetList = DatasetList.getInstance();
		main = MainWindow.getInstance();
		annotationManager = new AnnotationManager();
		
		Debug.println(Debug.main, "Mainwindow : " + main.toString());
		Debug.println(Debug.main, "MainWindow content pane :" + main.getContentPane().toString());
		Debug.println(Debug.main, "SignalPanel parent : " + SignalPanel.getInstance().getParent().toString());
		
		// setting up menu ActionListeners
		main.registerActionListener(MainWindow.ID.closeProgram, new ActionListener() {
																	public void actionPerformed(ActionEvent ae) {
																		Main.closeProgram(ae);
																	}
																});
		main.registerActionListener(MainWindow.ID.openNewView, new ActionListener() {
																public void actionPerformed(ActionEvent ae) {
																	Main.openNewSignalView(ae);
																}
															   });
		main.registerActionListener(MainWindow.ID.saveAllDatasets, new ActionListener() {
																	   public void actionPerformed(ActionEvent ae) {
																		   Main.saveAllDatasets();
																	   }
															   	   });
		main.registerActionListener(MainWindow.ID.newAnnotationFile, new ActionListener() {
																		 public void actionPerformed(ActionEvent ae) {
																			 Main.uiCreateNewAnnotationFile();
																		 }
																   	 });
		main.registerActionListener(MainWindow.ID.selectAnnotationFile, new ActionListener() {
																		    public void actionPerformed(ActionEvent ae) {
																			    Main.uiSelectAnnotation();
																		    }
																   	    });
		main.registerActionListener(MainWindow.ID.datasetManager, new ActionListener() {
																		    public void actionPerformed(ActionEvent ae) {
																			    new DatasetManagerDialog(main);
																		    }
																   	    });
		// init text of status bar
		StatusBar.getInstance().updateText("keiner");
		
		sv = new SignalView[MAXSIGNALS];
		generateSignalViews(MAXSIGNALS, 2000);
		// update main window component
		main.revalidate();
		main.repaint();
		
		// running test routines - not necessary for release 
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
		dt.testReadOnlyDataController();
		dt.testBufferedValues();
		main.revalidate();
		main.repaint();
		
		return;
	}
	
	/**
	 * Returns the instance of the global {@link gst.data.AnnotationManager}.
	 * @return instance of {@code AnnotationManager}
	 */
	public static AnnotationManager getAnnotationManager() {
		return annotationManager;
	}
	
	/**
	 * Opens a {@link gst.ui.dialog.DataSelectionDialog} that ask the user to select previously loaded data. A new
	 * {@link gst.ui.SignalView} is created for the selected data.
	 * @param ae {@code ActionEvent} fired the calling UI-element
	 */
	private static void openNewSignalView(ActionEvent ae) {
		DataSelectionDialog dialog = new DataSelectionDialog(null);
		List<DataController> ctrl = dialog.show();
		if(ctrl.isEmpty() == true) {
			// nothing to do here
			return;
		}
		SignalView view = SignalView.createControlledView(ctrl.get(0));
		for(int i = 1; i < ctrl.size(); i++) {
			view.addController(ctrl.get(i));
		}
		SignalPanel.getInstance().addSignal(view, true);
	}
	
	/**
	 * Opens a {@link gst.ui.dialog.AnnotationSelectionDialog} to select a EventEntry of a dataset to write annotations to.
	 */
	private static void uiSelectAnnotation() {
		AnnotationSelectionDialog dialog = new AnnotationSelectionDialog();
		AnnotationController selectedAC = dialog.show();
		if(selectedAC != null) {	// annotation selected -> update AnnoationManager selection
			getAnnotationManager().selectController(selectedAC); 
			Debug.println(Debug.main, "selected Annotation: " + selectedAC.getFullName());
		} else {
			Debug.println(Debug.main, "no new annotation selected");
		}
		return;
	}
	
	/**
	 * Opens a {@link gst.ui.dialog.DatasetSelectionDialog} and creates a new EventEntry for the selected dataset with the
	 * given name. This method is called by the menu entry "Annotationen -> Kanal hinzufügen...".
	 */
	private static void uiCreateNewAnnotationFile() {
		DatasetSelectionDialog dialog = new DatasetSelectionDialog();
		UnisensDataset selectedDs = dialog.show();
		if(selectedDs == null) {	// abort operation: no dataset selected
			Debug.println(Debug.main, "no dataset selected");
		} else {
			Debug.println(Debug.main, "dataset '" + selectedDs.getName() + "' selected");
			EnterFileNameDialog dialog2 = new EnterFileNameDialog();	// ask user for filename
			boolean finished = false;
			while(finished != true) {
				String fileName = dialog2.show();
				if(fileName != null) {
					AnnotationController newAnnoCtrl = selectedDs.createAnnotation(fileName); // creation fails if filename already used in dataset
					if(newAnnoCtrl != null) {
						// creation successful
						getAnnotationManager().selectController(newAnnoCtrl);
						finished = true;
					}
				} else {
					finished = true;
				}
			}
		}
	}
	
	/**
	 * Saves all datasets.
	 */
	private static void saveAllDatasets() {
		for(int i = 0; i < datasetList.size(); i++) {
			datasetList.get(i).save();
		}
	}
	
	/**
	 * Creates {@link gst.ui.SignalView}s for all {@code Entry}s of the given {@link gst.data.UnisensDataset} and adds them to the
	 * {@link gst.ui.SignalPanel}.
	 * @param ds {@code UnisensDataset} to use
	 * @return the list of generated controllers
	 */
	@Deprecated
	private static List<DataController> createSignalViews(UnisensDataset ds) {
		List<DataController> ctrlList = ds.getControllerList();
		Iterator<DataController> it = ctrlList.iterator();
		while(it.hasNext()) {
			SignalPanel.getInstance().addSignal(SignalView.createControlledView(it.next()));
		}
		return ctrlList;
	}
	
	/**
	 * Exits the program without saving changes to unisens datasets. Closes all open datasets and exits.
	 * @param ae {@code ActionEvent} causing this action
	 */
	private static void closeProgram(ActionEvent ae) {
		for(int i = 0; i < datasetList.size(); i++) {
			datasetList.close(i);
		}
		System.exit(0);
	}
	
	/**
	 * Used in early versions to create {@link gst.ui.SignalView SignalViews} with random data.
	 * @param numSignals number of {@code SignalViews} to create
	 * @param numDataPoints number of data-points per {@code SignalView}
	 */
	@Deprecated
	private static void generateSignalViews(int numSignals, int numDataPoints) {
		for(int i = 0; i < numSignals; i++) {
			sv[i] = SignalViewFactory.generateRandomChart(numDataPoints);
			SignalPanel.getInstance().addSignal(sv[i]);
		}
		return;
	}
	
	/**
	 * Used in early versions to set the ranges of time axises of all {@link gst.ui.SignalView SignalViews}.
	 * @param min lower end for time axis
	 * @param max upper end for time axis
	 */
	@Deprecated
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
