/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package net.sourceforge.texlipse.bibeditor.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eugene Kuleshov
 */
public class ListEditorContentProvider<T> implements IStructuredContentProvider {

  public static final Object[] EMPTY = new Object[0];
  private boolean shouldSort;
  private Comparator<T> comparator;
  
  @SuppressWarnings("unchecked")
  public Object[] getElements(Object input) {
    if(input instanceof List) {
      List<T> list = (List<T>) input;
      if (shouldSort) {
        T[] array = (T[]) list.toArray();
        Arrays.<T>sort(array, comparator);
        return array;
      }
      return list.toArray();
    }
    return EMPTY;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }
  
  public void dispose() {
  }
  
  public void setShouldSort(boolean shouldSort) {
    this.shouldSort = shouldSort;
  }
  
  public void setComparator(Comparator<T> comparator) {
    this.comparator = comparator;
  }
}
