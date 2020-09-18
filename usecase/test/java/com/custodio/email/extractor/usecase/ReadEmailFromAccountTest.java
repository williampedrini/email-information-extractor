package com.custodio.email.extractor.usecase;

import com.custodio.email.extractor.domain.entity.EmailAccount;
import com.custodio.email.extractor.domain.port.EmailAccountRepository;
import com.custodio.email.extractor.domain.port.RawEmailRepository;
import com.custodio.email.extractor.domain.value.raw.RawEmail;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.custodio.email.extractor.util.JSONUtil.fileToBean;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        ReadEmailFromAccount.class,
        ReadEmailFromAccountTest.TestContextConfiguration.class
})
public class ReadEmailFromAccountTest {

    private static final String TEST_CASES_BASE_PATH = "/test-case/use-case/read-email-from-account/%s";

    @Autowired
    private RawEmailRepository mockedRawEmailRepository;

    @Autowired
    private EmailAccountRepository mockedEmailAccountRepository;

    @Autowired
    private ReadEmailFromAccount underTest;

    @Test
    public void testWhenThereAreEmailsToReadForExistingEmailAccount_shouldReturnAllReadEmails() {
        //given
        final var accountFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-for-existing-account/account.json");
        final var account = fileToBean(accountFullPath, EmailAccount.class);
        when(mockedEmailAccountRepository.findById(account.getId())).thenReturn(of(account));

        final var emailsFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-for-existing-account/emails.json");
        final var emails = fileToBean(emailsFullPath, new TypeReference<Collection<RawEmail>>() {});
        when(mockedRawEmailRepository.readAllByAccountAndFolder(any(EmailAccount.class), anyString())).thenReturn(emails);

        //when
        final var actual = underTest.readAll(account.getId());

        //then
        final var expectedFullPath = format(TEST_CASES_BASE_PATH, "when-there-are-emails-for-existing-account/expected.json");
        final var expected = fileToBean(expectedFullPath, new TypeReference<Collection<RawEmail>>() {});
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhenReadForNotExistingEmailAccount_shouldThrowException() {
        //given
        final var emailAccountId = randomUUID();
        when(mockedEmailAccountRepository.findById(emailAccountId)).thenReturn(empty());
        //when
        underTest.readAll(emailAccountId);
    }

    @ContextConfiguration
    static class TestContextConfiguration {
        @Bean
        EmailAccountRepository mockedEmailAccountRepository() {
            return mock(EmailAccountRepository.class);
        }
        @Bean
        RawEmailRepository mockedRawEmailRepository() {
            return mock(RawEmailRepository.class);
        }
    }
}
