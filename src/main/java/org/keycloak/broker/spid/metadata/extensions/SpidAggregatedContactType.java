package org.keycloak.broker.spid.metadata.extensions;

import org.jboss.logging.Logger;
import org.keycloak.broker.spid.SpidIdentityProviderConfig;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.util.StringUtil;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * ContactPerson for the Aggregated subject (spid:entityType="spid:aggregated").
 */
public class SpidAggregatedContactType extends SpidContactType {

    private static final Logger logger = Logger.getLogger(SpidAggregatedContactType.class);

    public static Optional<SpidAggregatedContactType> build(final SpidIdentityProviderConfig config) throws ConfigurationException {
        if (!config.isAggregatorEnabled()) {
            return Optional.empty();
        }
        
        if (StringUtil.isNullOrEmpty(config.getAggregatedCompany())) {
            logger.warn("Aggregated contact missing company, skipping creation.");
            return Optional.empty();
        }
        return Optional.of(new SpidAggregatedContactType(config));
    }

    private SpidAggregatedContactType(final SpidIdentityProviderConfig config) throws ConfigurationException {
        super(config);
        
        addOtherAttribute(new QName(SPID_METADATA_EXTENSIONS_NS, "entityType", "spid"), "spid:aggregated");


        if (!StringUtil.isNullOrEmpty(config.getAggregatedCompany())) {
            this.setCompany(config.getAggregatedCompany());
        }
        if (!StringUtil.isNullOrEmpty(config.getAggregatedEmail())) {
            this.addEmailAddress(config.getAggregatedEmail());
        }
        if (!StringUtil.isNullOrEmpty(config.getAggregatedPhone())) {
            this.addTelephone(config.getAggregatedPhone());
        }

        addExtensionSpidElement("spid:IPACode", config.getAggregatedIpaCode());
        addExtensionSpidElement("spid:VATNumber", config.getAggregatedVatNumber());
        addExtensionSpidElement("spid:FiscalCode", config.getAggregatedFiscalCode());


        addAggregatedType(config);
    }

    private void addAggregatedType(final SpidIdentityProviderConfig config) {
        String aggregatedType = config.getAggregatedType();
        if (StringUtil.isNullOrEmpty(aggregatedType)) {
            aggregatedType = fallbackAggregatedType(config);
        }
        if (StringUtil.isNullOrEmpty(aggregatedType)) {
            logger.warn("Aggregated type missing, skipping type tag.");
            return;
        }

        String tagName;
        switch (aggregatedType) {
            case "Public":
            case "PublicServicesOperator":
            case "PublicOperator":
            case "Private":
                tagName = "spid:" + aggregatedType;
                addExtensionEmptySpidElement(tagName);
                break;
            default:
                logger.warnf("Unknown aggregated type '%s', skipping type tag", aggregatedType);
                return;
        }        
    }

    private String fallbackAggregatedType(final SpidIdentityProviderConfig config) {
        if (!StringUtil.isNullOrEmpty(config.getAggregatedVatNumber()) || !StringUtil.isNullOrEmpty(config.getAggregatedFiscalCode())) {
            return "Private";
        }
        if (!StringUtil.isNullOrEmpty(config.getAggregatedIpaCode())) {
            return "Public";
        }
        return null;
    }

}
