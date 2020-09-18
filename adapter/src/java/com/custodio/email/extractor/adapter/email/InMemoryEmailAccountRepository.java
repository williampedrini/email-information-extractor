package com.custodio.email.extractor.adapter.email;

import com.custodio.email.extractor.domain.configuration.ApplicationProperties;
import com.custodio.email.extractor.domain.entity.EmailAccount;
import com.custodio.email.extractor.domain.port.EmailAccountRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.custodio.email.extractor.adapter.util.JSONUtil.fileToBean;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Component
@Profile("in-memory")
class InMemoryEmailAccountRepository implements EmailAccountRepository {

    private final Map<UUID, EmailAccount> models;

    @Autowired
    public InMemoryEmailAccountRepository(@NotNull final ApplicationProperties applicationProperties) {
        requireNonNull(applicationProperties);
        this.models = new HashMap<>();
        fileToBean(applicationProperties.getEmailAccountDatasourceFile(), new TypeReference<Collection<EmailAccount>>() {
        }).forEach(model -> models.put(model.getId(), model));
    }

    @NotNull
    @Override
    public Optional<EmailAccount> findById(@NotNull final UUID id) {
        return ofNullable(this.models.get(id));
    }
}
