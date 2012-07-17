/*
 * Created on Mar 25, 2005
 */
package net.sourceforge.texlipse.templates;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Esa Seuranen
 * 
 *         Comparator for sorting completition proposals (ICompletitionProposal)
 */
public class CompletionProposalComparator implements Comparator<ICompletionProposal> {

	/**
	 * Compares two CompletionProposals accrding to their display strings
	 * 
	 * @param arg0
	 *            first ICompletionProposal
	 * @param arg1
	 *            second ICompletionProposal
	 * 
	 * @return the same as String.compareTo() does
	 */
	public int compare(ICompletionProposal arg0, ICompletionProposal arg1) {
		return (arg0).getDisplayString().compareTo((arg1).getDisplayString());
	}

}
