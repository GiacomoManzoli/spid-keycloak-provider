package org.keycloak.broker.spid.metadata.extensions;

import org.keycloak.broker.spid.SpidIdentityProviderConfig;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.util.StringUtil;

import java.util.Optional;


public class SpidAggregatorContactType extends SpidContactType {


    public static Optional<SpidAggregatorContactType> build(final SpidIdentityProviderConfig config) throws ConfigurationException {
        if ( StringUtil.isNullOrEmpty(config.getAggregatorContactCompany()) &&
            StringUtil.isNullOrEmpty(config.getAggregatorContactEmail()) &&
            StringUtil.isNullOrEmpty(config.getAggregatorContactPhone())) {
            return Optional.empty();
        } else {
            return Optional.of(new SpidAggregatorContactType(config));
        }
    }

    protected SpidAggregatorContactType(final SpidIdentityProviderConfig config) throws ConfigurationException {
        super(config);

        String conctactCompany = config.getAggregatorContactCompany();
        if (!StringUtil.isNullOrEmpty(conctactCompany)) {
            this.setCompany(conctactCompany);
        }

        String contactEmail = config.getAggregatorContactEmail();
        if (!StringUtil.isNullOrEmpty(contactEmail)) {
            this.addEmailAddress(contactEmail);
        }
        
        String contactPhone = config.getAggregatorContactPhone();
        if (!StringUtil.isNullOrEmpty(contactPhone)) {
            this.addTelephone(contactPhone);
        }

        this.addExtensionSpidElement("spid:IPACode", config.getAggregatorContactIpaCode());
        this.addExtensionSpidElement("spid:VATNumber", config.getAggregatorContactVatNumber());
        this.addExtensionSpidElement("spid:FiscalCode", config.getAggregatorContactFiscalCode());

        String spidAggergatorContactType = "spid:"+config.getAggregatorContactType();
        this.addExtensionEmptySpidElement(spidAggergatorContactType);
    } 

}
