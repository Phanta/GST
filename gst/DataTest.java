/**
 * DataTest.java created on 14.06.2012
 */

package gst;

import gst.data.UnisensDataset;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (14.06.2012)
 */
public class DataTest {
	UnisensDataset usds = null;
	
	public DataTest() {
		
	}
	
	public void testGenerate() {
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\uniImplTest", true);
		usds.setComment("this is a test dataset");
		usds.save();
	}
	
	public void testLoad() {
		usds = new UnisensDataset("D:\\Users\\grunitz\\Documents\\Unisens Examples\\uniImplTest", true);
		System.out.println(usds.getComment());
		usds.setName(null);
		usds.save();
		usds.close();
	}
}
