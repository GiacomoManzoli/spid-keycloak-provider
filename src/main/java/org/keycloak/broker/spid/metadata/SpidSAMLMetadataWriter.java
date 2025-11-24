package org.keycloak.broker.spid.metadata;

import org.keycloak.broker.spid.metadata.extensions.SpidContactType;
import org.keycloak.dom.saml.v2.metadata.ContactType;
import org.keycloak.dom.saml.v2.metadata.ContactTypeType;
import org.keycloak.saml.common.constants.JBossSAMLConstants;
import org.keycloak.saml.common.constants.JBossSAMLURIConstants;
import org.keycloak.saml.common.util.StaxUtil;
import org.keycloak.saml.processing.core.saml.v2.writers.SAMLMetadataWriter;

import org.keycloak.dom.saml.v2.metadata.ExtensionsType;
import org.keycloak.saml.common.exceptions.ProcessingException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Map;

public class SpidSAMLMetadataWriter extends SAMLMetadataWriter {

    private final String METADATA_PREFIX = "md";

    public SpidSAMLMetadataWriter(XMLStreamWriter writer) {
        super(writer);
    }

    /**
     * Override the standar write(contanct) in order to handle the otherAttributes
     */
    @Override
    public void write(ContactType contact) throws ProcessingException {
        StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.CONTACT_PERSON.get(),
                JBossSAMLURIConstants.METADATA_NSURI.get());

        if (contact instanceof SpidContactType) {
            Map<String, String> namespaces = ((SpidContactType) contact).getOtherNamespaces();
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                String prefix = entry.getKey();
                String uri = entry.getValue();
                StaxUtil.writeNameSpace(writer, prefix, uri);
            }
        }

        ContactTypeType attribs = contact.getContactType();
        StaxUtil.writeAttribute(writer, JBossSAMLConstants.CONTACT_TYPE.get(), attribs.value());

        /* Also prints the "otherAttributes" of the contact */
        Map<QName, String> otherAttributes = contact.getOtherAttributes();
        for (Map.Entry<QName, String> entry : otherAttributes.entrySet()) {
            QName attribData = entry.getKey();
            String prefix = attribData.getPrefix();
            String local = attribData.getLocalPart();
            String attrib = (prefix == null || prefix.isEmpty())
                    ? local
                    : prefix + ":" + local;
            StaxUtil.writeAttribute(writer, attrib, entry.getValue());
        }

        ExtensionsType extensions = contact.getExtensions();
        if (extensions != null) {
            write(extensions);
        }

        // Write the name
        String company = contact.getCompany();
        if (company != null) {
            StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.COMPANY.get(),
                    JBossSAMLURIConstants.METADATA_NSURI.get());
            StaxUtil.writeCharacters(writer, company);
            StaxUtil.writeEndElement(writer);
        }
        String givenName = contact.getGivenName();
        if (givenName != null) {
            StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.GIVEN_NAME.get(),
                    JBossSAMLURIConstants.METADATA_NSURI.get());
            StaxUtil.writeCharacters(writer, givenName);
            StaxUtil.writeEndElement(writer);
        }

        String surName = contact.getSurName();
        if (surName != null) {
            StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.SURNAME.get(),
                    JBossSAMLURIConstants.METADATA_NSURI.get());
            StaxUtil.writeCharacters(writer, surName);
            StaxUtil.writeEndElement(writer);
        }

        List<String> emailAddresses = contact.getEmailAddress();
        for (String email : emailAddresses) {
            StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.EMAIL_ADDRESS.get(),
                    JBossSAMLURIConstants.METADATA_NSURI.get());
            StaxUtil.writeCharacters(writer, email);
            StaxUtil.writeEndElement(writer);
        }

        List<String> tels = contact.getTelephoneNumber();
        for (String telephone : tels) {
            StaxUtil.writeStartElement(writer, METADATA_PREFIX, JBossSAMLConstants.TELEPHONE_NUMBER.get(),
                    JBossSAMLURIConstants.METADATA_NSURI.get());
            StaxUtil.writeCharacters(writer, telephone);
            StaxUtil.writeEndElement(writer);
        }

        StaxUtil.writeEndElement(writer);
        StaxUtil.flush(writer);
    }
}
