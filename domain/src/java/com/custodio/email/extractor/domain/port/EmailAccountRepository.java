package com.custodio.email.extractor.domain.port;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface EmailAccountRepository {

    /**
     * Searches for all the existing accounts by a certain id.
     * @param id The account identifier.
     * @return The found account.
     */
    @NotNull
    Optional<EmailAccount> findById(@NotNull UUID id);
}
