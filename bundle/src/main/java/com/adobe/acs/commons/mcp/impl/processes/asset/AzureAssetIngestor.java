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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.ListBlobItem;


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
            name = "Container Name",
            description = "Azure Blob Container Name"
    )
    String accountContainerName;

    @FormField(
            name = "Connection timeout",
            description = "HTTP Connection timeout (in milliseconds)",
            required = true,
            options = ("default=30000")
    )
    private int timeout = 30000;

    transient CloudStorageAccount storageAccount;

    @Override
    public void init() throws RepositoryException {
        super.init();
    }

    @Override
    public void buildProcess(ProcessInstance instance, ResourceResolver rr) throws LoginException, RepositoryException {
        LOG.info("\n\n******* buildProcess");

        try {
            String storageConnectionString = "DefaultEndpointsProtocol=http;"
                    + "AccountName="+accountName+";"
                    + "AccountKey="+accountKey;

            //CloudBlobContainer blobContainer = CloudStorageAccount.parse(storageConnectionString).createCloudBlobClient().getContainerReference("acsazurecontainer");

            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            if (storageAccount==null) {
                throw new Exception("Connection to Azure Storage Account Failed!");
            }

            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            if (blobClient==null) {
                throw new Exception("Connection to Azure Storage Blob Client Failed!");
            }

            CloudBlobContainer blobContainer = blobClient.getContainerReference(accountContainerName);

            if (blobContainer==null) {
                throw new Exception("Connection to Azure Storage Blob Container Failed!");
            }

            LOG.info("\n\n******* Connected to Azure Blob Storage - blobContainer="+blobContainer);

            // Loop over blobs within the container and output the URI to each of them.
            for (ListBlobItem blobItem : blobContainer.listBlobs()) {
                LOG.info("\n\n******* blobItem.getUri()="+blobItem.getUri());
            }

        } catch(InvalidKeyException ex) {
            LOG.info("\n******* Invalid credentials", ex);
        } catch (Exception ex) {
            LOG.info("\n******* buildProcess Exception", ex.getMessage());
        }
    }

    public void closeConnection() {

    }
}
