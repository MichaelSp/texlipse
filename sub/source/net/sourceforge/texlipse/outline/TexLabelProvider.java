/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.outline;

import net.sourceforge.texlipse.TexlipsePlugin;
import net.sourceforge.texlipse.model.OutlineNode;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Label provider for the OutlineNodes. The getName() method of the
 * OutlineNodes is used to get the label for a node. 
 * 
 * All the for nodes of type ENVIRONMENT the name is used to find
 * the right image. 
 * 
 * If no image is found for a node, default_outline.gif is used.
 * 
 * The images are loaded and disposed by the TexlipsePlugin.
 *
 * @author Laura Takkinen, Taavi Hupponen 
 *  
 */
public class TexLabelProvider extends LabelProvider {

	/**
	 * Returns an image for the given element.
     * 
     * @return the image to view at the given element
	 */
	public Image getImage(Object element) {
		OutlineNode node = (OutlineNode) element;
		Image image;
		
		switch(node.getType()) {
		case OutlineNode.TYPE_PREAMBLE:
			image = TexlipsePlugin.getImage("preamble");
        	break;
		case OutlineNode.TYPE_PART:
            image = TexlipsePlugin.getImage("part");
        	break;
		case OutlineNode.TYPE_CHAPTER:
            image = TexlipsePlugin.getImage("chapter");
			break;
		case OutlineNode.TYPE_SECTION:
			image = TexlipsePlugin.getImage("section");
			break;
		case OutlineNode.TYPE_SUBSECTION:
			image = TexlipsePlugin.getImage("subsection");
			break;
		case OutlineNode.TYPE_SUBSUBSECTION:
			image = TexlipsePlugin.getImage("subsubsection");
			break;
        case OutlineNode.TYPE_PARAGRAPH:
            image = TexlipsePlugin.getImage("paragraph");
        	break;
		case OutlineNode.TYPE_ENVIRONMENT:
			image = TexlipsePlugin.getImage(node.getName());
			break;
		default:
			image = TexlipsePlugin.getImage("default_outline");
		}
		if (image == null) {
			image = TexlipsePlugin.getImage("default_outline");
		}
		return image;
		
	}

	/**
     * Returns the text description of the elemet. That is element 
     * name for OutlineNode.
     * 
	 * @return the text to view at the given element
	 */
	public String getText(Object element) {
		return ((OutlineNode)element).getName();
	}

    
    /**
     * Not used atm. Simply return false.
     * 
     * @return true, if the given property affects the given element
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}