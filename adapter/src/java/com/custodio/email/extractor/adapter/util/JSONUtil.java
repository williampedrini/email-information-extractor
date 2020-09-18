package com.custodio.email.extractor.adapter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static java.nio.file.Paths.get;
import static java.util.Optional.of;
import static lombok.AccessLevel.PRIVATE;

/**
 * Class responsible for manipulating the data related to a JSON object.
 */
@NoArgsConstructor(access = PRIVATE)
public final class JSONUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(NON_EMPTY);
        MAPPER.setVisibility(MAPPER.getVisibilityChecker().withFieldVisibility(ANY));
    }

    /**
     * Read a certain JSON file and map its value to a certain bean type.
     *
     * @param path The of the file to be read.
     * @param type The {@link JavaType} used for the conversion.
     * @return The representation of the file content as a bean.
     */
    @NotNull
    public static <T> T fileToBean(@NotNull final String path, @NotNull final Class<T> type) {
        try (final var file = JSONUtil.class.getResourceAsStream(path)) {
            return MAPPER.readValue(file, defaultInstance().constructType(type));
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Read a certain JSON file and map its value to a certain bean type.
     *
     * @param path The of the file to be read.
     * @param type The {@link TypeReference} used for the conversion.
     * @return The representation of the file content as a bean.
     */
    @NotNull
    public static <T> T fileToBean(@NotNull final String path, @NotNull final TypeReference<T> type) {
        try (final var file = new FileInputStream(getAbsolutePath(path))) {
            return MAPPER.readValue(file, defaultInstance().constructType(type));
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String getAbsolutePath(final String path) {
        return of(get(path))
                .filter(Files::exists)
                .map(Path::toString)
                .orElseGet(() -> JSONUtil.class.getResource(path).getPath());
    }
}
