package com.custodio.email.extractor.domain.value.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.toByteArray;

@Getter
public class RawEmailAttachment {
    private final byte[] content;
    private final String contentType;
    private final String fileName;

    @JsonCreator
    public RawEmailAttachment(@JsonProperty("content") final byte[] content,
                              @JsonProperty("contentType") final String contentType,
                              @JsonProperty("fileName") final String fileName) {
        this.content = content;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public RawEmailAttachment(@NotNull final MimeBodyPart part) {
        try {
            this.content = toByteArray(part.getInputStream());
            this.contentType = part.getContentType();
            this.fileName = part.getFileName();
        } catch (final MessagingException | IOException exception) {
            final var errorMessage = format("Error while reading the attachment %s.", part);
            throw new IllegalStateException(errorMessage);
        }
    }
}
