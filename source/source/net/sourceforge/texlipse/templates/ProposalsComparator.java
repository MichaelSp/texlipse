/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.templates;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Esa Seuranen
 * 
 *         A comparator class for ICompletition proposals, so that proposals can
 *         be sorted.
 */
public class ProposalsComparator implements Comparator<ICompletionProposal> {

	/**
	 * Compares two ICompletionProposals according to their display Strings
	 * 
	 * @return same as String.compareToIgnoreCase()
	 */
	public int compare(ICompletionProposal o1, ICompletionProposal o2) {
		return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
	}
}
