package com.custodio.email.extractor.domain.value.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNullElse;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static javax.mail.Part.ATTACHMENT;
import static javax.mail.Part.INLINE;
import static org.apache.commons.io.IOUtils.toByteArray;

@Getter
@EqualsAndHashCode
public class RawEmail {
    private static final String ADDRESS_INITIAL_PART = "<";
    private static final String ADDRESS_ENDING_PART = ">";
    private static final String EMAIL_DELIMITER = ", ";
    private static final String EMPTY = "";

    private final Collection<RawEmailAttachment> attachments;
    private final String cc;
    private final byte[] content;
    private final String contentType;
    private final String from;
    private final String sentDate;
    private final String subject;
    private final String to;

    @JsonCreator
    public RawEmail(@JsonProperty("attachments") final Collection<RawEmailAttachment> attachments,
                    @JsonProperty("cc") final String cc,
                    @JsonProperty("content") final byte[] content,
                    @JsonProperty("contentType") final String contentType,
                    @JsonProperty("from") final String from,
                    @JsonProperty("sentDate") final String sentDate,
                    @JsonProperty("subject") final String subject,
                    @JsonProperty("to") final String to) {
        this.attachments = attachments;
        this.cc = cc;
        this.content = content;
        this.contentType = contentType;
        this.from = from;
        this.sentDate = sentDate;
        this.subject = subject;
        this.to = to;
    }

    public RawEmail(@NotNull final Message message) {
        try {
            attachments = parseAttachments(message.getContent());
            cc = parseAddresses(message.getRecipients(CC));
            content = parseContent(message.getContent());
            contentType = requireNonNullElse(message.getContentType(), EMPTY);
            from = parseAddresses(message.getFrom());
            sentDate = ofNullable(message.getSentDate())
                    .map(Date::toString)
                    .orElse(EMPTY);
            subject = requireNonNullElse(message.getSubject(), EMPTY);
            to = parseAddresses(message.getRecipients(TO));
        } catch (final MessagingException | IOException exception) {
            throw new IllegalStateException("Error while creating the email content.");
        }
    }

    /**
     * Parse the current content depending on the content type.
     *
     * @param content The content to be converted into byte array.
     * @return The parsed content presented as byte array.
     * @throws MessagingException Error while reading the message.
     * @throws IOException        Error while reading the body.
     */
    private byte[] parseContent(final Object content) throws IOException, MessagingException {
        if (content instanceof Multipart) {
            final var multipart = (Multipart) content;
            return parseContent(multipart);
        }
        return content.toString()
                .getBytes();
    }

    /**
     * Retrieve the current body from the message.
     *
     * @param multiPart The message parts to get the content from.
     * @return The content or empty if not found.
     * @throws MessagingException Error while reading the message.
     * @throws IOException        Error while reading the body.
     */
    private byte[] parseContent(final Multipart multiPart) throws MessagingException, IOException {
        for (int index = 0; index < multiPart.getCount(); index++) {
            final var part = (MimeBodyPart) multiPart.getBodyPart(index);
            if (INLINE.equalsIgnoreCase(part.getDisposition())) {
                return toByteArray(part.getInputStream());
            }
        }
        return null;
    }

    /**
     * Retrieve all attachments from the current e-mail.
     *
     * @param content The message content to get the attachments from.
     * @return A list containing all the attachments.
     * @throws MessagingException Error while reading the message.
     */
    private Collection<RawEmailAttachment> parseAttachments(final Object content) throws MessagingException {
        if (content instanceof Multipart) {
            final var multipart = (Multipart) content;
            return parseAttachments(multipart);
        }
        return emptyList();
    }

    /**
     * Retrieve all attachments from the current e-mail.
     *
     * @param multiPart The message parts to get the attachments from.
     * @return A list containing all the attachments.
     * @throws MessagingException Error while reading the message.
     */
    private Collection<RawEmailAttachment> parseAttachments(final Multipart multiPart) throws MessagingException {
        final var attachments = new ArrayList<RawEmailAttachment>();
        for (int index = 0; index < multiPart.getCount(); index++) {
            final var part = (MimeBodyPart) multiPart.getBodyPart(index);
            if (ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                attachments.add(new RawEmailAttachment(part));
            }
        }
        return attachments;
    }

    /**
     * Parse a list of {@link Address} into a string joined by ','.
     *
     * @param addresses The list of addresses.
     * @return The string containing the emails.
     */
    private String parseAddresses(final Address[] addresses) {
        final var addressesToParse = ofNullable(addresses)
                .orElse(new Address[]{});
        return stream(addressesToParse)
                .map(this::parseAddress)
                .collect(joining(EMAIL_DELIMITER));
    }

    /**
     * Parse a certain address to its string format without special character.
     *
     * @param address The address to be converted to string.
     * @return The formatted address.
     */
    private String parseAddress(final Address address) {
        final var addressAsString = address.toString();
        if (addressAsString.contains(ADDRESS_INITIAL_PART)) {
            final var initialPart = addressAsString.indexOf(ADDRESS_INITIAL_PART);
            final var endingPart = addressAsString.indexOf(ADDRESS_ENDING_PART);
            return addressAsString.substring(initialPart + 1, endingPart);
        }
        return addressAsString;
    }
}
