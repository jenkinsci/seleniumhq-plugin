package hudson.plugins.seleniumhq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * 
 * @author Pascal Martin
 *
 */
public class SuiteResult implements Serializable {

	private int numTestPasses;
	private int numTestFailures;
	private int numCommandPasses;
	private int numCommandFailures;
	private int numCommandErrors;

		
	public SuiteResult(int numTestPasses, int numTestFailures)
	{
		this.numTestPasses = numTestPasses;
		this.numTestFailures = numTestFailures;
	}	

	public SuiteResult(int numTestPasses, int numTestFailures, int numCommandPasses,
		int numCommandFailures, int numCommandErrors)
	{
		this.numTestPasses = numTestPasses;
		this.numTestFailures = numTestFailures;
		this.numCommandPasses = numCommandPasses;
		this.numCommandFailures = numCommandFailures;
		this.numCommandErrors = numCommandErrors;
	}	
	public int getNumTestPasses() {
		return numTestPasses;
	}

	public int getNumTestFailures() {
		return numTestFailures;
	}

	public int numCommandPasses() {
		return numCommandPasses;
	}

	public int numCommandFailures() {
		return numCommandFailures;
	}

	public int numCommandErrors() {
		return numCommandErrors;
	}

	public static SuiteResult parse(InputStream xmlReport) throws Exception {
		int numTestPasses = 0;
		int numTestFailures = 0;
		int numCommandPasses = 0;
		int numCommandFailures = 0;
		int numCommandErrors = 0;
		
		BufferedReader buff = null;
		try
		{			
			buff = new BufferedReader( new InputStreamReader(xmlReport));

			numTestPasses = Integer.valueOf(readInfo(buff,"numTestPasses:")).intValue();
			numTestFailures = Integer.valueOf(readInfo(buff,"numTestFailures:")).intValue();
			numCommandPasses = Integer.valueOf(readInfo(buff,"numCommandPasses:")).intValue();
			numCommandFailures = Integer.valueOf(readInfo(buff,"numCommandFailures:")).intValue();
			numCommandErrors = Integer.valueOf(readInfo(buff,"numCommandErrors:")).intValue();
			
			buff.close();
		}
		finally 
		{
			if (buff != null)
			{
				buff.close();
			}
		}
		
        return new SuiteResult(numTestPasses, numTestFailures, numCommandPasses, numCommandFailures, numCommandErrors);
    }
	
	private static String readInfo(BufferedReader buff, String infoName) throws IOException
	{
		String line;
		boolean isNextLine = false;
		while ((line = buff.readLine()) != null) 
		{
			if (isNextLine)
			{				
				return line.substring(4, line.length()-5);
			}
			if (line.indexOf(infoName) != -1)
			{
				isNextLine = true;				
			}
		}
		return null;		
	}

	private static final long serialVersionUID = 1L;
}
