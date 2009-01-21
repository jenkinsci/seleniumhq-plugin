package hudson.plugins.seleniumhq;

/**
 * 
 * @author Pascal Martin
 *
 */
class AbortException extends RuntimeException {
    public AbortException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
