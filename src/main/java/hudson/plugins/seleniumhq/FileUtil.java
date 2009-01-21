package hudson.plugins.seleniumhq;

import hudson.EnvVars;
import hudson.Util;

import java.io.File;

/**
 * 
 * @author Pascal Martin
 *
 */
public class FileUtil 
{

	/**
	 * Check in system PATH for the file and return the absolut path
	 * 
	 * Code like FormFieldValidator.Executable
	 * 
	 * @param exe File to find path
	 * @return AbsolutePath of executable file
	 */
	public static String getExecutableAbsolutePath(String exe)
	{
        if(exe==null) {
            return exe;
        }

        if(exe.indexOf(File.separatorChar)>=0) {
            // this is full path
            File f = new File(exe);
            if(f.exists()) {
                return f.getAbsolutePath();
            }

            File fexe = new File(exe+".exe");
            if(fexe.exists()) {
            	return fexe.getAbsolutePath();
            }
            return exe;
        } else {
            // look in PATH
            String path = EnvVars.masterEnvVars.get("PATH");
            String tokenizedPath = "";
            String delimiter = null;
            if(path!=null) {
                for (String _dir : Util.tokenize(path.replace("\\", "\\\\"),File.pathSeparator)) {
                    if (delimiter == null) {
                      delimiter = ", ";
                    }
                    else {
                      tokenizedPath += delimiter;
                    }

                    tokenizedPath += _dir.replace('\\', '/');
                    
                    File dir = new File(_dir);

                    File f = new File(dir,exe);
                    if(f.exists()) {
                        return f.getAbsolutePath();
                    }

                    File fexe = new File(dir,exe+".exe");
                    if(fexe.exists()) {
                    	return fexe.getAbsolutePath();
                    }
                }
                
                tokenizedPath += ".";
            }           
        }
        
		return exe;
	}
}
