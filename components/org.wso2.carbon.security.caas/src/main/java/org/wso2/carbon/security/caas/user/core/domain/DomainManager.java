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

import org.wso2.carbon.security.caas.user.core.bean.Domain;
import org.wso2.carbon.security.caas.user.core.config.StoreConfig;
import org.wso2.carbon.security.caas.user.core.exception.CredentialStoreException;
import org.wso2.carbon.security.caas.user.core.exception.IdentityStoreException;

/**
 * Domain manager.
 */
public interface DomainManager {

    /**
     * Get the domain from the name.
     *
     * @param domainName Name of the domain.
     * @return Domain.
     */
    Domain getDomainFromName(String domainName);

    /**
     * Add a domain to the mapping
     *
     * @param domainName  Name of the domain
     * @param storeConfig Store configuration for the domain
     */
    void addDomain(String domainName, StoreConfig storeConfig)
            throws CredentialStoreException, IdentityStoreException;

    /**
     * Get the domain instance when a user name is given.
     *
     * @param username String username
     * @return Domain instance for which the user belongs
     */
    Domain getDomainFromUserName(String username);
}