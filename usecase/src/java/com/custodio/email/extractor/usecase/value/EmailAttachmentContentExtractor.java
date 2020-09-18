package com.custodio.email.extractor.usecase.value;

import com.custodio.email.extractor.domain.value.raw.RawEmailAttachment;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.custodio.email.extractor.usecase.value.EmailContentExtractor.Type.ATTACHMENT;
import static java.util.Objects.requireNonNullElse;
import static java.util.regex.Pattern.compile;

public class EmailAttachmentContentExtractor extends EmailContentExtractor {
    private static final String ALLOW_ALL_PATTERN = ".*";
    private final Pattern nameExpression;

    public EmailAttachmentContentExtractor(@JsonProperty("format") final ContentFormat contentFormat,
                                           @JsonProperty("field") final EmailContentField field,
                                           @JsonProperty("nameExpression") final String nameExpression) {
        super(contentFormat, field, ATTACHMENT);
        this.nameExpression = compile(requireNonNullElse(nameExpression, ALLOW_ALL_PATTERN));
    }

    /**
     * Verifies whether a {@link RawEmailAttachment} can have its content extracted by the current extractor.
     *
     * @param attachment The attachment to be used for the extraction.
     * @return {@code true}: Can have information extracted. <br/>
     * {@code false}: Cannot have information extracted.
     */
    public boolean matches(@NotNull final RawEmailAttachment attachment) {
        final var allowedContentType = getContentFormat()
                .getValue()
                .toUpperCase();
        final var matchesContentType = attachment.getContentType()
                .toUpperCase()
                .contains(allowedContentType);
        final var matchesFileName = nameExpression
                .matcher(attachment.getFileName())
                .matches();
        return matchesContentType && matchesFileName;
    }
}
