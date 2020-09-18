package com.custodio.email.extractor.domain.entity;

import com.custodio.email.extractor.domain.configuration.Properties;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Getter
public class EmailAccount {
    private final static String HOST_PROPERTY = "mail.%s.host";
    private final static String PORT_PROPERTY = "mail.%s.port";
    private final static String SOCKET_FACTORY_CLASS_PROPERTY = "mail.%s.socketFactory.class";
    private final static String SOCKET_FACTORY_FALLBACK_PROPERTY = "mail.%s.socketFactory.fallback";
    private final static String SOCKET_FACTORY_PORT_PROPERTY = "mail.%s.socketFactory.port";

    private final Collection<String> folders;
    private final String host;
    private final UUID id;
    private final User owner;
    private final String password;
    private final String port;
    private final String protocol;
    private final String username;

    @JsonCreator
    public EmailAccount(@JsonProperty("folders") final Collection<String> folders,
                        @JsonProperty("host") final String host,
                        @JsonProperty("id") final UUID id,
                        @JsonProperty("owner") final User owner,
                        @JsonProperty("password") final String password,
                        @JsonProperty("port") final String port,
                        @JsonProperty("protocol") final String protocol,
                        @JsonProperty("username") final String username) {
        this.folders = ofNullable(folders).orElse(emptyList());
        this.host = host;
        this.id = id;
        this.owner = owner;
        this.password = password;
        this.port = port;
        this.protocol = protocol;
        this.username = username;
    }

    /**
     * Converts the current connection configuration into {@link Properties}.
     *
     * @return The object containing the property information.
     */
    @NotNull
    public Properties toProperty() {
        final var properties = Map.of(
                format(HOST_PROPERTY, this.getProtocol()), this.getHost(),
                format(PORT_PROPERTY, this.getProtocol()), this.getPort(),
                format(SOCKET_FACTORY_CLASS_PROPERTY, this.getProtocol()), "javax.net.ssl.SSLSocketFactory",
                format(SOCKET_FACTORY_FALLBACK_PROPERTY, this.getProtocol()), "false",
                format(SOCKET_FACTORY_PORT_PROPERTY, this.getProtocol()), this.getPort()
        );
        return new Properties(properties);
    }
}
