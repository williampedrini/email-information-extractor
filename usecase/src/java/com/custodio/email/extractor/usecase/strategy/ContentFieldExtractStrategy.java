package com.custodio.email.extractor.usecase.strategy;

import com.custodio.email.extractor.usecase.value.EmailContentField;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents an strategy to be used to extract information from a certain byte array.
 *
 * @since 1.0.0
 */
public interface ContentFieldExtractStrategy {

    /**
     * Extract the fields values from a certain content.
     * @param content The content with all the information to be filtered.
     * @return The extracted information.
     */
    @NotNull
    Optional<String> extract(@NotNull byte[] content, @NotNull final EmailContentField field);
}
