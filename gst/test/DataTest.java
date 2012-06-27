/**
 * DataTest.java created on 14.06.2012
 */

package gst.test;

import java.util.Iterator;
import java.util.List;

import gst.data.UnisensDataset;
import gst.data.UnisensDataset.EntryType;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (27.06.2012)
 */
public class DataTest {
	UnisensDataset usds = null;
	
	public DataTest() {
		
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
		testing("loadAndPrintIds(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_001\\Example_001", true);
		List<String> ids = usds.getDataIds();
		Iterator<String> it = ids.iterator();
		while(it.hasNext()) {
			try {
				echo(it.next());
			} catch(NullPointerException npe) {
				echo("NullPointerException!");
			}
		}
		usds.close();
		testEnd("loadAndPrintIds(void)");
		return;
	}
	
	public void loadAndPrintContentClasses() {
		testing("loadAndPrintClasses(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_001\\Example_001", true);
		List<String> ids = usds.getContentClasses();
		Iterator<String> it = ids.iterator();
		while(it.hasNext()) {
			echo(it.next());
		}
		usds.close();
		testEnd("loadAndPrintClasses(void)");
		return;
	}

	public void loadAndPrintEntryTypes() {
		testing("loadAndPrintClasses(void)");
		// DEBUG absolute path
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\Example_001\\Example_001", true);
		List<EntryType> ids = usds.getEntryTypes();
		Iterator<EntryType> it = ids.iterator();
		while(it.hasNext()) {
			echo(it.next().toString());
		}
		usds.close();
		testEnd("loadAndPrintClasses(void)");
		return;
	}

	private static void testing(String testName) {
		System.out.println("Running test '" + testName + "' ...");
	}
	
	private static void testEnd(String testName) {
		System.out.println("... test '" + testName + "' ended");
		System.out.println("");
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
