package com.custodio.email.extractor.adapter.email.value;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import org.jetbrains.annotations.NotNull;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static javax.mail.Folder.READ_ONLY;

/**
 * Represents a certain connection information used to perform requests against the e-mail server.
 *
 * @since 1.0.0
 */
public class EmailConnection implements AutoCloseable {

    private final Store store;
    private final EmailFolder folder;

    /**
     * Initiate a connection based on an {@link EmailAccount} and folder name.
     *
     * @param emailAccount The information of the url and credentials used to connect to the account.
     * @param folderName   The name of the folder used to retrieve the messages.
     */
    public EmailConnection(@NotNull final EmailAccount emailAccount, @NotNull final String folderName) {
        try {
            requireNonNull(emailAccount);
            requireNonNull(folderName);
            final var session = Session.getDefaultInstance(emailAccount.toProperty());
            store = session.getStore(emailAccount.getProtocol());
            store.connect(emailAccount.getUsername(), emailAccount.getPassword());
            folder = new EmailFolder(store.getFolder(folderName), READ_ONLY);
        } catch (final MessagingException exception) {
            throw new IllegalArgumentException("Error while creating the connection.");
        }
    }

    @Override
    public void close() {
        try {
            folder.close();
            store.close();
        } catch (final MessagingException exception) {
            throw new IllegalStateException("Error while closing the e-mail connection.");
        }
    }

    /**
     * Get all the messages from the current folder.
     *
     * @return A collection of messages.
     * @throws MessagingException Error while retrieving the messages from the server.
     */
    @NotNull
    public Collection<Message> getMessages() throws MessagingException {
        return asList(folder.getMessages());
    }
}
