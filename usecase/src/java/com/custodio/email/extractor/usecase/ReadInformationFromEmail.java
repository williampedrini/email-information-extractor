package com.custodio.email.extractor.usecase;

import com.custodio.email.extractor.domain.value.processed.ProcessedEmail;
import com.custodio.email.extractor.domain.value.processed.ProcessedEmailField;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import com.custodio.email.extractor.domain.value.raw.RawEmailAttachment;
import com.custodio.email.extractor.usecase.value.EmailAttachmentContentExtractor;
import com.custodio.email.extractor.usecase.value.EmailBodyContentExtractor;
import com.custodio.email.extractor.usecase.value.EmailContentExtractor;
import com.custodio.email.extractor.usecase.value.EmailContentExtractor.Type;
import com.custodio.email.extractor.usecase.value.EmailFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.custodio.email.extractor.usecase.value.EmailContentExtractor.Type.ATTACHMENT;
import static com.custodio.email.extractor.usecase.value.EmailContentExtractor.Type.BODY;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

@Component
public class ReadInformationFromEmail {

    private static final String BODY_SOURCE_NAME = "BODY";

    private final ReadEmailFromAccount readEmailFromAccount;

    @Autowired
    public ReadInformationFromEmail(@NotNull final ReadEmailFromAccount readEmailFromAccount) {
        this.readEmailFromAccount = requireNonNull(readEmailFromAccount);
    }

    @NotNull
    public Collection<ProcessedEmail> read(@NotNull final UUID emailAccountId, @NotNull final Collection<EmailFilter> emailFilters) {
        return readEmailFromAccount
                .readAll(emailAccountId)
                .stream()
                .filter(email -> emailFilters.stream().anyMatch(filter -> filter.matches(email)))
                .map(email -> this.toProcessedEmailFields(email, emailFilters))
                .collect(toUnmodifiableList());
    }

    /**
     * Converts the current e-mail content into a {@link ProcessedEmailField} based on a collection of {@link EmailFilter}.
     *
     * @param filters The provided filters to be used to extract the information from the e-mail.
     * @return The built collection of {@link ProcessedEmailField} with all extracted data.
     */
    private ProcessedEmail toProcessedEmailFields(final RawEmail email, final Collection<EmailFilter> filters) {
        final var extractors = getExtractors(email, filters);
        final var attachments = extractContentFromAttachments(email, extractors);
        final var body = extractContentFromBody(email, extractors);
        final var contents = of(attachments, body)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
        return ProcessedEmail.builder()
                .cc(email.getCc())
                .from(email.getFrom())
                .fields(contents)
                .sentDate(email.getSentDate())
                .subject(email.getSubject())
                .to(email.getTo())
                .build();
    }

    /**
     * Extract all contents from a {@link RawEmail} body that matches the expressions defined by a {@link EmailContentExtractor}.
     *
     * @param extractors The extractors with the expressions used to extract content from the body.
     * @return All extracted content from the e-mails.
     */
    private Collection<ProcessedEmailField> extractContentFromBody(final RawEmail email, final Map<Type, List<EmailContentExtractor>> extractors) {
        return extractors.getOrDefault(BODY, emptyList())
                .stream()
                .map(extractor -> (EmailBodyContentExtractor) extractor)
                .map(extractor -> {
                    final var processedEmailFieldBuilder = ProcessedEmailField.builder();
                    extractor.extract(email.getContent())
                            .ifPresent(processedEmailFieldBuilder::content);
                    final var expression = extractor.getField()
                            .getContentExpression()
                            .pattern();
                    processedEmailFieldBuilder.expression(expression);
                    return processedEmailFieldBuilder
                            .contentType(email.getContentType())
                            .sourceName(BODY_SOURCE_NAME)
                            .build();
                })
                .collect(toUnmodifiableList());
    }

    /**
     * Extract all contents from a {@link RawEmailAttachment} that matches the expressions defined by a {@link EmailContentExtractor}.
     *
     * @param extractors The extractors with the expressions used to extract content from the attachments.
     * @return All extracted content from the e-mail's attachments.
     */
    private Collection<ProcessedEmailField> extractContentFromAttachments(final RawEmail email, final Map<Type, List<EmailContentExtractor>> extractors) {
        final var attachmentContentExtractors = extractors.getOrDefault(ATTACHMENT, emptyList())
                .stream()
                .map(extractor -> (EmailAttachmentContentExtractor) extractor)
                .collect(toUnmodifiableList());
        return email.getAttachments()
                .stream()
                .filter(attachment -> attachmentContentExtractors
                        .stream()
                        .anyMatch(extractor -> extractor.matches(attachment)))
                .map(attachment -> attachmentContentExtractors
                        .stream()
                        .filter(extractor -> extractor.matches(attachment))
                        .findAny()
                        .map(extractor -> extractor.extract(attachment))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    /**
     * Get all the extractors defined by a {@link EmailFilter} that matches the current email.
     *
     * @param emailFilters The provided filters to be used as base.
     * @return All the extractors by {@link Type}.
     */
    private Map<Type, List<EmailContentExtractor>> getExtractors(final RawEmail email, final Collection<EmailFilter> emailFilters) {
        return emailFilters.stream()
                .filter(filter -> filter.matches(email))
                .map(EmailFilter::getExtractors)
                .findAny()
                .orElse(emptyList())
                .stream()
                .collect(groupingBy(EmailContentExtractor::getType));
    }
}
