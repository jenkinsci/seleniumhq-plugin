package hudson.plugins.seleniumhq;

import hudson.Util;
import hudson.util.IOException2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;

/**
 * 
 * @author Pascal Martin
 *
 */
public class TestResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<SuiteResult> suites = new ArrayList<SuiteResult>();
	transient private List<File> files = new ArrayList<File>();
	
	public TestResult()
	{
			
	}
	
	public TestResult(long buildTime, DirectoryScanner results)
			throws IOException {
		parse(buildTime, results);
	}

	public int getNumTestPasses() {
		int count = 0;
		for (SuiteResult suite : suites) {
			count += suite.getNumTestPasses();
		}
		return count;
	}

	public int getNumTestFailures() {
		int count = 0;
		for (SuiteResult suite : suites) {
			count += suite.getNumTestFailures();
		}
		return count;
	}

	public int getNumTestTotal()
	{
		return getNumTestPasses() + getNumTestFailures();
	}
	
	public List<File> getFiles() {
		return files;
	}

	public void parse(long buildTime, DirectoryScanner results)
			throws IOException {
		String[] includedFiles = results.getIncludedFiles();
		File baseDir = results.getBasedir();

		boolean parsed = false;

		for (String value : includedFiles) {
			File reportFile = new File(baseDir, value);
			// only count files that were actually updated during this build
			if (buildTime - 1000/* error margin */<= reportFile.lastModified()) {
				if (reportFile.length() != 0) 
				{
					parse(reportFile);
					files.add(reportFile);
				}
				else
				{
					throw new IOException(
							"File was empty "
									+ reportFile
									+ "\n"
									+ "  Is this really a Selenium report file? \n  Your configuration must be matching too many files ?");
				}
				parsed = true;
			}
		}

		if (!parsed) {
			long localTime = System.currentTimeMillis();
			if (localTime < buildTime - 1000) /* margin */
				// build time is in the the future. clock on this slave must be
				// running behind
				throw new AbortException(
						"Clock on this slave is out of sync with the master, and therefore \n"
								+ "I can't figure out what test results are new and what are old.\n"
								+ "Please keep the slave clock in sync with the master.");

			File f = new File(baseDir, includedFiles[0]);
			throw new AbortException(String.format(
					"Test reports were found but none of them are new. Did tests run? \n"
							+ "For example, %s is %s old\n", f, Util
							.getTimeSpanString(buildTime - f.lastModified())));
		}
	}

	public void parse(File reportFile) throws IOException {
		try 
		{
			suites.add(SuiteResult.parse( new FileInputStream(reportFile)));
		}  
		catch (Exception e) 
		{
			throw new IOException2(
					"Failed to read "
							+ reportFile
							+ "\n"
							+ "  Is this really a Selenium report file? \n  Your configuration must be matching too many files ?",
					e);
		}
	}
	
}
