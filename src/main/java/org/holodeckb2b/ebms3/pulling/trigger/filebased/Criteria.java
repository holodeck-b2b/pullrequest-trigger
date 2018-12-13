/**
 * 
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import org.holodeckb2b.interfaces.general.IService;
import org.holodeckb2b.interfaces.messagemodel.IAgreementReference;
import org.simpleframework.xml.Element;

/**
 * Represents the <code>Criteria</code> element from a Pull Request meta-data XML document. This element contains the 
 * criteria for a <i>selective</i> Pull Request. The possible selection criteria are described in section 5.1 of the 
 * ebMS 3 Part 2 Specification. 
 * <p>NOTE: In the current version only the <i>simple</i> criteria are supported.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class Criteria {

	@Element(name="RefToMessageId", required=false)
	private String	refToMessageId;
	
	@Element(name="ConversationId", required=false)
	private String	conversationId;
	
	@Element(name="AgreementRef", required=false)
	private AgreementReference	agreementRef;
	
	@Element(name="Service", required=false)
	private Service	service;
	
	@Element(name="Action", required=false)
	private String	action;
	
    /**
     * Gets the <i>RefToMessageId</i> selection criterion that should be included in the PullRequest.
     *
     * @return      The <i>RefToMessageId</i> of the message to be pulled
     */
    public String getReferencedMessageId() {
    	return refToMessageId;
    }

    /**
     * Gets the <i>ConversationId</i> selection criterion that should be included in the PullRequest.
     *
     * @return      The <i>ConversationId</i> of the message to be pulled
     */
    public String getConversationId() {
    	return conversationId;
    }

    /**
     * Gets the <i>Agreement Reference</i> selection criterion that should be included in the PullRequest.
     *
     * @return      The <i>Agreement Reference</i> of the message to be pulled
     */
    public IAgreementReference getAgreementRef() {
    	return agreementRef;
    }

    /**
     * Gets the <i>Service</i> selection criterion that should be included in the PullRequest.
     *
     * @return      The <i>Service</i> of the message to be pulled
     */
    public IService getService() {
    	return service;
    }

    /**
     * Gets the <i>Action</i> selection criterion that should be included in the PullRequest.
     *
     * @return      The <i>Action</i> of the message to be pulled
     */
    public String getAction() {
    	return action;
    }
}
