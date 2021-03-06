/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.security.caas.user.core.domain;

import org.wso2.carbon.security.caas.api.util.CarbonSecurityConstants;
import org.wso2.carbon.security.caas.user.core.bean.Domain;
import org.wso2.carbon.security.caas.user.core.claim.Claim;
import org.wso2.carbon.security.caas.user.core.exception.DomainException;
import org.wso2.carbon.security.caas.user.core.store.connector.CredentialStoreConnector;
import org.wso2.carbon.security.caas.user.core.store.connector.IdentityStoreConnector;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Domain manager.
 */
public class DomainManager {

    /**
     * Mapping between domain priority and domain name to domain.
     * Map<String, Domain> maps between domain map to domain instance
     * Retrieval and insertion - O(log n)
     */
    private TreeMap<Integer, Map<String, Domain>> domainPriorityToDomainMap = new TreeMap<>();

    /**
     * Mapping between all domain names and domain instances.
     */
    private Map<String, Domain> allDomainNameToDomainMap = new HashMap<>();

    /**
     * Get the domain from the name.
     *
     * @param domainName Name of the domain.
     * @return Domain.
     * @throws DomainException domain exception
     */
    public Domain getDomainFromDomainName(String domainName) throws DomainException {

        Domain domain = allDomainNameToDomainMap.get(domainName);

        if (domain == null) {
            throw new DomainException(String.format("Domain %s was not found", domainName));
        }

        return domain;
    }

    /**
     * Get the list of domains which belongs to a certain priority.
     *
     * @param priority domain priority
     * @return Map<String, Domain>
     */
    public Map<String, Domain> getDomainsFromPriority(int priority) throws DomainException {

        Map<String, Domain> domainNameToDomainMap = domainPriorityToDomainMap.get(priority);

        if (domainNameToDomainMap == null) {
            throw new DomainException(String.format("Domain for priority %d not found", priority));
        }

        return domainNameToDomainMap;
    }

    /**
     * Add a domain to the mapping
     *
     * @param domain Domain object
     */
    public void addDomain(Domain domain) throws DomainException {

        String domainName = domain.getDomainName();
        int domainPriority = domain.getDomainPriority();

        if (allDomainNameToDomainMap.containsKey(domainName)) {
            throw new DomainException(String
                    .format("Domain %s already exists in the domain map", domainName));
        }

        if (!domainPriorityToDomainMap.containsKey(domainPriority)) {
            domainPriorityToDomainMap.put(domainPriority, new HashMap<>());
        }

        // Add to domain priority list and domain name list
        domainPriorityToDomainMap.get(domain.getDomainPriority()).put(domainName, domain);
        allDomainNameToDomainMap.put(domainName, domain);

    }

    /**
     * Get the domain instance when a user name is given.
     *
     * @param username String username
     * @return Domain instance for which the user belongs
     * @throws DomainException domain exception
     */
    public Domain getDomainFromUserName(String username)
            throws DomainException {

        // Check if the domain information is available in username
        String[] usernameSplit = username.split(CarbonSecurityConstants.URL_SPLITTER);

        if (usernameSplit.length > 0) {

            String domainName = usernameSplit[0];

            // Throws an exception if the specified domain is not found
            return getDomainFromDomainName(domainName);
        }

        // If the domain information is not available iterate through the connectors and find the
        // relevant domain. If no domain found throw an exception

//        for (Domain domain : allDomainNameToDomainMap.values()) {
//
//            Map<String, IdentityStoreConnector> identityStoreConnectorsMap =
//                    domain.getIdentityStoreConnectorMap();
//
//            for (IdentityStoreConnector identityStoreConnector : identityStoreConnectorsMap.values()) {

                // TODO: Implement primary and unique attributes along with unique user id.
//            }
//        }

        return null;

        // Domain for the username specified do not exist even in primary domain.
//        throw new DomainException(String.format("Username %s do not exist in any domain", username));
    }

