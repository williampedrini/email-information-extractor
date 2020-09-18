package com.custodio.email.extractor.adapter.email;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;

import static com.custodio.email.extractor.adapter.util.JSONUtil.fileToBean;
import static java.lang.String.format;
import static org.junit.Assert.assertFalse;

public class DefaultRawRawEmailRepositoryTest {

    private static final String INBOX_FOLDER = "INBOX";
    private static final String TEST_CASES_BASE_PATH = "/test-case/adapter/default-email-repository/%s";

    @Rule
    public final MethodRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private DefaultRawEmailRepository underTest;

    @Test
    public void whenThereAreEmailsToReaderFromInbox_shouldReturnAllEmails() {
        //given
        final var inputFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-email-from-inbox/configuration.json");
        final var input = fileToBean(inputFullPath, EmailAccount.class);
        //when
        final var actual = underTest.readAllByAccountAndFolder(input, INBOX_FOLDER);
        //then
        assertFalse(actual.isEmpty());
    }
}
