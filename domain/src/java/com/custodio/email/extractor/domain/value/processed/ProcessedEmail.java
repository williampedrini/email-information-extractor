package com.custodio.email.extractor.domain.value.processed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ProcessedEmail {
    private final String cc;
    private final Collection<ProcessedEmailField> fields;
    private final String from;
    private final String sentDate;
    private final String subject;
    private final String to;

    @JsonCreator
    public ProcessedEmail(@JsonProperty("cc") final String cc,
                          @JsonProperty("fields") final Collection<ProcessedEmailField> fields,
                          @JsonProperty("from") final String from,
                          @JsonProperty("sentDate") final String sentDate,
                          @JsonProperty("subject") final String subject,
                          @JsonProperty("to") final String to) {
        this.cc = cc;
        this.fields = fields;
        this.from = from;
        this.sentDate = sentDate;
        this.subject = subject;
        this.to = to;
    }
}
