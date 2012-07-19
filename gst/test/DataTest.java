/**
 * DataTest.java created on 14.06.2012
 */

package gst.test;

import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.SignalEntry;
import org.unisens.ValuesEntry;
import org.unisens.EventEntry;

import gst.data.AnnotationController;
import gst.data.AnnotationList;
import gst.data.SignalController;
import gst.data.UnisensDataset;
import gst.data.UnisensDataset.EntryType;
import gst.data.ValueController;
import gst.data.DataController;
import gst.ui.SignalPanel;
import gst.ui.SignalView;

/**
 * Class to contain static tests on some components of the programm. functions in this class are not commented due to the fact they change
 * frequently or are never touched again.
 * @author Enrico Grunitz
 * @version 3 (17.07.2012)
 */
public class DataTest {
	UnisensDataset usds = null;
	static int test = 0;
	static String functionName = "";
	
	public DataTest() {
		test = 0;
		return;
	}
	
	public void testGenerate() {
		testing("testGenerate(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\uniImplTest", true);
		usds.setComment("this is a test dataset");
		usds.save();
		testEnd("testGenerate(void)");
	}
	
	public void testLoad() {
		testing("testLoad(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\uniImplTest", true);
		echo(usds.getComment());
		usds.setName(null);
		usds.save();
		usds.close();
		testEnd("testLoad(void)");
	}
	
	public void loadAndPrintIds() {
		testing("loadAndPrintIds(void)", 3);
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		List<String> ids = usds.getDataIds();
		Iterator<String> it = ids.iterator();
		while(it.hasNext()) {
			try {
				echo(it.next());
				test--;
			} catch(NullPointerException npe) {
				echo("NullPointerException!");
			}
		}
		usds.close();
		testEnd("loadAndPrintIds(void)");
		return;
	}
	
	public void loadAndPrintContentClasses() {
		testing("loadAndPrintContentClasses(void)", 3);
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		List<String> ids = usds.getContentClasses();
		Iterator<String> it = ids.iterator();
		while(it.hasNext()) {
			echo(it.next());
			test--;
		}
		usds.close();
		testEnd("loadAndPrintContentClasses(void)");
		return;
	}

	public void loadAndPrintEntryTypes() {
		testing("loadAndPrintEntryTypes(void)", 3);
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		List<EntryType> ids = usds.getEntryTypes();
		Iterator<EntryType> it = ids.iterator();
		while(it.hasNext()) {
			echo(it.next().toString());
			test--;
		}
		usds.close();
		testEnd("loadAndPrintEntryTypes(void)");
		return;
	}
	
