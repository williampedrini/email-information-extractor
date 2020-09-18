package com.custodio.email.extractor.usecase.value;

import com.custodio.email.extractor.domain.value.raw.RawEmail;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNullElse;
import static java.util.regex.Pattern.compile;

@Getter
public class EmailFilter {
    private static final String ALLOW_ALL_PATTERN = ".*";

    private final Collection<EmailContentExtractor> extractors;
    private final Pattern fromExpression;
    private final UUID id;
    private final String name;
    private final Pattern subjectExpression;
    private final Pattern toExpression;

    @JsonCreator
    public EmailFilter(@JsonProperty("extractors") final Collection<EmailContentExtractor> extractors,
                       @JsonProperty("fromExpression") final String fromExpression,
                       @JsonProperty("id") final UUID id,
                       @JsonProperty("name") final String name,
                       @JsonProperty("subjectExpression") final String subjectExpression,
                       @JsonProperty("toExpression") final String toExpression) {
        this.extractors = extractors;
        this.fromExpression = compile(requireNonNullElse(fromExpression, ALLOW_ALL_PATTERN));
        this.id = id;
        this.name = name;
        this.subjectExpression = compile(requireNonNullElse(subjectExpression, ALLOW_ALL_PATTERN));
        this.toExpression = compile(requireNonNullElse(toExpression, ALLOW_ALL_PATTERN));
    }

    /**
     * Verifies whether an {@link RawEmail} matches the current filter or not.
     *
     * @param email The e-mail with the content to be verified.
     * @return {@code true}: The e-mail content matches the filter. <br/>
     * {@code false}: The e-mail content does matches the filter.
     */
    public boolean matches(@NotNull final RawEmail email) {
        final var matchesFromExpression = fromExpression.matcher(email.getFrom()).matches();
        final var matchesToExpression = toExpression.matcher(email.getTo()).matches();
        final var matchesSubjectExpression = subjectExpression.matcher(email.getSubject()).matches();
        return matchesFromExpression && matchesToExpression && matchesSubjectExpression;
    }
}
