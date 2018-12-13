/**
 * 
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.simpleframework.xml.core.Persister;

/**
 * Test reading AgreementRef selection criterion.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class AgreementReferenceTest {
	
	@Test
	public void testNameAndType() {
		final String agreementXML = 
				"<AgreementRef xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\" "
						+ "type=\"http://test.holodeck-b2b.org/agreements\">agreement-to-pull</AgreementRef>";
		
		try {
			AgreementReference agreementRef = new Persister().read(AgreementReference.class, agreementXML);
			assertEquals("http://test.holodeck-b2b.org/agreements", agreementRef.getType());
			assertEquals("agreement-to-pull", agreementRef.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNameOnly() {
		final String agreementXML = 
				"<AgreementRef xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\" "
				+ ">http://test.holodeck-b2b.org/agreements/pulling</AgreementRef>";
		
		try {
			AgreementReference agreementRef = new Persister().read(AgreementReference.class, agreementXML);
			assertNull(agreementRef.getType());
			assertEquals("http://test.holodeck-b2b.org/agreements/pulling", agreementRef.getName());
		} catch (Exception e) {			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNoName() {
		final String agreementXML = 
				"<AgreementRef xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"/>";
		
		try {
			AgreementReference agreementRef = new Persister().read(AgreementReference.class, agreementXML);
			fail();
		} catch (Exception e) {			
		}
	}
}
