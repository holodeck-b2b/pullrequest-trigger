/**
 * 
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Represents the root element <code>PullRequestMetadata</code> from the XML document that is used to trigger a Pull 
 * Request in Holodeck B2B. The structure of these documents is defined by XML schema 
 * <i>http://holodeck-b2b.org/schemas/2018/02/pullrequest/metdata</i>. 
 * <p>These "trigger" documents must contain at least the P-Mode that governs the processing of the Pull Request and can
 * further configure the MPC [sub-channell], the selection criteria to use for pulling and since version 1.1.0 the 
 * <code>MessageId</code> to use for the Pull Request.
 * <p>NOTE: In the current version only the "simple" selection criteria as described in section 5.1 of the ebMS V3 
 * Part 2 Specification are supported.
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 * @since 1.1 Support for specifying of the Pull Request's <code>eb:MessageId</code>
 */
@Root(name = "PullRequestMetadata",strict = false)
@Namespace(reference="http://holodeck-b2b.org/schemas/2018/02/pullrequest/metdata")
public class PullRequestMetadata {

	@Element(name="PModeId")
	private String	pmodeId;
	
	@Element(name="MessageId", required=false)
	private String  messageId;
	
	@Element(name="Subchannel", required=false)
	private String	subchannel;
	
	@Element(name="Criteria", required=false)
	private Criteria	criteria;

    /**
     * Creates a new <code>PullRequestMetadata</code> instance from the XML document in the specified {@link File}.
     *
     * @param  triggerFile  A handle to file that contains the meta data
     * @return              A <code>PullRequestMetadata</code> instance for the meta data contained in the given file
     * @throws Exception    When the specified file is not found, readable or doesn't contain a Pull Request trigger
     */
    public static PullRequestMetadata createFromFile(final File triggerFile) throws Exception {
        if( !triggerFile.exists() || !triggerFile.canRead())
            // Given file must exist and be readable 
            throw new Exception("Specified file [" + triggerFile.getAbsolutePath() 
            						+ "] not found or no permission to read!");

        PullRequestMetadata prmd = null;
        try {
            final Serializer  serializer = new Persister();
            prmd = serializer.read(PullRequestMetadata.class, triggerFile);
        } catch (final Exception ex) {
            // The specified file could not be read as a Pull Request trigger document
            throw new Exception("Problem reading Pull Request Metadata from " + triggerFile.getAbsolutePath(), ex);
        }

        return prmd;
    }
    
	/**
	 * Gets the id of the P-Mode that should be used for processing of the Pull Request.
	 * 
	 * @return	PMode.id of P-Mode governing the pulling
	 */
	public String getPModeId() {
		return pmodeId;
	}
	
	/**
	 * Gets the MessageId that should be used for the Pull Request.
	 * 
	 * @return	The MessageId to be used for the Pull Request
	 * @since 1.1.0
	 */
	public String getMessageId() {
		return messageId;
	}
	
	/**
	 * Gets the sub-channel that should be appended to the MPC specified in the P-Mode. 
	 * 
	 * @return	The sub-channel to be used for the Pull Request
	 */
	public String getSubchannel() {
		return subchannel;
	}
	
	/**
	 * Gets the selection criteria specified in the meta-data document and which should be included in the Pull Request.
	 *  
	 * @return	The selection criteria to be used for the Pull Request
	 */
	public Criteria getSelectionCriteria() {
		return criteria;
	}
}
