package com.custodio.email.extractor.usecase.strategy;

import com.custodio.email.extractor.usecase.value.EmailContentField;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;

import java.io.IOException;

import static com.custodio.email.extractor.util.JSONUtil.fileToBean;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PDFContentExtractStrategyTest {

    private static final String TEST_CASES_BASE_PATH = "/test-case/use-case/strategy/pdf-content-extract/%s";

    @Rule
    public final MethodRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private PDFContentExtractStrategy underTest;

    @Test
    public void testWhenThereAreDefinedFiltersAndValidPDF_shouldExtractRightFields() throws IOException {
        //given
        final var inputFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-defined-filters-and-valid-pdf/input.pdf");
        final var input = this.getClass().getResourceAsStream(inputFullPath);

        final var emailContentFieldFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-defined-filters-and-valid-pdf/filter.json");
        final var emailContentField = fileToBean(emailContentFieldFullPath, EmailContentField.class);

        //when
        final var actual = underTest.extract(input.readAllBytes(), emailContentField);

        //then
        assertTrue(actual.isPresent());
        assertEquals("63,02", actual.get());
    }
}
