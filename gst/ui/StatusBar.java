package gst.ui;

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.MenuElement;

/**
 * Single instance status bar.
 * <p>
 * This class is also a MouseListener which listens to MOUSE_ENTERED and 
 * MOUSE_EXITED events from Action derived components so that the value of the
 * Action.LONG_DESCRIPTION key is sent as a message to the status bar.
 * <p>
 * To enable this behavior, add the StatusBar instance as a MouseListener to the
 * component that was created from an Action.
 * 
 * edited by Enrico Grunitz
 * 
 * @version 0.1.1 (08.08.2012)
 * @author Mark Davidson
 * @author Enrico Grunitz
 */
public class StatusBar extends JPanel implements MouseListener {

	private JLabel label;
	private Dimension preferredSize;
	private String annoChannel;
	private String annoType;
	private String annoComment;
	private String infoType;
	private String infoComment;

	private static StatusBar INSTANCE;
	private static final String prefixAnnoChannel = "Annotationen   -   Kanal: ";
	private static final String prefixAnnoType = "   -   Typ: ";
	private static final String prefixAnnoComment = "   -   Kommentar: ";
	private static final String prefixInfoType = "          << Typ: ";
	private static final String prefixInfoComment = "   -   Kommentar: ";
	private static final String suffixInfoComment = " >>";

	public StatusBar() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createEtchedBorder());

		// Set a large blank label to set the preferred size.
		label = new JLabel(
				"                                                                                        ");
		preferredSize = new Dimension(getWidth(label.getText()),
				2 * getFontHeight());

		this.add(label);
		
		this.setAnnoChannel(null);
		this.setAnnoType(null);
		this.setAnnoComment(null);
		this.setInfoType(null);
		this.setInfoComment(null);
		this.setMessage();
	}

	/**
	 * Return the instance of the StatusBar. If this has not been explicity set
	 * then it will be created.
	 * @return the StatusBar instance.
	 * @see #setInstance
	 */
	public static StatusBar getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new StatusBar();
		}
		return INSTANCE;
	}
	
	public void updateText(String annoChannel) {
		this.setAnnoChannel(annoChannel);
		this.setMessage();
	}
	
	public void updateText(String annoType, String annoComment) {
		this.setAnnoType(annoType);
		this.setAnnoComment(annoComment);
		this.setMessage();
	}
	
	public void updateInfoText(String type, String comment) {
		this.setInfoType(type);
		this.setInfoComment(comment);
		this.setMessage();
	}
	
	private void setAnnoChannel(String str) {
		if(str == null || str.isEmpty()) {
			this.annoChannel = "=keine Auswahl=";
		} else {
			this.annoChannel = str;
		}
	}
	
	private void setAnnoType(String str) {
		if(str == null || str.isEmpty()) {
			this.annoType = " = ";
		} else {
			this.annoType = str;
		}
	}
	
	private void setAnnoComment(String str) {
		if(str == null) {
			this.annoComment = "";
		} else {
			this.annoComment = str;
		}
	}
	
	private void setInfoType(String str) {
		if(str == null || str.isEmpty()) {
			this.infoType = " = ";
		} else {
			this.infoType = str;
		}
	}
	
	private void setInfoComment(String str) {
		if(str == null) {
			this.infoComment = "";
		} else {
			this.infoComment = str;
		}
	}
	
	private void setMessage() {
		this.setMessage(prefixAnnoChannel + this.annoChannel +
						prefixAnnoType + this.annoType +
						prefixAnnoComment + this.annoComment +
						prefixInfoType + this.infoType +
						prefixInfoComment + this.infoComment +
						suffixInfoComment);
	}

	/**
	 * Sets the StatusBar instance.
	 */
	public static void setInstance(StatusBar status) {
		INSTANCE = status;
	}

	/**
	 * Returns the string width
	 * @param s the string
	 * @return the string width
	 */
	protected int getWidth(String s) {
		FontMetrics fm = this.getFontMetrics(this.getFont());
		if (fm == null) {
			return 0;
		}
		return fm.stringWidth(s);
	}

	/**
	 * Returns the height of a line of text
	 * @return the height of a line of text
	 */
	protected int getFontHeight() {
		FontMetrics fm = this.getFontMetrics(this.getFont());
		if (fm == null) {
			return 0;
		}
		return fm.getHeight();
	}

	/**
	 * Returns the perferred size
	 * @return the preferred size
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * Sets non-transient status bar message
	 * @param message the message to display on the status bar
	 */
	public void setMessage(String message) {
		label.setText(message);
	}

	//
	// MouseListener methods
	//

	public void mouseClicked(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
		setMessage("");
	}

	/**
	 * Takes the LONG_DESCRIPTION of the Action based components and sends them
	 * to the Status bar
	 */
	public void mouseEntered(MouseEvent evt) {
		if (evt.getSource() instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) evt.getSource();
			Action action = button.getAction();
			if (action != null) {
				String message = (String) action
						.getValue(Action.LONG_DESCRIPTION);
				setMessage(message);
			}
		}
	}

	/**
	 * Helper method to recursively register all MenuElements with a mouse
	 * listener.
	 */
	public void registerMouseListener(MenuElement[] elements) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof JMenuItem) {
				((JMenuItem) elements[i]).addMouseListener(this);
			}
			registerMouseListener(elements[i].getSubElements());
		}
	}

	/**
	 * Helper method to register all components with a mouse listener.
	 */
	public void registerMouseListener(Component[] components) {
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof AbstractButton) {
				((AbstractButton) components[i]).addMouseListener(this);
			}
		}
	}
} // end class StatusBar
