/**
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 */
package com.oracle.bmc.hdfs.store;

import java.util.concurrent.Callable;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.RenameObjectRequest;
import com.oracle.bmc.objectstorage.responses.RenameObjectResponse;

/**
 * Callable that performs a rename as a sequence of copy+delete steps.
 */
public class RenameOperation implements Callable<String> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RenameOperation.class);
    private final ObjectStorage objectStorage;
    private final RenameObjectRequest renameRequest;

    /**
     * Delete will not happen if the copy fails. Returns the entity tag of the newly copied renamed object.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String call() throws Exception {
        LOG.debug("Renaming object from {} to {}.", this.renameRequest.getRenameObjectDetails().getSourceName(), this.renameRequest.getRenameObjectDetails().getNewName());
        RenameObjectResponse renameResponse = this.objectStorage.renameObject(this.renameRequest);
        return renameResponse.getETag();
    }

    @java.beans.ConstructorProperties({"objectStorage", "renameRequest"})
    public RenameOperation(final ObjectStorage objectStorage, final RenameObjectRequest renameRequest) {
        this.objectStorage = objectStorage;
        this.renameRequest = renameRequest;
    }
}
