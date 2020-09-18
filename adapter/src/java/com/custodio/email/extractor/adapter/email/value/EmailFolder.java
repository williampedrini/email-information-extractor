package com.custodio.email.extractor.adapter.email.value;

import org.jetbrains.annotations.NotNull;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import static java.util.Objects.requireNonNull;

class EmailFolder implements AutoCloseable {

    private final Folder folder;

    EmailFolder(@NotNull final Folder folder, final int mode) throws MessagingException {
        this.folder = requireNonNull(folder);
        this.folder.open(mode);
    }

    @Override
    public void close() throws MessagingException {
        this.folder.close(false);
    }

    @NotNull
    public Message[] getMessages() throws MessagingException {
        return this.folder.getMessages();
    }
}
