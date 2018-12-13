/**
 * 
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.simpleframework.xml.core.Persister;

/**
 * Test reading selection criteria.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class CriteriaTest {
	
	@Test
	public void testRefToMessageId() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " +				
				"  <RefToMessageId>sample-msg-id@test.holodeck-b2b.org</RefToMessageId> " + 
				"</Criteria>";		
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertEquals("sample-msg-id@test.holodeck-b2b.org", criteria.getReferencedMessageId());
			assertNull(criteria.getConversationId());
			assertNull(criteria.getAgreementRef());
			assertNull(criteria.getService());
			assertNull(criteria.getAction());			
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}

	@Test
	public void testConversationId() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " +				
				"  <ConversationId>sample-conversation-id@test.holodeck-b2b.org</ConversationId> " + 
				"</Criteria>";		
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertEquals("sample-conversation-id@test.holodeck-b2b.org", criteria.getConversationId());
			assertNull(criteria.getReferencedMessageId());
			assertNull(criteria.getAgreementRef());
			assertNull(criteria.getService());
			assertNull(criteria.getAction());			
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAgreementRef() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " +				
				"  <AgreementRef>sample-pull-agreement</AgreementRef> " + 
				"</Criteria>";		
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertNull(criteria.getReferencedMessageId());
			assertNull(criteria.getConversationId());
			assertNotNull(criteria.getAgreementRef());
			assertEquals("sample-pull-agreement", criteria.getAgreementRef().getName());					
			assertNull(criteria.getService());
			assertNull(criteria.getAction());			
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testService() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " +				
				"  <Service>sample-pull-service</Service> " + 
				"</Criteria>";		
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertNull(criteria.getReferencedMessageId());
			assertNull(criteria.getConversationId());
			assertNull(criteria.getAgreementRef());
			assertNotNull(criteria.getService());
			assertEquals("sample-pull-service", criteria.getService().getName());					
			assertNull(criteria.getAction());			
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}	
	
	@Test
	public void testAction() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " +				
				"  <Action>GetNextMessage</Action> " + 
				"</Criteria>";		
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertNull(criteria.getReferencedMessageId());
			assertNull(criteria.getConversationId());
			assertNull(criteria.getAgreementRef());
			assertNull(criteria.getService());
			assertEquals("GetNextMessage", criteria.getAction());					
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}	

	@Test
	public void testNone() {
		final String xml = 
				"<Criteria xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"/>";				
		try {
			Criteria criteria = new Persister().read(Criteria.class, xml);
			
			assertNull(criteria.getReferencedMessageId());
			assertNull(criteria.getConversationId());
			assertNull(criteria.getAgreementRef());
			assertNull(criteria.getService());
			assertNull(criteria.getAction());					
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}	
	
}
