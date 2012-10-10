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

import gst.ui.DataSelectionDialog;
import gst.ui.MainWindow;
import gst.ui.SignalPanel;
import gst.ui.SignalView;
import gst.ui.SignalViewFactory;
import gst.ui.StatusBar;
import gst.ui.dialog.AnnotationSelectionDialog;
import gst.ui.dialog.DatasetManagerDialog;
import gst.ui.dialog.DatasetSelectionDialog;
import gst.ui.dialog.EnterFileNameDialog;

/**
 * Class for the public static void main(String[] args) function.
 * @author Enrico Grunitz
 * @version 0.1.6.2 (08.10.2012)
 */
public abstract class Main {
	
	private static SignalView[] sv;
	private static final int MAXSIGNALS = 0;

	private static MainWindow main;
	//private static ArrayList<UnisensDataset> datasets;
	private static DatasetList datasetList;
	private static AnnotationManager annotationManager;
	
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
		StatusBar.getInstance().updateText("keiner");
		
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
		dt.testReadOnlyDataController();
		main.revalidate();
		main.repaint();
		
		return;
	}
	
	public static AnnotationManager getAnnotationManager() {
		return annotationManager;
	}
	
	/**
	 * Asks the user to select data of loaded datasets and creates a new {@link gst.ui.SignalView} to display them.
	 * @param ae {@code ActionEvent} fired
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
	 * Asks the user to select an {@code EventEntry} to edit and selects it.
	 */
	private static void uiSelectAnnotation() {
		AnnotationSelectionDialog dialog = new AnnotationSelectionDialog();
		AnnotationController selectedAC = dialog.show();
		if(selectedAC != null) {
			getAnnotationManager().selectController(selectedAC); 
			Debug.println(Debug.main, "selected Annotation: " + selectedAC.getFullName());
		} else {
			Debug.println(Debug.main, "no new annotation selected");
		}
		return;
	}
	
	private static void uiCreateNewAnnotationFile() {
		DatasetSelectionDialog dialog = new DatasetSelectionDialog();
		UnisensDataset selectedDs = dialog.show();
		if(selectedDs == null) {
			Debug.println(Debug.main, "no dataset selected");
		} else {
			Debug.println(Debug.main, "dataset '" + selectedDs.getName() + "' selected");
			EnterFileNameDialog dialog2 = new EnterFileNameDialog();
			boolean finished = false;
			while(finished != true) {
				String fileName = dialog2.show();
				if(fileName != null) {
					AnnotationController newAnnoCtrl = selectedDs.createAnnotation(fileName);
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
	private static List<DataController> createSignalViews(UnisensDataset ds) {
		List<DataController> ctrlList = ds.getControllerList();
		Iterator<DataController> it = ctrlList.iterator();
		while(it.hasNext()) {
			SignalPanel.getInstance().addSignal(SignalView.createControlledView(it.next()));
		}
		return ctrlList;
	}
	
	/**
	 * Exits the program without saving changes to unisens databases.
	 * @param ae {@code ActionEvent} causing this action
	 */
	private static void closeProgram(ActionEvent ae) {
		for(int i = 0; i < datasetList.size(); i++) {
			datasetList.close(i);
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
