/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.builder;

import net.sourceforge.texlipse.properties.TexlipseProperties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


/**
 * Build tex-file(s) using latex or pslatex or pdflatex.
 * 
 * @author Kimmo Karlsson
 */
public class TexBuilder extends AbstractBuilder {

    private ProgramRunner latex;
    private ProgramRunner bibtex;
    private ProgramRunner makeIndex;
    private String output;
    
    public TexBuilder(int i, String outputFormat) {
        super(i);
        output = outputFormat;
        latex = null;
        bibtex = null;
        makeIndex = null;
        isValid();
    }
    
    /**
     * Check if the needed program runners are operational.
     * Update runners from registry if necessary.
     * @return true, if this builder is ready for operation, false otherwise
     */
    public boolean isValid() {
        if (latex == null || !latex.isValid()) {
            latex = BuilderRegistry.getRunner(TexlipseProperties.OUTPUT_FORMAT_TEX, output);
        }
        if (bibtex == null || !bibtex.isValid()) {
            bibtex = BuilderRegistry.getRunner("", TexlipseProperties.OUTPUT_FORMAT_BIB);
        }
        if (makeIndex == null || !makeIndex.isValid()) {
            makeIndex = BuilderRegistry.getRunner(TexlipseProperties.OUTPUT_FORMAT_IDX, null);
        }
        return latex != null && latex.isValid()
            && bibtex != null && bibtex.isValid()
            && makeIndex != null && makeIndex.isValid();
    }
    
    /**
     * @return output format of the latex processor
     */
    public String getOutputFormat() {
        return latex.getOutputFormat();
    }
    
    /**
     * @return sequence
     */
    public String getSequence() {
        return latex.getProgramName();
    }
    
    public void stopRunners() {
        latex.stop();
        bibtex.stop();
    }
    
    /**
     * Run latex and optionally bibtex to produce a dvi file.
     * @throws CoreException if the build fails at any point
     */
    public void buildResource(IResource resource) throws CoreException {
        
        monitor.subTask("Building document");
        latex.run(resource);
        monitor.worked(10);

        IProject project = resource.getProject();
        String runBib = (String) TexlipseProperties.getSessionProperty(project, TexlipseProperties.SESSION_BIBTEX_RERUN);
        Boolean bibChange = (Boolean) TexlipseProperties.getSessionProperty(project, TexlipseProperties.BIBFILES_CHANGED);
        IResource runIdx = findIndex(project, resource);
        
        String[] bibs = (String[]) TexlipseProperties.getSessionProperty(project, TexlipseProperties.BIBFILE_PROPERTY);
        if (bibs != null && bibs.length > 0 && (runBib != null || bibChange != null)) {
            
            bibtex.run(resource);
            monitor.worked(10);
            
            TexlipseProperties.setSessionProperty(project, TexlipseProperties.SESSION_BIBTEX_RERUN, null);
            TexlipseProperties.setSessionProperty(project, TexlipseProperties.BIBFILES_CHANGED, null);
            
            if (runIdx != null) {
                makeIndex.run(resource);
                monitor.worked(10);
            }
            
            latex.run(resource);
            monitor.worked(10);
            latex.run(resource);
            monitor.worked(10);
            
        } else {
        
            // if bibtex is not used, maybe the references need to be updated in the main document
            String rerun = (String) TexlipseProperties.getSessionProperty(resource.getProject(), TexlipseProperties.SESSION_LATEX_RERUN);
            if (rerun != null) {
                
                if (runIdx != null) {
                    makeIndex.run(resource);
                    monitor.worked(10);
                }
                
                latex.run(resource);
                monitor.worked(10);
                
                TexlipseProperties.setSessionProperty(resource.getProject(), TexlipseProperties.SESSION_LATEX_RERUN, null);
            }
        }
    }

    /**
     * Find a handle to the index file of this project.
     * @param project the current project
     * @param source buildable resource inside project 
     * @return handle to index file or null if not found.
     *         Returns null also if the index file is older than the current output file
     */
    private IResource findIndex(IProject project, IResource source) {
        
        IContainer srcDir = TexlipseProperties.getProjectSourceDir(project);
        if (srcDir == null) {
            srcDir = project;
        }
        
        String name = source.getName();
        IResource idxFile = project.findMember(name.substring(0, name.length() - source.getFileExtension().length()) + TexlipseProperties.OUTPUT_FORMAT_IDX);
        if (idxFile == null) {
            return null;
        }
        
        IResource outFile = TexlipseProperties.getProjectOutputFile(project);
        if (outFile.getLocalTimeStamp() > idxFile.getLocalTimeStamp()) {
            return null;
        }
        
        return idxFile;
    }
}