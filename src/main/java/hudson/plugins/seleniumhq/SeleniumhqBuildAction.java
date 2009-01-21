package hudson.plugins.seleniumhq;


import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.util.XStream2;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Pascal Martin
 *
 */
public class SeleniumhqBuildAction implements Action, Serializable { 

	private transient WeakReference<TestResult> result;
	
	private static final long serialVersionUID = 1L;
	
	public final AbstractBuild<?,?> owner;
	
	private static final Logger logger = Logger.getLogger(SeleniumhqBuildAction.class.getName());
			
    public SeleniumhqBuildAction(AbstractBuild<?,?> owner, TestResult result, BuildListener listener) {
        this.owner = owner;
        
        // persist the data
        try {
            getDataFile().write(result);
        } catch (IOException e) {
            e.printStackTrace(listener.fatalError("Failed to save the Selenium test result"));
        }
    }
    
    private XmlFile getDataFile() {
        return new XmlFile(XSTREAM,new File(owner.getRootDir(), "seleniumhqResult.xml"));
    }
	
    public Object getTarget() {
        return getResult();  
    }
    
    public String getIconFileName() {
        return "graph.gif";  
    }

    public String getDisplayName() {
        return "Selenium Report";  
    }

    public String getUrlName() {
        return "seleniumhq";  
    }

    public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public synchronized TestResult getResult() {
        TestResult r;
        if(result==null) {
            r = load();
            result = new WeakReference<TestResult>(r);
        } else {
            r = result.get();
        }
        if(r==null) {
            r = load();
            result = new WeakReference<TestResult>(r);
        }
        return r;
    }
    
    public synchronized SeleniumhqBuildAction getPreviousResult() {
    	AbstractBuild<?, ?> b = owner;
        while (true) {
            b = b.getPreviousBuild();
            if (b == null)
                return null;
            if (b.getResult() == Result.FAILURE)
                continue;
            SeleniumhqBuildAction r = b.getAction(SeleniumhqBuildAction.class);
            if (r != null)
                return r;
        }
    }

    
    /**
     * Loads a {@link TestResult} from disk.
     */
    private TestResult load() {
        TestResult r;
        try {
            r = (TestResult)getDataFile().read();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load "+getDataFile(),e);
            r = new TestResult();   // return a dummy
        }
        return r;
    }
    
    private static final XStream XSTREAM = new XStream2();
}
