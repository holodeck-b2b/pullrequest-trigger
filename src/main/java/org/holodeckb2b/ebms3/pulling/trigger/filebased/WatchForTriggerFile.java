/**
x * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.holodeckb2b.common.messagemodel.PullRequest;
import org.holodeckb2b.common.messagemodel.SelectivePullRequest;
import org.holodeckb2b.common.util.Utils;
import org.holodeckb2b.common.workerpool.AbstractWorkerTask;
import org.holodeckb2b.interfaces.core.HolodeckB2BCoreInterface;
import org.holodeckb2b.interfaces.general.EbMSConstants;
import org.holodeckb2b.interfaces.messagemodel.IPullRequest;
import org.holodeckb2b.interfaces.pmode.IBusinessInfo;
import org.holodeckb2b.interfaces.pmode.ILeg;
import org.holodeckb2b.interfaces.pmode.ILeg.Label;
import org.holodeckb2b.interfaces.pmode.IPMode;
import org.holodeckb2b.interfaces.pmode.IPullRequestFlow;
import org.holodeckb2b.interfaces.pmode.IUserMessageFlow;
import org.holodeckb2b.interfaces.submit.MessageSubmitException;
import org.holodeckb2b.interfaces.workerpool.TaskConfigurationException;
import org.holodeckb2b.pmode.PModeUtils;

/**
 * This worker reads all <i>"Pull Request trigger documents"</i> from the specified directory and then submits the 
 * corresponding Pull Request Signal message unit to the Holodeck B2B Core to trigger the pull process.
 * <p>The files to process must have extension <b>xml</b>. After processing the file, i.e. after the Pull Request has
 * been submitted, the extension will be changed to <b>triggered</b>. When an error occurs on submit the extension will
 * be changed to <b>rejected</b> and information on the error will be written to a file with the same name but
 * with extension <b>err</b>.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 1.1.0 Support for setting of the Pull Request's <code>eb:MessageId</code>
 */
public class WatchForTriggerFile extends AbstractWorkerTask {
    /**
     * The path to the directory to watch for files
     */
    private String watchPath;

