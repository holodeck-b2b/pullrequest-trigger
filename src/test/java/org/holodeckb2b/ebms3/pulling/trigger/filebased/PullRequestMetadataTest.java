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
 * Test reading of the Pull Request meta-data. 
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class PullRequestMetadataTest {

	@Test
	public void testPModeIdOnly() {
		final String xml = 
				"<PullRequestMetadata xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " + 
				"    <PModeId>sample-receive-pmode-id</PModeId> " +
				"</PullRequestMetadata>";
		try {
			PullRequestMetadata prmd = new Persister().read(PullRequestMetadata.class, xml);
			
			assertEquals("sample-receive-pmode-id", prmd.getPModeId());
			assertNull(prmd.getSubchannel());
			assertNull(prmd.getSelectionCriteria());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}

	@Test
	public void testWithMessageId() {
		final String xml = 
				"<PullRequestMetadata xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " + 
						"    <PModeId>sample-receive-pmode-id</PModeId> " +
						"    <MessageId>uuid-1@test.holodeck-b2b.org</MessageId> " +
						"</PullRequestMetadata>";
		try {
			PullRequestMetadata prmd = new Persister().read(PullRequestMetadata.class, xml);
			
			assertEquals("sample-receive-pmode-id", prmd.getPModeId());
			assertEquals("uuid-1@test.holodeck-b2b.org", prmd.getMessageId());
			assertNull(prmd.getSubchannel());
			assertNull(prmd.getSelectionCriteria());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testWithSubchannel() {
		final String xml = 
				"<PullRequestMetadata xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " + 
				"    <PModeId>sample-receive-pmode-id</PModeId> " +
				"	 <Subchannel>highprio</Subchannel> " +
				"</PullRequestMetadata>";
		try {
			PullRequestMetadata prmd = new Persister().read(PullRequestMetadata.class, xml);
			
			assertEquals("sample-receive-pmode-id", prmd.getPModeId());
			assertNull(prmd.getMessageId());
			assertEquals("highprio", prmd.getSubchannel());
			assertNull(prmd.getSelectionCriteria());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testWithMsgIdAndSubchannel() {
		final String xml = 
				"<PullRequestMetadata xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " + 
						"    <PModeId>sample-receive-pmode-id</PModeId> " +
						"    <MessageId>uuid-1@test.holodeck-b2b.org</MessageId> " +
						"	 <Subchannel>highprio</Subchannel> " +
						"</PullRequestMetadata>";
		try {
			PullRequestMetadata prmd = new Persister().read(PullRequestMetadata.class, xml);
			
			assertEquals("sample-receive-pmode-id", prmd.getPModeId());
			assertEquals("uuid-1@test.holodeck-b2b.org", prmd.getMessageId());
			assertEquals("highprio", prmd.getSubchannel());
			assertNull(prmd.getSelectionCriteria());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}

	@Test
	public void testWithCriteria() {
		final String xml = 
				"<PullRequestMetadata xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"> " + 
				"    <PModeId>sample-receive-pmode-id</PModeId> " +
				"    <Criteria> " + 
				"        <RefToMessageId>sample-msg-id@test.example.com</RefToMessageId> " + 
				"    </Criteria> " + 
				"</PullRequestMetadata>";
		try {
			PullRequestMetadata prmd = new Persister().read(PullRequestMetadata.class, xml);
			
			assertEquals("sample-receive-pmode-id", prmd.getPModeId());
			assertNull(prmd.getMessageId());
			assertNull(prmd.getSubchannel());
			assertNotNull(prmd.getSelectionCriteria());
			assertEquals("sample-msg-id@test.example.com", prmd.getSelectionCriteria().getReferencedMessageId());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	
}
