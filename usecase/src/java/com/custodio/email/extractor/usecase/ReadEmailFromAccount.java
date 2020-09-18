package com.custodio.email.extractor.usecase;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import com.custodio.email.extractor.domain.port.EmailAccountRepository;
import com.custodio.email.extractor.domain.port.RawEmailRepository;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import com.custodio.email.extractor.usecase.value.EmailFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Use case responsible for reading e-mails.
 *
 * @since 1.0.0
 */
@Component
public class ReadEmailFromAccount {

    private final EmailAccountRepository emailAccountRepository;
    private final RawEmailRepository emailRepository;

    @Autowired
    public ReadEmailFromAccount(@NotNull final EmailAccountRepository emailAccountRepository,
                                @NotNull final RawEmailRepository emailRepository) {
        this.emailRepository = requireNonNull(emailRepository);
        this.emailAccountRepository = requireNonNull(emailAccountRepository);
    }

    /**
     * Read all e-mails from a certain account based on the {@link EmailFilter} defined by the user.
     *
     * @param emailAccountId The {@link EmailAccount} identifier.
     * @return All read e-mails from the account's folders.
     */
    @NotNull
    public Collection<RawEmail> readAll(@NotNull final UUID emailAccountId) {
        final var emailAccount = emailAccountRepository.findById(emailAccountId)
                .orElseThrow(() -> {
                    final var errorMessage = format("There is not any e-mail account for the id %s.", emailAccountId);
                    return new IllegalArgumentException(errorMessage);
                });
        return emailAccount.getFolders()
                .stream()
                .map(folder -> this.emailRepository.readAllByAccountAndFolder(emailAccount, folder))
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }
}
