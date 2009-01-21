package hudson.plugins.seleniumhq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * JUnit test for {@link SuiteResult}
 */
public class SuiteResultTest extends TestCase {


	public void testParse1() throws Exception {
		SuiteResult sr = SuiteResult.parse( SuiteResultTest.class.getResourceAsStream("testResult.html") );
		assertEquals(7, sr.getNumTestPasses());
		assertEquals(0, sr.getNumTestFailures());
    }	
	
	public void testParse2() throws Exception {				
		try 
		{            
			String testBadInput = "";
			InputStream in = new ByteArrayInputStream(testBadInput.getBytes());
			SuiteResult.parse( in );
			fail("Should have NumberFormatException");        
		} 
		catch (NumberFormatException expected) 
		{            
			return;       
		}
    }
}
