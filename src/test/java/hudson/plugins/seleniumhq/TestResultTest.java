package hudson.plugins.seleniumhq;

import junit.framework.TestCase;

public class TestResultTest extends TestCase
{
	public void testTestResult() throws Exception {
		new TestResult();
    }
	
	public void testGetNumTestPasses() throws Exception {
		TestResult result = new TestResult();
		assertEquals(0, result.getNumTestPasses());
    }
	
	public void testGetNumTestFailures() throws Exception {
		TestResult result = new TestResult();
		assertEquals(0, result.getNumTestFailures());
    }
	
	public void testGetNumTestTotal() throws Exception {
		TestResult result = new TestResult();
		assertEquals(0, result.getNumTestTotal());
    }
	
	public void testGetFiles() throws Exception {
		TestResult result = new TestResult();
		assertEquals(0, result.getFiles().size());		
    }
	
	
	
}
