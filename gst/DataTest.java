/**
 * DataTest.java created on 14.06.2012
 */

package gst;

import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.ri.UnisensImpl;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1 (14.06.2012)
 */
public class DataTest {
	Unisens us;
	
	public DataTest() {
		try {
			us = new UnisensImpl("");
		} catch(UnisensParseException upe) {
			System.out.println("boom");
		}
		 
	}
}
