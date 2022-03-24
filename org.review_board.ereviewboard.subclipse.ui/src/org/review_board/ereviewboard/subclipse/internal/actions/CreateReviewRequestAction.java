/*******************************************************************************
 * Copyright (c) 2011 Robert Munteanu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Munteanu - initial API and implementation
 *******************************************************************************/
package org.review_board.ereviewboard.subclipse.internal.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.review_board.ereviewboard.subclipse.internal.wizards.PostReviewRequestWizard;

/**
 * @author Robert Munteanu
 */
public class CreateReviewRequestAction implements IActionDelegate {

    private IProject currentProject;
    
    public void run(IAction action) {
        
        if ( currentProject == null )
            return;
        

        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        
        new WizardDialog(win.getShell(), new PostReviewRequestWizard(currentProject)).open();
        
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if ( selection instanceof IStructuredSelection ) {
            
            IStructuredSelection sel = (IStructuredSelection) selection;
            
            if ( sel.getFirstElement() instanceof IProject ) {
                currentProject = (IProject) sel.getFirstElement();
                
                return;
            }
        }
        
        currentProject = null;
    }
}
