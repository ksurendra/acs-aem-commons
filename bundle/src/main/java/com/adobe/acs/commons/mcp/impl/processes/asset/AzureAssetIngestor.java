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

import com.adobe.acs.commons.mcp.ProcessInstance;
import com.adobe.acs.commons.mcp.form.FormField;
import org.apache.http.client.HttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

import com.microsoft.azure.storage.CloudStorageAccount;

/**
 * Import assets and metadata provided by a spreadsheet
 */
public class AzureAssetIngestor extends AssetIngestor {

    private static final Logger LOG = LoggerFactory.getLogger(AzureAssetIngestor.class);
    private HttpClientBuilderFactory httpFactory;
    private HttpClient httpClient = null;

    public AzureAssetIngestor(MimeTypeService mimeTypeService) {
        super(mimeTypeService);
    }

    @FormField(
            name = "Account Name",
            description = "Account Name"
    )
    String accountName;

    @FormField(
            name = "Account Key",
            description = "Azure Account Key"
    )
    String accountKey;

    @FormField(
            name = "Connection timeout",
            description = "HTTP Connection timeout (in milliseconds)",
            required = true,
            options = ("default=30000")
    )
    private int timeout = 30000;


    @Override
    public void init() throws RepositoryException {
        super.init();

        LOG.info("\n\n****** accessName, accessKey="+accountName+" , "+accountKey);

        try {

            String storageConnectionString = "DefaultEndpointsProtocol=http;"
                    + "AccountName="+accountName+";"
                    + "AccountKey="+accountKey;
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            LOG.info("\n\n******* 1 account=" + account.toString());
        } catch (Exception ex) {
            LOG.info("\n\n******* init Exception", ex);
        }
    }


    @Override
    public void buildProcess(ProcessInstance instance, ResourceResolver rr) throws LoginException, RepositoryException {
        LOG.info("\n\n******* Entering buildProcess");

        String storageConnectionString = "DefaultEndpointsProtocol=http;"
                                            + "AccountName="+accountName+";"
                                            + "AccountKey="+accountKey;
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);

            //CloudBlobClient serviceClient = account.createCloudBlobClient();
            //LOG.info("\n\n******* serviceClient="+serviceClient.);

            // Container name must be lower case.
            //CloudBlobContainer container = serviceClient.getContainerReference("acsimages");
            //container.createIfNotExists();

        } catch (Exception ex) {
            LOG.info("\n\n******* buildProcess Exception", ex);
        }
    }

}
