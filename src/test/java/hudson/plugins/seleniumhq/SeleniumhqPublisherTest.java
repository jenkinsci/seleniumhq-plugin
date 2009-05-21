package hudson.plugins.seleniumhq;

import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.Publisher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SingleFileSCM;

public class SeleniumhqPublisherTest extends HudsonTestCase
{
	/**
	 * Test du constructeur
	 */
	public void testSeleniumhqPublisher()
	{
		SeleniumhqPublisher publisher = new SeleniumhqPublisher("*.html");
		assertTrue(publisher instanceof Publisher);
	}
	
	/**
	 * Test de la méthode getTestResults
	 */
	public void testGetTestResults()
	{
		SeleniumhqPublisher publisher = new SeleniumhqPublisher("*.html");
		assertEquals("*.html", publisher.getTestResults());
	}
	
	/**
	 * Test de la méthode getDescriptor
	 */
	public void testGetDescriptor()
	{
		SeleniumhqPublisher publisher = new SeleniumhqPublisher("*.html");
		Descriptor<Publisher> descriptor =  publisher.getDescriptor();
		assertTrue(descriptor instanceof SeleniumhqPublisher.DescriptorImpl);
		assertEquals("/plugin/seleniumhq/help-publisher.html", descriptor.getHelpFile());
		assertEquals("Publish Selenium Report", descriptor.getDisplayName());
	}
	
	/**
	 * Test de la méthode getProjectAction
	 */
	public void testGetProjectAction()
	{
		SeleniumhqPublisher publisher = new SeleniumhqPublisher("*.html");
		assertTrue(publisher.getProjectAction(null) instanceof SeleniumhqProjectAction);
	}
	
	/**
	 * Test du Publisher avec aucun fichier source
	 * @throws Exception
	 */
	public void test1() throws Exception 
	{
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
              
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        assertEquals(Result.FAILURE, build.getResult());

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("Publishing Selenium report..."));
        assertTrue(s.contains("No Test Report Found"));
    }

	/**
	 * Test du Publisher avec 1 fichier source invalide
	 * @throws Exception
	 */
	public void test2() throws Exception 
	{
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        project.setScm(new SingleFileSCM("badResult.html", getClass().getResource("badResult.html")));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.FAILURE, build.getResult());

        String s = FileUtils.readFileToString(build.getLogFile());

        assertTrue(s.contains("Staging badResult.html"));
        assertTrue(s.contains("Publishing Selenium report..."));
        assertTrue(s.contains("ERROR: Failed to archive Selenium reports"));
    }
	
	/**
	 * Test du Publisher avec 1 fichier source invalide vide
	 * @throws Exception
	 */
	public void test3() throws Exception 
	{
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        project.setScm(new SingleFileSCM("emptyResult.html", getClass().getResource("emptyResult.html")));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.FAILURE, build.getResult());

        String s = FileUtils.readFileToString(build.getLogFile());

        assertTrue(s.contains("Staging emptyResult.html"));
        assertTrue(s.contains("Publishing Selenium report..."));
        assertTrue(s.contains("ERROR: Failed to archive Selenium reports"));
    }
	
	/**
	 * Test du Publisher avec 1 fichier valide
	 * @throws Exception
	 */
	public void test4() throws Exception 
	{
		/*
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        project.setScm(new SingleFileSCM("testResult.html", getClass().getResource("testResult.html")));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.SUCCESS, build.getResult());

        Action action = build.getAction(0);
        assertTrue(action instanceof SeleniumhqBuildAction);
        
        SeleniumhqBuildAction buildAction = (SeleniumhqBuildAction)action;
        assertEquals(0, buildAction.getResult().getNumTestFailures());
        assertEquals(7, buildAction.getResult().getNumTestPasses());
        assertEquals(7, buildAction.getResult().getNumTestTotal());
        */
    }
	
	/**
	 * Test du Publisher avec 1 fichier valide avec failures
	 * @throws Exception
	 */
	public void test5() throws Exception 
	{
		/*
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        project.setScm(new SingleFileSCM("testResultWithFailure.html", getClass().getResource("testResultWithFailure.html")));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.UNSTABLE, build.getResult());

        Action action = build.getAction(0);
        assertTrue(action instanceof SeleniumhqBuildAction);
        
        SeleniumhqBuildAction buildAction = (SeleniumhqBuildAction)action;
        assertEquals(1, buildAction.getResult().getNumTestFailures());
        assertEquals(2, buildAction.getResult().getNumTestPasses());
        assertEquals(3, buildAction.getResult().getNumTestTotal());
        */
    }
	
	/**
	 * Test du Publisher avec 2 fichier valide
	 * @throws Exception
	 */
	public void test6() throws Exception 
	{
		/*
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        List<SingleFileSCM> files = new ArrayList<SingleFileSCM>(2);
        files.add(new SingleFileSCM("testResult1.html", getClass().getResource("testResult.html")));
        files.add(new SingleFileSCM("testResult2.html", getClass().getResource("testResult.html")));
        project.setScm(new MultiFileSCM(files));     
        
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.SUCCESS, build.getResult());

        Action action = build.getAction(0);
        assertTrue(action instanceof SeleniumhqBuildAction);
        
        SeleniumhqBuildAction buildAction = (SeleniumhqBuildAction)action;
        assertEquals(0, buildAction.getResult().getNumTestFailures());
        assertEquals(14, buildAction.getResult().getNumTestPasses());
        assertEquals(14, buildAction.getResult().getNumTestTotal());
        */               
    }
	
	/**
	 * Test du Publisher avec 3 fichier 2 valide et 1 avec failure
	 * @throws Exception
	 */
	public void test7() throws Exception 
	{
		/*
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        List<SingleFileSCM> files = new ArrayList<SingleFileSCM>(2);
        files.add(new SingleFileSCM("testResult1.html", getClass().getResource("testResult.html")));
        files.add(new SingleFileSCM("testResult2.html", getClass().getResource("testResult.html")));
        files.add(new SingleFileSCM("testResult3.html", getClass().getResource("testResultWithFailure.html")));
        
        project.setScm(new MultiFileSCM(files));     
        
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.UNSTABLE, build.getResult());

        Action action = build.getAction(0);
        assertTrue(action instanceof SeleniumhqBuildAction);
        
        SeleniumhqBuildAction buildAction = (SeleniumhqBuildAction)action;
        assertEquals(1, buildAction.getResult().getNumTestFailures());
        assertEquals(16, buildAction.getResult().getNumTestPasses());
        assertEquals(17, buildAction.getResult().getNumTestTotal());
        */               
    }
	
	/**
	 * Test du Publisher avec 1 fichier valide avec 0 test
	 * @throws Exception
	 */
	public void test8() throws Exception 
	{
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new SeleniumhqPublisher("*.html"));
        
        project.setScm(new SingleFileSCM("testResultNoTest.html", getClass().getResource("testResultNoTest.html")));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        
        assertEquals(Result.FAILURE, build.getResult());

        String s = FileUtils.readFileToString(build.getLogFile());

        assertTrue(s.contains("Staging testResultNoTest.html"));
        assertTrue(s.contains("Publishing Selenium report..."));
        assertTrue(s.contains("ERROR: Result does not have test"));
    }
}
