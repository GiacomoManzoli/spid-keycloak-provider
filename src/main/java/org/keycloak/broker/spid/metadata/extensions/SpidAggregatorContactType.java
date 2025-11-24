package org.keycloak.broker.spid.metadata.extensions;

import org.jboss.logging.Logger;
import org.keycloak.broker.spid.SpidIdentityProviderConfig;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.util.StringUtil;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * ContactPerson for the Aggregator (spid:entityType="spid:aggregator").
 */
public class SpidAggregatorContactType extends SpidContactType {

    private static final Logger logger = Logger.getLogger(SpidAggregatorContactType.class);

    private static final String MD_NS = "urn:oasis:names:tc:SAML:2.0:metadata";
    private static final String XMLDSIG_NS = "http://www.w3.org/2000/09/xmldsig#";


    public static Optional<SpidAggregatorContactType> build(final SpidIdentityProviderConfig config) throws ConfigurationException {
        if (!config.isAggregatorEnabled()) {
            return Optional.empty();
        }
        if (StringUtil.isNullOrEmpty(config.getAggregatorCompany()) && StringUtil.isNullOrEmpty(config.getAggregatorEmail())) {
            logger.warn("Aggregator contact missing company/email, skipping creation.");
            return Optional.empty();
        }
        return Optional.of(new SpidAggregatorContactType(config));
    }

    private SpidAggregatorContactType(final SpidIdentityProviderConfig config) throws ConfigurationException {
        super(config);

 
        if (!StringUtil.isNullOrEmpty(config.getAggregatorCompany())) {
            this.setCompany(config.getAggregatorCompany());
        }
        if (!StringUtil.isNullOrEmpty(config.getAggregatorEmail())) {
            this.addEmailAddress(config.getAggregatorEmail());
        }
        if (!StringUtil.isNullOrEmpty(config.getAggregatorPhone())) {
            this.addTelephone(config.getAggregatorPhone());
        }

        addOtherAttribute(new QName(SPID_METADATA_EXTENSIONS_NS, "entityType", "spid"), "spid:aggregator");

        addExtensionSpidElement("spid:IPACode", config.getAggregatorIpaCode());
        addExtensionSpidElement("spid:VATNumber", config.getAggregatorVatNumber());
        addExtensionSpidElement("spid:FiscalCode", config.getAggregatorFiscalCode());
        
        addActivityElement(config.getAggregatorActivity());

        if (requiresValidationCert(config)) {
            addValidationKey(config.getAggregatorValidationCert());
        }
    }

    private void addActivityElement(String activity) {
        mapActivity(activity).ifPresent(tag -> {
            this.addExtensionEmptySpidElement(tag);
        });
    }

    private Optional<String> mapActivity(String activity) {
        if (StringUtil.isNullOrEmpty(activity)) {
            return Optional.empty();
        }
        activity = activity.trim();
        switch (activity) {
            case "PublicServicesFullAggregator":
            case "PublicServicesLightAggregator":
            case "PrivateServicesFullAggregator":
            case "PrivateServicesLightAggregator":
            case "PublicServicesFullOperator":
            case "PublicServicesLightOperator":
                String tagName = "spid:"+activity;
                return Optional.of(tagName);
            default:
                logger.warnf("Unknown aggregator activity '%s', skipping activity tag", activity);
                return Optional.empty();
        }
    }

    private boolean requiresValidationCert(SpidIdentityProviderConfig config) {
        String activity = config.getAggregatorActivity();
        return activity != null && activity.contains("Light") && !StringUtil.isNullOrEmpty(config.getAggregatorValidationCert());
    }

    private void addValidationKey(String validationCert) {
        Element keyDescriptor = doc.createElementNS(SPID_METADATA_EXTENSIONS_NS, "spid:KeyDescriptor");
        keyDescriptor.setAttributeNS(XMLNS_NS, "xmlns:spid", SPID_METADATA_EXTENSIONS_NS);
        keyDescriptor.setAttributeNS(MD_NS, "md:use", "spid:validation");

        Element keyInfo = doc.createElementNS(XMLDSIG_NS, "ds:KeyInfo");
        keyInfo.setAttributeNS(XMLNS_NS, "xmlns:ds", XMLDSIG_NS);
        Element x509Data = doc.createElementNS(XMLDSIG_NS, "ds:X509Data");
        Element x509Certificate = doc.createElementNS(XMLDSIG_NS, "ds:X509Certificate");
        x509Certificate.setTextContent(validationCert);
        x509Data.appendChild(x509Certificate);
        keyInfo.appendChild(x509Data);
        keyDescriptor.appendChild(keyInfo);

        getExtensions().addExtension(keyDescriptor);
    }

    

}
