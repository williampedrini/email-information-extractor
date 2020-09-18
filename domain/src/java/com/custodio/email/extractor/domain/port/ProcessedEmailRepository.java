package com.custodio.email.extractor.domain.port;

import com.custodio.email.extractor.domain.value.processed.ProcessedEmail;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of a port responsible for handling operations associated to e-mails already processed.
 *
 * @since 1.0.0
 */
public interface ProcessedEmailRepository {

    /**
     * Persists a certain processed e-mail.
     * @param processedEmail The object containing the data to be processed.
     */
    void save(@NotNull ProcessedEmail processedEmail);
}
