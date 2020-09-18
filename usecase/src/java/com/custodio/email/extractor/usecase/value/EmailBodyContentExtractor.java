package com.custodio.email.extractor.usecase.value;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.custodio.email.extractor.usecase.value.EmailContentExtractor.Type.BODY;

public class EmailBodyContentExtractor extends EmailContentExtractor {

    public EmailBodyContentExtractor(@JsonProperty("field") final EmailContentField field,
                                     @JsonProperty("contentFormat") final ContentFormat contentFormat) {
        super(contentFormat, field, BODY);
    }
}
