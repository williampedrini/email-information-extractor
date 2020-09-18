module email.information.extractor.domain {
    requires annotations;
    requires com.fasterxml.jackson.annotation;
    requires commons.io;
    requires mail;
    requires lombok;
    requires spring.beans;
    requires spring.context;

    exports com.custodio.email.extractor.domain.configuration;
    exports com.custodio.email.extractor.domain.entity;
    exports com.custodio.email.extractor.domain.port;
    exports com.custodio.email.extractor.domain.value.processed;
    exports com.custodio.email.extractor.domain.value.raw;
}
