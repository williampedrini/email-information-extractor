package com.custodio.email.extractor.adapter.email;

import com.custodio.email.extractor.adapter.email.value.EmailConnection;
import com.custodio.email.extractor.domain.entity.EmailAccount;
import com.custodio.email.extractor.domain.port.RawEmailRepository;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Collection;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Repository responsible for communicating with an e-mail server using any protocol.
 *
 * @since 1.0.0
 */
@Component
class DefaultRawEmailRepository implements RawEmailRepository {

    @NotNull
    @Override
    public Collection<RawEmail> readAllByAccountAndFolder(@NotNull final EmailAccount emailAccount, @NotNull final String folderName) {
        try (final var connection = new EmailConnection(emailAccount, folderName)) {
            return connection.getMessages()
                    .stream()
                    .map(RawEmail::new)
                    .collect(toUnmodifiableList());
        } catch (final MessagingException exception) {
            throw new IllegalStateException("Error while connecting to the server.");
        }
    }
}
