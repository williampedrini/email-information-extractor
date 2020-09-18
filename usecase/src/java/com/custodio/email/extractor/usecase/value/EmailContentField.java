package com.custodio.email.extractor.usecase.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;

@Getter
public class EmailContentField {
    private static final String EMPTY = "";

    /**
     * Alias used to identify the group from the {@code EmailContentField#contentExpression} responsible for identifying the value to be extracted.
     */
    private static final String VALUE_FIELD = "value";

    private final Pattern contentExpression;
    private final String type;

    @JsonCreator
    public EmailContentField(@NotNull @JsonProperty("contentExpression") final String contentExpression,
                             @JsonProperty("type") final String type) {
        requireNonNull(contentExpression);
        this.contentExpression = compile(contentExpression);
        this.type = type;
    }

    /**
     * Extracts the content from a certain string based on the current regular expression.
     * @param content The raw content.
     * @return The extracted content.
     */
    @NotNull
    public String extractContent(@NotNull final String content) {
        return Optional.of(content)
                .map(contentExpression::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(VALUE_FIELD))
                .orElse(EMPTY);
    }

    /**
     * Verifies whether a content matches the current expression or not.
     * @param content The content to be validated.
     * @return {@code true}: The content matches the current expression. <br/>
     *         {@code false}: The content does not matche the current expression.
     */
    public boolean matches(@NotNull final String content) {
        return contentExpression.matcher(content)
                .matches();
    }
}
