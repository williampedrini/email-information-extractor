package com.custodio.email.extractor.usecase.strategy;

import com.custodio.email.extractor.usecase.value.EmailContentField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

import static java.util.Arrays.stream;

public class PDFContentExtractStrategy implements ContentFieldExtractStrategy {

    private static final String BREAK_LINE = "\\n";

    @NotNull
    @Override
    public Optional<String> extract(@NotNull final byte[] pdfContent, @NotNull final EmailContentField emailContentField) {
        try {
            final var file = PDDocument.load(pdfContent);
            final var fileContent = new PDFTextStripper().getText(file);
            return stream(fileContent.split(BREAK_LINE))
                .filter(emailContentField::matches)
                .map(emailContentField::extractContent)
                .findAny();
        } catch (final IOException exception) {
            throw new IllegalStateException("Error while reading PDF file.", exception);
        }
    }
}
