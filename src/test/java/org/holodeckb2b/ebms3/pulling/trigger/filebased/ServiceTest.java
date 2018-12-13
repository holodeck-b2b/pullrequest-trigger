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
 * Test reading Service selection criterion.
 * 
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class ServiceTest {
	
	@Test
	public void testNameAndType() {
		final String serviceXML = 
				"<Service xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\" "
						+ "type=\"http://test.holodeck-b2b.org/services\">PullService</Service>";
		
		try {
			Service service = new Persister().read(Service.class, serviceXML);
			assertEquals("http://test.holodeck-b2b.org/services", service.getType());
			assertEquals("PullService", service.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNameOnly() {
		final String serviceXML = 
				"<Service xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\" "
				+ ">http://test.holodeck-b2b.org/services/pulling</Service>";
		
		try {
			Service service = new Persister().read(Service.class, serviceXML);
			assertNull(service.getType());
			assertEquals("http://test.holodeck-b2b.org/services/pulling", service.getName());
		} catch (Exception e) {			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNoName() {
		final String serviceXML = 
				"<Service xmlns=\"http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger\"/>";
		
		try {
			Service service = new Persister().read(Service.class, serviceXML);
			fail();
		} catch (Exception e) {			
		}
	}
}
