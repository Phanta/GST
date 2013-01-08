/**
 * DataTest.java created on 14.06.2012
 */

package gst.test;

import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.XYSeries;

import org.unisens.SignalEntry;
import org.unisens.ValuesEntry;
import org.unisens.EventEntry;

import gst.data.AnnotationController;
import gst.data.AnnotationList;
import gst.data.BufferedValueController;
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
 * @version 3 (24.07.2012)
 */
public class DataTest {
	/** dataset */										private UnisensDataset usds = null;
	/** number of tests to do */						static int test = 0;
	/** name of the currently running test method */	static String functionName = "";
	
	public DataTest() {
		test = 0;
		return;
	}
	
	/**
	 * Testing creation, editing and saving of {@link gst.data.UnisensDataset}.
	 */
	public void testGenerate() {
		testing("testGenerate(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\uniImplTest", true);
		usds.setComment("this is a test dataset");
		usds.save();
		testEnd("testGenerate(void)");
	}
	
	/**
	 * Testing basic reading of data from {@link gst.data.UnisensDataset}.
	 */
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
	
	/**
	 * Loading dataset and reading all IDs of its entries.
	 */
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
	
	/**
	 * Loading dataset and reading all content classes of its entries.
	 */
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

	/**
	 * Loading dataset and reading all entry types of its entries.
	 */
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
		XYSeries xys = dataCtrl.getDataPoints(0.0, 300.0, 60000);
		echo("collection contains " + xys.getItemCount() + " Items (-> 60000)");
		if(xys.getItemCount() == 60000) {
			test--;
		}
		xys = dataCtrl.getDataPoints(0.0, 150.0, 60000);
		echo("half-collection contains " + xys.getItemCount() + " Items (-> 30000)");
		if(xys.getItemCount() == 30000) {
			test--;
		}
		xys = dataCtrl.getDataPoints(0.0, 150.0, 1000);
		echo("1k-item-collection contains " + xys.getItemCount() + " Items (-> 1000)");
		if(xys.getItemCount() == 1000) {
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
	
	/**
	 * Testing zoom functionality with annotations.
	 */
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
	
	/**
	 * Testing {@link gst.data.SignalController} with multiple channels.
	 */
	public void testMultiController() {
		testing("multiple Controllers for one View -> Example_003", 0);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_003", true);
		DataController annoCtrl = new AnnotationController((EventEntry) usds.getEntry("trigger_reference.csv"));
		DataController sigCtrl1 = new SignalController((SignalEntry) usds.getEntry("ecg_padsy_250.bin"));
		DataController sigCtrl2 = new SignalController((SignalEntry) usds.getEntry("ecg_padsy_250.bin"));
		echo("loading data");
		((SignalController) sigCtrl1).setChannelToControl(0);
		SignalView csv = SignalView.createControlledView(sigCtrl1);
		((SignalController) sigCtrl2).setChannelToControl(1);
		csv.addController(sigCtrl2);
		csv.addController(annoCtrl);
		csv.setTimeAxisBounds(21.75, 23.545);
		SignalPanel.getInstance().addSignal(csv);
		echo("displaying data");
		testEnd();
		return;
	}
	
	/**
	 * Test correct handling of read-only entries in dataset.
	 */
	public void testReadOnlyDataController() {
		testing("read-only Controllers -> KM251170", 10);
		echo("loading data");
		usds = new UnisensDataset("D:/Users/grunitz/Documents/Unisens Examples/testdata", true);
		echo("checking read-only attributes");
		Iterator<DataController> it = usds.getControllerList().iterator();
		while(it.hasNext()) {
			DataController dc = it.next();
			if(dc.getEntryId().equals("fQRS.csv") || dc.getEntryId().equals("mQRS.csv") || dc.getEntryId().equals("tst.csv")) {
				if(dc.isReadOnly()) {
					test--;
				}
			} else {
				if(!dc.isReadOnly()) {
					test--;
				}
			}
		}
		testEnd();
		return;
	}
	
	/**
	 * Testing {@link gst.data.BufferedValueController} for correct behavior.
	 */
	public void testBufferedValues() {
		testing("BufferedValueController -> Implementation Test");
		echo("loading Data");
		usds = new UnisensDataset("D:/Users/grunitz/Documents/Unisens Examples/uniImplTest", true);
		ValuesEntry entry = (ValuesEntry)usds.getEntry("bufferedRRTest.csv");
		BufferedValueController bvc = new BufferedValueController(entry);
		SignalView csv = SignalView.createControlledView(bvc);
		SignalPanel.getInstance().addSignal(csv);
		bvc.save();
		testEnd();
	}
	
	@Deprecated
	public void arrayTest() {
		testing("arrayTest(void)", 0);
		int[][] ar = new int[5][3];
		echo("" + ar.length);
		testEnd();
	}
	
	/**
	 * Standard test begin console output.
	 * @param testName name of the test method that started
	 */
	private static void testing(String testName) {
		System.out.println("TEST\tRunning '" + testName + "' ...");
		functionName = testName;
		return;
	}
	
	/**
	 * Start of standard test including console output and initialization of test counter.
	 * @param testName name of the test method that started
	 * @param numTests number of test to perform
	 */
	private static void testing(String testName, int numTests) {
		test = numTests;
		testing(testName);
		return;
	}
	
	/**
	 * Ends the previously started test.
	 */
	private static void testEnd() {
		testEnd(functionName);
		return;
	}
	
	/**
	 * Console output at test end.
	 * @param testName name of the test method
	 */
	private static void testEnd(String testName) {
		System.out.println("TEST\t... test '" + testName + "' ended with " + test + " failed tests.\n");
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
