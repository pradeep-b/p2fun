/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial implementation
 *******************************************************************************/
package com.ibm.pradeepb.p2.sitesgc.handlers;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.ibm.pradeepb.p2.sitesgc.Activator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	
	private Set<URI> sites;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (sites == null) {
			populateRepos();
			showMessage(event, "Initialized repos - " + sites.size());
			printRepos();
			return null;
		}
		printRepos();
		int extraRepos = removeExtraneousRepos();
		showMessage(event, "Removed extraneous repos - " + extraRepos);
		return null;
	}

	private int removeExtraneousRepos() {
		IArtifactRepositoryManager artRepoMgr = getArtifactRepoManager();
		IMetadataRepositoryManager metRepoMgr = getMetatdataRepoManager();
		URI[] allRepoURIs = artRepoMgr.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
		int numRemoved = 0;
		for (URI uri : allRepoURIs) {
			if (!sites.contains(uri)) {
				if (artRepoMgr.removeRepository(uri))
					numRemoved++;
				if (metRepoMgr.removeRepository(uri))
					numRemoved++;			
			}
		}
		return numRemoved;
	}

	private void populateRepos() {
		sites = new HashSet<>();
		URI[] metaRepoURIs = getMetatdataRepoManager().getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
		if (metaRepoURIs.length > 0) 
			sites.addAll(Arrays.asList(metaRepoURIs));
		URI[] artRepoURIs = getArtifactRepoManager().getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
		if (artRepoURIs.length > 0)
			sites.addAll(Arrays.asList(artRepoURIs));
	}
	
	private IArtifactRepositoryManager getArtifactRepoManager() {
		BundleContext context = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference<IProvisioningAgent> agentSvcRef = context.getServiceReference(IProvisioningAgent.class);
		IProvisioningAgent agent = context.getService(agentSvcRef);
		return (IArtifactRepositoryManager) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
	}

	private IMetadataRepositoryManager getMetatdataRepoManager() {
		BundleContext context = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference<IProvisioningAgent> agentSvcRef = context.getServiceReference(IProvisioningAgent.class);
		IProvisioningAgent agent = context.getService(agentSvcRef);
		return (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
	}

	private void showMessage(ExecutionEvent event, String msg) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(), "Sites GC", msg);
		System.out.println(msg);
	}
	
	private void printRepos() {
		System.out.println("====================================================");
		for (URI uri : sites) {
			System.out.println(uri);
		}
		System.out.println("============== Total Repos: " + sites.size() + " ======================");
	}
}
