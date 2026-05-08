package com.github.rspereiratech.openapi.collection.generator.core.factory;

import com.github.rspereiratech.openapi.collection.generator.core.generator.CollectionGenerator;
import com.github.rspereiratech.openapi.collection.generator.core.model.CollectionFormat;

/**
 * Factory contract for creating a {@link CollectionGenerator} based on the requested output format.
 */
public interface CollectionGeneratorFactory {

    /**
     * Creates a {@link CollectionGenerator} appropriate for the given format.
     *
     * @param format the target collection format
     * @return a configured {@link CollectionGenerator} instance
     */
    CollectionGenerator create(CollectionFormat format);
}
