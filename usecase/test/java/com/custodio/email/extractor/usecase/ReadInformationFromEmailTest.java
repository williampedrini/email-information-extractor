package com.custodio.email.extractor.usecase;

import com.custodio.email.extractor.domain.value.processed.ProcessedEmail;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import com.custodio.email.extractor.usecase.value.EmailFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collection;

import static com.custodio.email.extractor.util.JSONUtil.fileToBean;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        ReadInformationFromEmailTest.TestContextConfiguration.class,
        ReadInformationFromEmail.class
})
public class ReadInformationFromEmailTest {

    private static final String TEST_CASES_BASE_PATH = "/test-case/use-case/read-email-information/%s";

    @Autowired
    private ReadEmailFromAccount mockedReadEmailFromAccount;

    @Autowired
    private ReadInformationFromEmail underTest;

    @Test
    public void testWhenThereAreEmailsToReadForExistingEmailAccount_shouldReturnAllReadEmails() throws IOException {
        //given
        final var id = randomUUID();

        final var emailsFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-to-read/emails.json");
        final var email = fileToBean(emailsFullPath, RawEmail.class);

        final var emailContentFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-to-read/emailContent.pdf");
        try (final var emailContent = this.getClass().getResourceAsStream(emailContentFullPath)) {
            for (final var attachment : email.getAttachments()) {
                setField(attachment, "content", emailContent.readAllBytes());
            }
        }
        when(mockedReadEmailFromAccount.readAll(id)).thenReturn(singletonList(email));

        //when
        final var filtersFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-to-read/filters.json");
        final var filters = fileToBean(filtersFullPath, new TypeReference<Collection<EmailFilter>>() {});
        final var actual = underTest.read(id, filters);

        //then
        final var expectedFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-to-read/expected.json");
        final var expected = fileToBean(expectedFullPath, new TypeReference<Collection<ProcessedEmail>>() {});
        assertEquals(expected, actual);
    }

    @Configuration
    static class TestContextConfiguration {
        @Bean
        ReadEmailFromAccount mockedReadEmailFromAccount() {
            return mock(ReadEmailFromAccount.class);
        }
    }
}
