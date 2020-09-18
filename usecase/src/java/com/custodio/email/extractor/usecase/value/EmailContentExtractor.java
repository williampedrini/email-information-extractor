package com.custodio.email.extractor.usecase.value;

import com.custodio.email.extractor.domain.value.processed.ProcessedEmailField;
import com.custodio.email.extractor.domain.value.raw.RawEmailAttachment;
import com.custodio.email.extractor.usecase.strategy.ContentFieldExtractStrategy;
import com.custodio.email.extractor.usecase.strategy.PDFContentExtractStrategy;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type", include = EXTERNAL_PROPERTY)
@JsonSubTypes(value = {
        @Type(value = EmailBodyContentExtractor.class, name = "BODY"),
        @Type(value = EmailAttachmentContentExtractor.class, name = "ATTACHMENT")
})
@Getter
@RequiredArgsConstructor
public class EmailContentExtractor {
    private final ContentFormat contentFormat;
    private final EmailContentField field;
    private final Type type;

    /**
     * Converts the current attachment into a {@link ProcessedEmailField} based on a {@link EmailContentExtractor}.
     *
     * @param attachment The attachment used to extract the information.
     * @return The built {@link ProcessedEmailField} with the extracted data.
     */
    @NotNull
    public ProcessedEmailField extract(@NotNull final RawEmailAttachment attachment) {
        final var processedEmailFieldBuilder = ProcessedEmailField.builder();
        this.extract(attachment.getContent())
                .ifPresent(processedEmailFieldBuilder::content);
        final var expression = this.getField()
                .getContentExpression()
                .pattern();
        processedEmailFieldBuilder.expression(expression);
        return processedEmailFieldBuilder
                .contentType(attachment.getContentType())
                .sourceName(attachment.getFileName())
                .build();
    }

    /**
     * Extract the value from a certain content.
     *
     * @param content The content with all the information to be filtered.
     * @return The extracted information.
     */
    @NotNull
    public Optional<String> extract(@NotNull final byte[] content) {
        return this.contentFormat.extract(content, field);
    }

    @RequiredArgsConstructor
    public enum ContentFormat {
        PDF("APPLICATION/PDF", new PDFContentExtractStrategy());

        @Getter
        private final String value;
        private final ContentFieldExtractStrategy strategy;

        /**
         * Extract the fields values from a certain content.
         *
         * @param content The content with all the information to be filtered.
         * @return The extracted information.
         */
        @NotNull
        private Optional<String> extract(@NotNull final byte[] content, @NotNull final EmailContentField field) {
            return strategy.extract(content, field);
        }
    }

    public enum Type {
        BODY, ATTACHMENT
    }
}
