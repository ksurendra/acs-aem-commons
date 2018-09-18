/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2017 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.commons.mcp.impl.processes.asset;

import com.adobe.acs.commons.mcp.AuthorizedGroupProcessDefinitionFactory;
import com.adobe.acs.commons.mcp.ProcessDefinition;
import com.adobe.acs.commons.mcp.ProcessDefinitionFactory;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.commons.mime.MimeTypeService;
import com.adobe.acs.commons.mcp.impl.processes.asset.AzureAssetIngestor;
import com.microsoft.azure.storage.CloudStorageAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Factory for asset import processes
 */
@Component
@Service(ProcessDefinitionFactory.class)
public class AzureAssetIngestorFactory extends AuthorizedGroupProcessDefinitionFactory<ProcessDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(AzureAssetIngestorFactory.class);

    @Reference
    private transient MimeTypeService mimeTypeService;

    @Override
    public String getName() {
        return "Azure Asset Ingestor";
    }

    @Override
    public ProcessDefinition createProcessDefinitionInstance() {
        return new AzureAssetIngestor(mimeTypeService);
    }

    @Override
    public boolean isAllowed(User user) {
        if (super.isAllowed(user)) {
            // check if Azure SDK is available
            try {
                CloudStorageAccount storageAccount;
                return true;
            } catch (NoClassDefFoundError e) {
                //ignore
                LOG.info("\n***NoClassDefFoundError - Azure CloudStorageAccount class not found");
            }
        }
        return false;
    }

    @Override
    protected final String[] getAuthorizedGroups() {
        return AssetIngestor.AUTHORIZED_GROUPS;
    }    
}