    /**
     * Add an identity store connector to the map of a domain.
     *
     * @param identityStoreConnector Identity Store connector
     * @param domainName             Name of the domain to add the connector
     */
    public void addIdentityStoreConnectorToDomain(
            IdentityStoreConnector identityStoreConnector,
            String domainName) throws DomainException {

        Domain domain = getDomainFromDomainName(domainName);

        domain.addIdentityStoreConnector(identityStoreConnector);
    }

    /**
     * Get IdentityStoreConnector from identity store connector id.
     *
     * @param identityStoreConnectorId String - IdentityStoreConnectorId
     * @param domainName               Name of the domain which the connector instance belongs
     * @return IdentityStoreConnector
     * @throws DomainException Domain exception
     */
    public IdentityStoreConnector getIdentityStoreConnector(
            String identityStoreConnectorId, String domainName) throws DomainException {

        Domain domain = getDomainFromDomainName(domainName);

        IdentityStoreConnector identityStoreConnector = domain.getIdentityStoreConnectorFromId
                (identityStoreConnectorId);

        if (identityStoreConnector == null) {
            throw new DomainException(String
                    .format("IdentityStoreConnector %s was not found", identityStoreConnectorId));
        }

        return identityStoreConnector;
    }

    /**
     * Get identity store connector map.
     *
     * @param domainName Name of the domain which the connector instances belong
     * @return Map<String, IdentityStoreConnector> identityStoreConnectorsMap
     * @throws DomainException DomainException
     */
    public Map<String, IdentityStoreConnector> getIdentityStoreConnectorMapForDomain(
            String domainName) throws DomainException {

        return getDomainFromDomainName(domainName)
                .getIdentityStoreConnectorMap();

    }

    /**
     * Add an credential store connector to the map of a domain.
     *
     * @param credentialStoreConnector Credential Store connector
     * @param domainName               Name of the domain to add the connector
     */
    public void addCredentialStoreConnectorToDomain(
            CredentialStoreConnector credentialStoreConnector,
            String domainName) throws DomainException {

        Domain domain = getDomainFromDomainName(domainName);

        domain.addCredentialStoreConnector(credentialStoreConnector);
    }

    /**
     * Get CredentialStoreConnector from credential store connector id.
     *
     * @param credentialStoreConnectorId String - CredentialStoreConnectorId
     * @param domainName                 Name of the domain which the connector instance belongs
     * @return CredentialStoreConnector
     * @throws DomainException DomainException
     */
    public CredentialStoreConnector getCredentialStoreConnector(
            String credentialStoreConnectorId, String domainName) throws DomainException {

        Domain domain = getDomainFromDomainName(domainName);

        CredentialStoreConnector credentialStoreConnector = domain
                .getCredentialStoreConnectorFromId(credentialStoreConnectorId);

        if (credentialStoreConnector == null) {
            throw new DomainException(String
                    .format("credentialStoreConnector %s was not found", credentialStoreConnectorId));
        }

        return credentialStoreConnector;
    }

    /**
     * Get credential store connector map.
     *
     * @param domainName Name of the domain which the connector instances belong
     * @return Map<String, CredentialStoreConnector> credentialStoreConnectorsMap
     * @throws DomainException Domain exception
     */
    public Map<String, CredentialStoreConnector> getCredentialStoreConnectorMapForDomain(
            String domainName) throws DomainException {

        return getDomainFromDomainName(domainName)
                .getCredentialStoreConnectorMap();
    }

    /**
     * Resolve domain from claim.
     * Getting the claim from domains are done according to the priority
     *
     * @param claim Claim
     * @return Domain which has claim
     * @throws DomainException DomainException
     */
    public Domain getDomainFromClaim(Claim claim) throws DomainException {

        Domain domain = domainPriorityToDomainMap.values().stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ))
                .values()
                .stream()
                .filter(d -> d.isClaimBelongToDomain(claim))
                .collect(Collectors.toList())
                .get(0);

        if (domain == null) {

            throw new DomainException(String.format("Claim URI %s do not belong to any domain",
                    claim.getClaimURI()));
        }

        return domain;
    }

}
