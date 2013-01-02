/** Debug.java created on 27.07.2012 */

package gst.test;

/**
 * Class for standardized debug messages. Allows global enabling and disabling of messages for all classes.
 * @author Enrico Grunitz
 * @version 1.0.4 (04.10.2012)
 */
public class Debug {
	// gst
	public static final int main = 0;
	public static final int settings = 1;
	//gst.data
	public static final int annotationController = 2;
	public static final int annotationList = 3;
	public static final int controller = 4;
	public static final int signalController = 5;
	public static final int unisensDataset = 6;
	public static final int valueController = 7;
	// gst.test
	public static final int dataTest = 8;
	// gst.ui
	public static final int mainWindow = 9;
	public static final int 	mainWindowMouseForwarder = 10;
	public static final int menus = 11;
	public static final int namedMouseAdapter = 12;
	public static final int sidebar = 13;
	public static final int signalOverview = 14;
	public static final int 	signalOverviewMouseAdapter = 15;
	public static final int signalPanel = 16;
	public static final int 	signalPanelComponentAdapter = 17;
	public static final int 	signalPanelMouseAdapter = 18;
	public static final int signalView = 19;
	public static final int 	signalViewMouseAdapter = 20;
	public static final int signalViewFactory = 21;
	public static final int statusbar = 22;
	public static final int toolbar = 23;
	// gst.ui.layout
	public static final int componentArrangement = 24;
	public static final int multiSplit = 25;
	public static final int signalPanelLayoutManager = 26;
	public static final int verticalLayoutManager = 27;
	
	public static final int /* gst.ui */ dataSelectionDialog = 28;
	public static final int /* gst.ui.SignalView */ signalViewKeyAdapter = 29;
	public static final int /* gst.ui */ annotationSelectionDialog = 30;
	public static final int /* gst.ui */ signalPanelScrollLockManager = 31;
	public static final int /* gst.data */ annotationManager = 32;
	public static final int /* gst.ui */ signalPanelZoomLockManager = 33;
	public static final int /* gst.ui.dialog */ datasetManagerDialog = 34;
	public static final int /* gst.data */ bufferedValueController = 35;
	public static final int /* gst.signalprocessing */ signalProcessor = 36;
	public static final int /* gst.signalprocessing.rrcalc */ rrCalculator =37;
	public static final int /* gst.signalprocessing.rrcalc */ rrConfig =38;
	
	public static final int END = 39;


	private static final boolean isEnabled[] = {true,	//	main
												false,	//	settings
														//gst.data
												true,	//	annotationController
												false,	//	annotationList
												true,	//	controller
												false,	//	signalController
												false,	//	unisensDataset
												false,	//	valueController
														//gst.test
												false,	//	dataTest
														//gst.ui
												true,	//	mainWindow
												true,	//		mainWindowMouseForwarder
												false,	//	menus
												true,	//	namedMouseAdapter
												false,	//	sidebar
												false,	//	signalOverview
												false,	//		signalOverviewMouseAdapter
												false,	//	signalPanel
												false,	//		signalPanelComponentAdapter
												false,	//		signalPanelMouseAdapter
												false,	//	signalView
												true,	//		signalViewMouseAdapter
												false,	//	signalViewFactory
												false,	//	statusbar
												false,	//	toolbar
														// gst.ui.layout
												false,	//	componentArrangement
												false,	//	multiSplit
												false,	//	signalPanelLayoutManager
												false,	//	verticalLayoutManager
												true,	// gst.ui.DataSelectionDialog
												true,	// gst.ui.SignalView.SignalViewKeyAdapter
												true,	// gst.ui.AnnotationSelectionDialog
												true,	// gst.ui.SignalPanel.ScrollLockManager
												true,	// gst.data.AnnotationManager
												true,	// gst.ui.SignalPanel.ZoomLockManager
												true,	// gst.ui.dialog.DatasetManagerDialog
												true,	// gst.data.BufferdValueController
												true,	// gst.signalprocessing.SignalProcessor
												true,	// gst.signalprocessing.rrcalc.RRCalculator
												true,	// gst.signalprocessing.rrcalc.ConfigDialog
												false};	// END
	
	public static void println(int src, String msg) {
		if(isEnabled[src]) {
			System.out.println("DEBUG >\t" + msg);
		}
	}
}
