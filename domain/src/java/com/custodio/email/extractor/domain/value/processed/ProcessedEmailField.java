package com.custodio.email.extractor.domain.value.processed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ProcessedEmailField {
    private final String content;
    private final String contentType;
    private final String expression;
    private final String sourceName;

    @JsonCreator
    public ProcessedEmailField(@JsonProperty("content") final String content,
                               @JsonProperty("contentType") final String contentType,
                               @JsonProperty("expression") final String expression,
                               @JsonProperty("sourceName") final String sourceName) {
        this.content = content;
        this.contentType = contentType;
        this.expression = expression;
        this.sourceName = sourceName;
    }
}
