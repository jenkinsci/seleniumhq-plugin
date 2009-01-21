package hudson.plugins.seleniumhq;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Builder;
import hudson.util.FormFieldValidator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link SeleniumhqBuilder} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(Build, Launcher, BuildListener)} method will be invoked.
 * 
 * @author Pascal Martin
 */
public class SeleniumhqBuilder extends Builder {

	private final String browser;
	private final String startURL;
	private final String suiteFile;
	private final String resultFile;
	private final String other;

	@DataBoundConstructor
	public SeleniumhqBuilder(String browser, String startURL, String suiteFile,
			String resultFile, String other) {
		this.browser = browser;
		this.startURL = startURL;
		this.suiteFile = suiteFile;
		this.resultFile = resultFile;
		this.other = other;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getBrowser() {
		return browser;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getStartURL() {
		return startURL;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getSuiteFile() {
		return suiteFile;
	}
	
	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getOther() {
		return other;
	}
	
	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getResultFile() {
		return resultFile;
	}
	
	/**
	 * Check if the suiteFile is a URL 
	 * @return true if the suiteFile is a valid url else return false
	 */
	public boolean isURLSuiteFile()
	{
		try
		{
			URL url = new URL(this.suiteFile);
			return url != null;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Check if the suiteFile is a file 
	 * @return true if the suiteFile is a filePath else return false
	 */
	public boolean isFileSuiteFile()
	{
		//org.apache.commons.io.FileUtils.
		if (new File(this.suiteFile).exists())
		{
			File file = new File(this.suiteFile);
			return file.isFile();
		}
		return false;
	}
	
		
	@SuppressWarnings("unchecked")
	public boolean perform(Build build, Launcher launcher, BuildListener listener)  throws IOException, InterruptedException  {
		
		// -------------------------------
		// Check global config
		// -------------------------------
		if (!DESCRIPTOR.isGoodSeleniumRunner())
		{
			listener.error("Please configure the Selenium Remote Control htmlSuite Runner in admin of hudson");
			build.setResult(Result.FAILURE);
			return false;
		}
		
		// -------------------------------
		// Check projet config
		// -------------------------------
		if (this.getBrowser() == null || this.getBrowser().length() == 0)
		{
			listener.error("Build config : browser field is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}
		if (this.getStartURL() == null || this.getStartURL().length() == 0)
		{
			listener.error("Build config : startURL field is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}
		if (this.getSuiteFile() == null || this.getSuiteFile().length() == 0)
		{
			listener.error("Build config : suiteFile field is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}
		if (this.getResultFile() == null || this.getResultFile().length() == 0)
		{
			listener.error("Build config : resultFile field is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}
				
		// -------------------------------
		// check suiteFile type
		// -------------------------------
		String suiteFile = null;
		FilePath tempSuite = null;
		if (this.isFileSuiteFile())
		{
			suiteFile = this.suiteFile;
		}
		else if (this.isURLSuiteFile())
		{			
			tempSuite = build.getProject().getWorkspace().createTempFile("tempHtmlSuite", "html");							
			FileUtils.copyURLToFile(new URL(this.suiteFile), new File(tempSuite.toURI()));			
			//Util.copyStream(url.openStream(), tempSuite.write());
			suiteFile = tempSuite.toURI().getPath();
		}
		else
		{
			// The suiteFile it is a unsuported type
			listener.error("The suiteFile is not a file or an url ! Check your buil configuration.");
			build.setResult(Result.FAILURE);
			return false;
		}
		
		
		// -------------------------------
		// launch : java -jar selenium-server.jar [other] -htmlSuite "{browser}" "{startURL}" "{suiteFile}" "{resultFile}"
		// -------------------------------
		String seleniumRunner = FileUtil.getExecutableAbsolutePath(DESCRIPTOR.getSeleniumRunner());
		String cmd = String.format("java -jar \"%1$s\" %2$s -htmlSuite \"%3$s\" \"%4$s\" \"%5$s\" \"%6$s\"",
									seleniumRunner,
									this.getOther()!= null?this.getOther():"",
									this.getBrowser(),
									this.getStartURL(),
									suiteFile,
									this.getResultFile());
 
		launcher.launch(cmd, build.getEnvVars(), listener.getLogger(), build.getProject().getWorkspace()).join();	    	   	
		
		// -------------------------------
    	// Delete the temp suite file
		// -------------------------------
    	if (tempSuite != null)
    		tempSuite.delete();
    		    
        return true;
    }

	public Descriptor<Builder> getDescriptor() {
		return DESCRIPTOR;
	}

	/**
	 * Descriptor should be singleton.
	 */
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	/**
	 * Descriptor for {@link SeleniumhqBuilder}. Used as a singleton. The class
	 * is marked as public so that it can be accessed from views.
	 * 
	 * <p>
	 * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	public static final class DescriptorImpl extends Descriptor<Builder> {
		/**
		 * To persist global configuration information, simply store it in a
		 * field and call save().
		 * 
		 * <p>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private String seleniumRunner;

		DescriptorImpl() {
			super(SeleniumhqBuilder.class);
			load();
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 * 
		 * @param value
		 *            This receives the current value of the field.
		 */
		public void doCheckSeleniumRunner(StaplerRequest req, StaplerResponse rsp,@QueryParameter final String value) throws IOException, ServletException 
		{
			new FormFieldValidator.Executable(req,rsp).process();
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "SeleniumHQ htmlSuite Run";
		}

		public boolean configure(StaplerRequest req, JSONObject o)
				throws FormException {
			// to persist global configuration information,
			// set that to properties and call save().
			seleniumRunner = o.getString("seleniumRunner");
			save();
			return super.configure(req, o);
		}
	
		public String getSeleniumRunner() {
			return seleniumRunner;
		}
		
		/**
		 * For junit test
		 * @param seleniumRunner
		 */
		public void setSeleniumRunner(String seleniumRunner) {
			this.seleniumRunner = seleniumRunner;
		}

		public boolean isGoodSeleniumRunner()
		{
			return this.seleniumRunner != null && this.seleniumRunner.length() > 0;
		}
		
		public void doCheckBrowser(StaplerRequest req, StaplerResponse rsp,@QueryParameter final String value) throws IOException, ServletException 
		{
			new EmptyFormFieldValidator(value, "browser is mandatory", null).process();			
		}
		
		public void doCheckStartURL(StaplerRequest req, StaplerResponse rsp,@QueryParameter final String value) throws IOException, ServletException 
		{
			new EmptyFormFieldValidator(value, "startURL is mandatory", null).process();			
		}
		
		public void doCheckSuiteFile(StaplerRequest req, StaplerResponse rsp,@QueryParameter final String value) throws IOException, ServletException 
		{
			new EmptyFormFieldValidator(value, "suiteFile is mandatory", null).process();			
		}
		
		public void doCheckResultFile(StaplerRequest req, StaplerResponse rsp,@QueryParameter final String value) throws IOException, ServletException 
		{
			new EmptyFormFieldValidator(value, "resultFile is mandatory", null).process();			
		}
	}
}
