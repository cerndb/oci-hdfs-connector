/**
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 */
package com.oracle.bmc.hdfs.store;

import java.util.concurrent.Callable;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Function;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;

/**
 * Callable that performs a single object copy request.
 */
public class CopyOperation implements Callable<String> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CopyOperation.class);
    private final ObjectStorage client;
    private final GetObjectRequest getRequest;
    private final Function<GetObjectResponse, PutObjectRequest> putRequestFn;

    /**
     * Returns the entity tag of the newly copied object.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String call() throws Exception {
        final GetObjectResponse getResponse = this.client.getObject(this.getRequest);
        try {
            LOG.debug("Attempting to copy source object with eTag {}", getResponse.getETag());
            final PutObjectRequest putRequest = this.putRequestFn.apply(getResponse);
            final PutObjectResponse putResponse = this.client.putObject(putRequest);
            return putResponse.getETag();
        } finally {
            IOUtils.closeQuietly(getResponse.getInputStream());
        }
    }

    @java.beans.ConstructorProperties({"client", "getRequest", "putRequestFn"})
    public CopyOperation(final ObjectStorage client, final GetObjectRequest getRequest, final Function<GetObjectResponse, PutObjectRequest> putRequestFn) {
        this.client = client;
        this.getRequest = getRequest;
        this.putRequestFn = putRequestFn;
    }
}
