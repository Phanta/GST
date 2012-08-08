/**
 * AnnotationManager.java created on 08.08.2012
 */

package gst.data;

import org.unisens.Event;

import gst.test.Debug;
import gst.ui.StatusBar;

/**
 * 
 * @author Enrico Grunitz
 * @version 0.1.0 (08.08.2012)
 */
public class AnnotationManager {
	public enum Preset {
		N,
		V,
		B,
		E;
	}
	
	private AnnotationController selectedAnnotationChannel;
	private Preset selectedPreset;
	private String type;
	private String comment;
	
	public AnnotationManager() {
		this.selectedAnnotationChannel = null;
		this.selectedPreset = Preset.N;
		StatusBar.getInstance().updateText("N", "");
	}
	
	public void selectController(AnnotationController actrl) {
		this.selectedAnnotationChannel = actrl;
		if(actrl != null) {
			StatusBar.getInstance().updateText(actrl.getFullName());
		} else {
			StatusBar.getInstance().updateText("-keine Auswahl-");
		}
	}
	
	public AnnotationController selectedController() {
		return this.selectedAnnotationChannel;
	}
	
	public boolean selectPreset(Preset preset) {
		this.selectedPreset = preset;
		switch(preset) {
		case N:
			this.type = "N";
			this.comment =  "";
			break;
		case V:
			this.type = "V";
			this.comment =  "";
			break;
		case B:
			this.type = "B";
			this.comment =  "signal loss";
			break;
		case E:
			this.type = "E";
			this.comment =  "signal recovered";
			break;
		default:
			Debug.println(Debug.annotationManager, "unknown preset selected");
			return false;
		}
		StatusBar.getInstance().updateText(this.type, this.comment);
		return true;
	}
	
	public boolean addAnnotation(double time, String type, String comment) {
		if(this.selectedAnnotationChannel != null) {
			this.selectedAnnotationChannel.addAnnotation(time, type, comment);
			return true;
		}
		Debug.println(Debug.annotationManager, "annotation not added - no channel selected");
		return false;
	}
	
	public boolean addAnnotation(double time) {
		return this.addAnnotation(time, this.type, this.comment);
	}
	
	public int removeAnnotation(double time) {
		AnnotationList al = this.selectedAnnotationChannel.getAnnotation(time);
		this.selectedAnnotationChannel.removeAnnotation(al);
		return al.size();
	}
	
	public void updateStatusAnnotationNear(double time) {
		if(this.selectedAnnotationChannel == null) {
			return;
		}
		AnnotationList list = this.selectedAnnotationChannel.getAnnotation(time);
		if(list.size() == 0) {
			StatusBar.getInstance().updateInfoText(null, null);
		} else {
			Event event = list.getEvent(0);
			StatusBar.getInstance().updateInfoText(event.getType(), event.getComment());
		}
	}

}
