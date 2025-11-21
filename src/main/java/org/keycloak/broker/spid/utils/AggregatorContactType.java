package org.keycloak.broker.spid.utils;

public enum AggregatorContactType {

    PublicServicesFullAggregator,
    PublicServicesLightAggregator,
    PrivateServicesFullAggregator,
    PrivateServicesLightAggregator,
    PublicServicesFullOperator,
    PublicServicesLightOperator;

    public static AggregatorContactType fromString(String value) {
        if (value == null) return null;
        try {
            return AggregatorContactType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