	/**
	 * Testing functions of DataController and SignalController with Unisens example 002.
	 */
	public void signalControllerTest() {
		testing("signalControllerTest(void) -> Example_002", 11);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		DataController dataCtrl = new SignalController((SignalEntry) usds.getEntry("ecg.bin"));
		((SignalController)dataCtrl).setChannelToControl("Brustgurt");
		echo("channel 'Brustgurt' of 'ecg.bin' exists");
		test--;
		try{
			((SignalController)dataCtrl).setChannelToControl("brustgurt");
		} catch(Exception e) {
			echo("channel 'brustgurt' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		((SignalController)dataCtrl).setChannelToControl(0);
		echo("channel '0' of 'ecg.bin' exists");
		test--;
		try{
			((SignalController)dataCtrl).setChannelToControl(1);
		} catch(Exception e) {
			echo("channel '1' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		try{
			((SignalController)dataCtrl).setChannelToControl(-2);
		} catch(Exception e) {
			echo("channel '-2' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		echo("first data entry @ time " + dataCtrl.getMinX());
		if(dataCtrl.getMinX() == 0) {
			test--;
		}
		echo("last data entry @ time " + dataCtrl.getMaxX() + " (-> 300)");
		if(dataCtrl.getMaxX() == 300) {
			test--;
		}
		echo("data stored in units of '" + dataCtrl.getPhysicalUnit() + "' (-> 'mV')");
		if(dataCtrl.getPhysicalUnit().equals("mV")) {
			test--;
		}
		XYSeriesCollection xysc = dataCtrl.getDataPoints(0.0, 300.0, 60000);
		echo("collection contains " + xysc.getSeries(0).getItemCount() + " Items (-> 60000)");
		if(xysc.getSeries(0).getItemCount() == 60000) {
			test--;
		}
		xysc = dataCtrl.getDataPoints(0.0, 150.0, 60000);
		echo("half-collection contains " + xysc.getSeries(0).getItemCount() + " Items (-> 30000)");
		if(xysc.getSeries(0).getItemCount() == 30000) {
			test--;
		}
		xysc = dataCtrl.getDataPoints(0.0, 150.0, 1000);
		echo("1k-item-collection contains " + xysc.getSeries(0).getItemCount() + " Items (-> 1000)");
		if(xysc.getSeries(0).getItemCount() == 1000) {
			test--;
		}
		testEnd();
		usds.close();
		return;
	}

	/**
	 * Test of working SignalView controlled by SignalController.
	 */
	public void testControlledSignalView() {
		testing("testControlledSignalView(void) -> Example_002", 0);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		DataController dataCtrl = new SignalController((SignalEntry) usds.getEntry("ecg.bin"));
		((SignalController)dataCtrl).setChannelToControl("Brustgurt");
		SignalView csv = SignalView.createControlledView(dataCtrl);
		SignalPanel.getInstance().addSignal(csv);
		csv.setTimeAxisBounds(20.0, 40.0);
		testEnd();
	}
	
	/**
	 * Function test of ValueController including SignalView interaction.
	 */
	public void testValueController() {
		testing("testValueController(void) -> Example_003", 2);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_003", true);
		echo("loading dataset 'Example_003'");
		DataController dataCtrl = new ValueController((ValuesEntry) usds.getEntry("bloodpressure.csv"));
		echo("generated ValueController");
		test--;
		((ValueController)dataCtrl).setChannelToControl("systolisch");
		echo("selected channel 'systolisch'");
		test--;
		SignalView cvsv = SignalView.createControlledView(dataCtrl);
		SignalPanel.getInstance().addSignal(cvsv, true);
		cvsv.setTimeAxisBounds(0.0, 3600.0);
		echo("displaying data");
		testEnd();
		usds.close();
	}
	
	/**
	 * Test of AnnotationController.
	 */
	public void testAnnotationController() {
		testing("testAnnotationController(void) -> Example_003", 7);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_003", true);
		echo("loading dataset 'Example_003'");
		DataController dataCtrl = new AnnotationController((EventEntry) usds.getEntry("trigger_reference.csv"));
		echo("generated ValueController");
		test--;
		if(dataCtrl.isAnnotation() == true) {
			echo("controller is an AnnotationController");
			test--;
		} else {
			echo("controller isn't AnnotationController ... too bad :-(");
		}
		echo("time point of first event is " + dataCtrl.getMinX() + "s (-> 0.575s)");
		if(dataCtrl.getMinX() == 0.575) {
			test--;
		}
		echo("time point of last event is " + dataCtrl.getMaxX() + "s (-> 3599.857s)");
		if(dataCtrl.getMaxX() == 3599.857) {
			test--;
		}
		AnnotationList dataToDisplay = dataCtrl.getAnnotations(0, 84.0);
		echo("annotations between 0 and 84 seconds: " + dataToDisplay.size() + " (-> 100)");
		if(dataToDisplay.size() == 100) {
			test--;
		}
		dataToDisplay = dataCtrl.getAnnotations(1657.0, 1994.0);
		echo("annotations between 1657 and 1994 seconds: " + dataToDisplay.size() + " (-> 432)");
		if(dataToDisplay.size() == 432) {
			test--;
		}
		dataToDisplay = dataCtrl.getAnnotations(3333.0, 3600.0);
		echo("annotations between 3333 and 3600 seconds: " + dataToDisplay.size() + " (-> 279)");
		if(dataToDisplay.size() == 279) {
			test--;
		}
		SignalView cvsv = SignalView.createControlledView(dataCtrl);
		SignalPanel.getInstance().addSignal(cvsv, true);
		cvsv.setTimeAxisBounds(0.0, 300.0);
		echo("displaying data, not annotations");
		
		testEnd();
		usds.close();
	}
	
	public void testAnnotationZoom() {
		testing("testAnnotationZoom(void) -> Example_002", 2);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		echo("loading dataset 'Example_002'");
		DataController dataCtrl = new AnnotationController((EventEntry) usds.getEntry("qrs-Trigger.csv"));
		echo("generated DataController");
		test--;
		SignalView csv = SignalView.createControlledView(dataCtrl);
		SignalPanel.getInstance().addSignal(csv);
		csv.setTimeAxisBounds(20.0, 40.0);
		echo("displayed data");
		test--;
		testEnd();
		usds.close();
	}
		
	@Deprecated
	public void arrayTest() {
		testing("arrayTest(void)", 0);
		int[][] ar = new int[5][3];
		echo("" + ar.length);
		testEnd();
	}
	
	private static void testing(String testName) {
		System.out.println("Running test '" + testName + "' ...");
		functionName = testName;
		return;
	}
	
	private static void testing (String testName, int numTests) {
		test = numTests;
		testing(testName);
		return;
	}
	
	private static void testEnd() {
		testEnd(functionName);
		return;
	}
	
	private static void testEnd(String testName) {
		System.out.println("... test '" + testName + "' ended with " + test + " failed tests.\n");
	}

	/**
	 * Convenience method for {@code System.out.println(String)}.
	 * @param string the string to print
	 */
	private static void echo(String string) {
		System.out.println("\t" + string);
		return;
	}
}
