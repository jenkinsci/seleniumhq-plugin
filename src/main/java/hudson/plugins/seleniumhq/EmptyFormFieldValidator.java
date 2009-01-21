package hudson.plugins.seleniumhq;

import java.io.IOException;

import javax.servlet.ServletException;

import hudson.security.Permission;
import hudson.util.FormFieldValidator;

/**
 * 
 * @author Pascal Martin
 *
 */
public class EmptyFormFieldValidator extends FormFieldValidator{

	private String errorMessage = "";
	private String value = "";
	
	public EmptyFormFieldValidator(String value, String errorMessage, Permission permission)
	{
		super(permission);
		this.errorMessage = errorMessage;
		this.value = value;
	}
		
	@Override
	protected void check() throws IOException, ServletException {
		
		if(this.value.length()==0)
		{
            error(this.errorMessage);
		}
		else
		{
			ok();
		}
	}

}
