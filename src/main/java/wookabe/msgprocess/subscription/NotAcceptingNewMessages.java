package wookabe.msgprocess.subscription;

/**
 * Proprietary exception thrown when no new messages are accepted.
 * Alternatively the messages could be ignored (silent mode).
 */
public class NotAcceptingNewMessages extends Exception {
    /**
     * Constructor with limit of messages that triggered the exception.
     *
     * @param limit limit of messages reached
     */
    public NotAcceptingNewMessages(int limit) {
        super("Not accepting new messages, limit of " + limit + " reached");
    }
}
