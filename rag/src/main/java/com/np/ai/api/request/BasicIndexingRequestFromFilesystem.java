package com.np.ai.api.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BasicIndexingRequestFromFilesystem(@NotBlank String path,
                                                 @NotBlank String outputFilename,
                                                 boolean appendIfFileExists,
                                                 List<String> keywords) {
}
