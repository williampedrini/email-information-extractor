package com.custodio.email.extractor.domain.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Class used as helper to retrieve the properties from a certain property file.
 *
 * @since 1.0.0
 */
public class Properties extends java.util.Properties {

    /**
     * Load a certain {@link Properties} from a map.
     *
     * @param properties The map used to load the properties.
     */
    public Properties(@NotNull final Map<?, ?> properties) {
        requireNonNull(properties);
        this.putAll(properties);
    }

    /**
     * Retrieve a value of a key from the property and cast to a defined type.
     *
     * @param key  The key used to retrieve the information.
     * @param type The class type to be casted.
     * @param <T>  The type of to be converted.
     * @return The converted value from the property.
     */
    @Nullable
    public <T> T get(@NotNull final Object key, @NotNull final Class<T> type) {
        return type.cast(super.get(key));
    }
}
