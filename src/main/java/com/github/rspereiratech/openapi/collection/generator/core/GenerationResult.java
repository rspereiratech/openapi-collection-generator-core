package com.github.rspereiratech.openapi.collection.generator.core;

import java.io.File;
import java.util.List;

/**
 * Result of generating collections for a single format.
 *
 * @param collectionFile   the main collection file that was written
 * @param additionalFiles  any additional files written (e.g. environment files)
 */
public record GenerationResult(File collectionFile, List<File> additionalFiles) {}
