package org.keycloak.broker.spid.metadata.extensions;

import org.keycloak.broker.spid.SpidIdentityProviderConfig;
import org.keycloak.dom.saml.v2.metadata.ContactType;
import org.keycloak.dom.saml.v2.metadata.ContactTypeType;
import org.keycloak.dom.saml.v2.metadata.ExtensionsType;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.util.DocumentUtil;
import org.keycloak.saml.common.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SpidContactType extends ContactType {

    public static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    public static final String SPID_METADATA_EXTENSIONS_NS = "https://spid.gov.it/saml-extensions";

    protected Document doc;

    protected SpidContactType(final SpidIdentityProviderConfig config) throws ConfigurationException {
        super(ContactTypeType.OTHER);
        this.setExtensions(new ExtensionsType());
        doc = DocumentUtil.createDocument();
    }

    /**
     * Adds an Empty XML tag element under the `spid` namespace
     * to the `Extensions` element
     * 
     * @param name
     */
    protected void addExtensionEmptySpidElement(String name) {
        // Private qualifier
        Element element = doc.createElementNS(SPID_METADATA_EXTENSIONS_NS, name);
        element.setAttributeNS(XMLNS_NS, "xmlns:spid", SPID_METADATA_EXTENSIONS_NS);
        getExtensions().addExtension(element);
    }

    /**
     * Adds an XML element to Extensions with name and value, under the `spid`
     * namespace
     * if the given value is not empty
     * 
     * @param name  Element name
     * @param value Element value
     */
    protected void addExtensionSpidElement(String name, String value) {
        if (StringUtil.isNullOrEmpty(value)) {
            return;
        }
        Element element = doc.createElementNS(SPID_METADATA_EXTENSIONS_NS, name);
        element.setAttributeNS(XMLNS_NS, "xmlns:spid", SPID_METADATA_EXTENSIONS_NS);
        element.setTextContent(value);
        getExtensions().addExtension(element);

    }
}
