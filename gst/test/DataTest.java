/**
 * DataTest.java created on 14.06.2012
 */

package gst.test;

import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.XYSeriesCollection;
import org.unisens.SignalEntry;

import gst.data.SignalController;
import gst.data.UnisensDataset;
import gst.data.UnisensDataset.EntryType;
import gst.data.ViewController;

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
	 * Testing functions of ViewController and SignalController with Unisens example 002.
	 */
	public void signalControllerTest() {
		testing("controllerTest(void) -> Example_002", 11);
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_002\\Example_002", true);
		ViewController vc = new SignalController((SignalEntry) usds.getEntry("ecg.bin"));
		((SignalController)vc).setChannelToControl("Brustgurt");
		echo("channel 'Brustgurt' of 'ecg.bin' exists");
		test--;
		try{
			((SignalController)vc).setChannelToControl("brustgurt");
		} catch(Exception e) {
			echo("channel 'brustgurt' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		((SignalController)vc).setChannelToControl(0);
		echo("channel '0' of 'ecg.bin' exists");
		test--;
		try{
			((SignalController)vc).setChannelToControl(1);
		} catch(Exception e) {
			echo("channel '1' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		try{
			((SignalController)vc).setChannelToControl(-2);
		} catch(Exception e) {
			echo("channel '-2' of 'ecg.bin' doesn't exist ... fine for me");
			test--;
		}
		echo("first data entry @ time " + vc.getMinX());
		if(vc.getMinX() == 0) {
			test--;
		}
		echo("last data entry @ time " + vc.getMaxX());
		if(vc.getMaxX() == 300) {
			test--;
		}
		echo("data stored in units of '" + vc.getPhysicalUnit() + "'");
		if(vc.getPhysicalUnit().equals("mV")) {
			test--;
		}
		XYSeriesCollection xysc = vc.getDataPoints(0.0, 300.0, 60000);
		echo("collection contains " + xysc.getSeries(0).getItemCount() + " Items");
		if(xysc.getSeries(0).getItemCount() == 60000) {
			test--;
		}
		xysc = vc.getDataPoints(0.0, 150.0, 60000);
		echo("half-collection contains " + xysc.getSeries(0).getItemCount() + " Items");
		if(xysc.getSeries(0).getItemCount() == 30000) {
			test--;
		}
		xysc = vc.getDataPoints(0.0, 150.0, 1000);
		echo("1k-item-collection contains " + xysc.getSeries(0).getItemCount() + " Items");
		if(xysc.getSeries(0).getItemCount() == 1000) {
			test--;
		}
		testEnd();
		usds.close();
		return;
	}

	public void arrayTest() {
		testing("arrayTest(void)");
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
