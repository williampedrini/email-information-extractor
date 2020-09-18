package com.custodio.email.extractor.domain.port;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Representation of a port responsible for handling operations associated to e-mails.
 *
 * @since 1.0.0
 */
public interface RawEmailRepository {

    /**
     * Read all e-mails for a specific user and filter according to the defined filters.
     * @param emailAccount The e-mail account information.
     * @param folder The folder where the e-mails will be present.
     * @return All read e-mails from the server.
     */
    @NotNull
    Collection<RawEmail> readAllByAccountAndFolder(@NotNull EmailAccount emailAccount, @NotNull String folder);
}
