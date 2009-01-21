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
		
	public SuiteResult(int numTestPasses, int numTestFailures)
	{
		this.numTestPasses = numTestPasses;
		this.numTestFailures = numTestFailures;		
	}	

	public int getNumTestPasses() {
		return numTestPasses;
	}

	public int getNumTestFailures() {
		return numTestFailures;
	}

	public static SuiteResult parse(InputStream xmlReport) throws Exception {
		int numTestPasses = 0;
		int numTestFailures = 0;
		
		BufferedReader buff = null;
		try
		{			
			buff = new BufferedReader( new InputStreamReader(xmlReport));

			numTestPasses = Integer.valueOf(readInfo(buff,"numTestPasses:")).intValue();
			numTestFailures = Integer.valueOf(readInfo(buff,"numTestFailures:")).intValue();
			
			buff.close();
		}
		finally 
		{
			if (buff != null)
			{
				buff.close();
			}
		}
		
        return new SuiteResult(numTestPasses, numTestFailures);
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
