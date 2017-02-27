package wookabe.msgprocess.subscription;

/**
 * Interface for receiving messages.
 * To be used by external message providers.
 */
public interface Receivable {
    /**
     * Reception of a message.
     *
     * @param msg message to process
     * @throws NotAcceptingNewMessages thrown if no new message is accepted
     */
    void receive(Message msg) throws NotAcceptingNewMessages;
}