    /**
     * Initializes the worker. 
     *
     * @param parameters	The parameters as specified in the "workers.xml" file
     * @throws TaskConfigurationException When the provided configuration is not valid, for example because the given
     * 									  directory does not exist
     */
    @Override
    public void setParameters(final Map<String, ?> parameters) throws TaskConfigurationException {
        // Check the watchPath parameter is provided and points to a directory
        final String pathParameter = (String) parameters.get("watchPath");
        if (Utils.isNullOrEmpty(pathParameter)) {
            log.error("Unable to configure task: Missing required parameter \"watchPath\"");
            throw new TaskConfigurationException("Missing required parameter \"watchPath\"");
        } else if (!Paths.get(pathParameter).isAbsolute())
            watchPath = Paths.get(HolodeckB2BCoreInterface.getConfiguration().getHolodeckB2BHome(), pathParameter).toString();
        else
            watchPath = pathParameter;

        final File dir = new File(watchPath);
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            log.error("The specified directory to watch for triggers [" + watchPath + "] is not accessible to HB2B");
            throw new TaskConfigurationException("Invalid path specified!");
        }
    }

    @Override
    public void doProcessing() {
        log.trace("Checking for trigger files in watched directory: " + watchPath);
        final File   dir = new File(watchPath);
        final File[] triggerFiles = dir.listFiles(new FileFilter() {
                                        @Override
                                        public boolean accept(final File file) {
                                            return file.isFile() && file.getName().toLowerCase().endsWith(".xml");
                                        }
                                    });
        // A null value indicates the directory could not be read => signal as error
        if (triggerFiles == null) {
            log.error("The specified directory [" + watchPath 
            			+ "]could not be searched for Pull Request trigger files!");
            return;
        }

        for(File f : triggerFiles) {
            // Get file name without the extension
            final String  cFileName = f.getAbsolutePath();
            final String  baseFileName = cFileName.substring(0, cFileName.toLowerCase().indexOf(".xml"));
            final String tFileName = baseFileName + ".processing";

	        try {
	            // Directly rename file to prevent processing by another worker
	            if( !f.renameTo(new File(tFileName)))
	                // Renaming failed, so file already processed by another worker or externally
	                // changed
	                log.debug(f.getName() + " is not processed because it could not be renamed");
	            else {
	                // The file can be processed
	                log.trace("Read Pull Request meta data from " + f.getName());
	                final PullRequestMetadata prmd = PullRequestMetadata.createFromFile(new File(tFileName));
	                log.trace("Succesfully read meta data, create Pull Request for submission to Core");
	                final IPullRequest pullRequest = createPullRequest(prmd);
	                log.trace("Submitting Pull Request to Core");	                
	                HolodeckB2BCoreInterface.getMessageSubmitter().submitMessage(pullRequest);
	                log.info("Pull Request based on " + f.getName() + " succesfully submitted to Holodeck B2B");
	                // Change extension to reflect success
	                Files.move(Paths.get(tFileName), Utils.createFileWithUniqueName(baseFileName + ".triggered")
	                           , StandardCopyOption.REPLACE_EXISTING);
	            }
	        } catch (final Exception e) {
	            // Something went wrong on reading the message meta data
	            log.error("An error occured when reading message meta data from " + f.getName()
	                        + ". Details: " + e.getMessage());
	            // Change extension to reflect error and write error information
	            try {
	                final Path rejectFilePath = Utils.createFileWithUniqueName(baseFileName + ".rejected");
	                Files.move(Paths.get(tFileName), rejectFilePath, StandardCopyOption.REPLACE_EXISTING);
	                writeErrorFile(rejectFilePath, e);
	            } catch (IOException ex) {
	                // The directory where the file was originally found has gone. Nothing we can do about it, so ignore
	                log.error("An error occured while renaming the mmd file or writing the error info to file!");
	            }
	        }
        }
    }

    /**
     * Creates a Pull Request for submission to the Core based on the meta-data provided. Checks whether a P-Mode exists
     * with the given id and if the provided MPC (if any) is compatible with it. Also adds provided selection criteria
     * to the Pull Request.  
     * 
     * @param prmd	The meta-data to create the Pull Request
     * @return		The created Pull Request to submit 
     * @throws	MessageSubmitException	When no Pull Request can be created, for example because the P-Mode does not 
     * 									exist or provided MPC value is incompatible with it
     */
    private IPullRequest createPullRequest(final PullRequestMetadata prmd) throws MessageSubmitException {
    	final IPMode pmode = HolodeckB2BCoreInterface.getPModeSet().get(prmd.getPModeId()); 
    	if (pmode == null) 
    		throw new MessageSubmitException("No P-Mode found with id: " + prmd.getPModeId());
    	
    	// Check that this is a P-Mode for pulling
    	if (!EbMSConstants.ONE_WAY_PULL.equals(pmode.getMepBinding()))
    		throw new MessageSubmitException("The specified P-Mode [" + prmd.getPModeId() 
    																				+ "] can not be used for pulling");
    	
    	// Get the MPC defined in the P-Mode
		String pmodeMPC = null;
		final IPullRequestFlow prFlow = PModeUtils.getOutPullRequestFlow(pmode);
		pmodeMPC = prFlow != null ? prFlow.getMPC() : null;
		if (Utils.isNullOrEmpty(pmodeMPC)) {
			// Either no specific Pull Request configuration in P-Mode or no MPC specified for it,
			// check general MPC setting 
			final ILeg leg = pmode.getLeg(Label.REQUEST);
			final IUserMessageFlow umFlow = leg != null ? leg.getUserMessageFlow() : null;
			final IBusinessInfo businessInfo = umFlow != null ? umFlow.getBusinessInfo() : null;
			pmodeMPC = businessInfo != null ? businessInfo.getMpc() : null;
			if (Utils.isNullOrEmpty(pmodeMPC))
				pmodeMPC = EbMSConstants.DEFAULT_MPC;			
    	}
		// Append sub-channel if specified
		StringBuilder pullMPC = new StringBuilder(pmodeMPC);
		if (!Utils.isNullOrEmpty(prmd.getSubchannel())) {			
			if (!pmodeMPC.endsWith("/"))
				pullMPC.append("/");
			pullMPC.append(prmd.getSubchannel());
			log.trace("Added specified subchannel to MPC defined in P-Mode: " + pmodeMPC.toString());
		}
		// Create the Pull Request
		PullRequest pullRequest = new PullRequest(pmode.getId(), pullMPC.toString());
    	// Set the MessageId if specified
		pullRequest.setMessageId(prmd.getMessageId());
		// Extend with selection criteria if specified
		final Criteria criteria = prmd.getSelectionCriteria();
    	if (criteria != null) {
    		log.trace("Selection criteria specified in trigger, create a selective pull request");
    		SelectivePullRequest selectiveRequest = new SelectivePullRequest(pullRequest);
    		selectiveRequest.setReferencedMessageId(criteria.getReferencedMessageId());
    		selectiveRequest.setConversationId(criteria.getConversationId());
    		selectiveRequest.setAgreementRef(criteria.getAgreementRef());
    		selectiveRequest.setService(criteria.getService());
    		selectiveRequest.setAction(criteria.getAction());
    		pullRequest = selectiveRequest;
    	}
    	
    	log.debug("Created " + (pullRequest instanceof SelectivePullRequest ? "selective " : "") 
    				+ "Pull Request using P-Mode: " + pmode.getId());	
    	return pullRequest;
    }

    /**
     * Writes error information to file when a submission failed.
     *
     * @param rejectFilePath   The path to the renamed metadata file. Used to determine file name for the error file.
     * @param fault            The exception that caused the submission to fail
     */
    protected void writeErrorFile(final Path rejectFilePath, final Exception fault) {
        // Split the given path into name and extension part (if possible)
        String nameOnly = rejectFilePath.toString();
        final int startExt = nameOnly.lastIndexOf(".");
        if (startExt > 0)
            nameOnly = nameOnly.substring(0, startExt);
        final String errFileName = nameOnly + ".err";

        log.trace("Writing submission error to error file: " + errFileName);
        try (PrintWriter errorFile = new PrintWriter(new File(errFileName))) {

            errorFile.write("The Pull Request could not be submitted to Holodeck B2B due to an error:\n\n");
            errorFile.write("Error type:    " + fault.getClass().getSimpleName() + "\n");
            errorFile.write("Error message: " + fault.getMessage() + "\n");
            errorFile.write("\n\nError details\n-------------\n");
            errorFile.write("Exception cause: " + (fault.getCause() != null
                                                                ? fault.getCause().toString() : "unknown") + "\n");
            errorFile.write("Stacktrace:\n");
            fault.printStackTrace(errorFile);

            log.debug("Error information written to file");
        } catch (final IOException ioe) {
            log.error("Could not write error information to error file [" + errFileName + "]!");
        }
    }
}
