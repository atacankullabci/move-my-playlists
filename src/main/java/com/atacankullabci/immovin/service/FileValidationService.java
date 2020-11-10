package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.enums.EnumTransformationType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileValidationService {

    public void validateLibraryFile(MultipartFile libraryFile) throws Exception {
        fileNameValidation(libraryFile.getOriginalFilename());

        LibraryTransformer transformer = new LibraryTransformer();
        transformer.transform(libraryFile.getBytes(), EnumTransformationType.LIBRARY);
    }

    public void fileNameValidation(String fileName) throws Exception {
        String fileExtension = fileName.split("\\.")[fileName.split("\\.").length - 1];
        if (!fileExtension.equals("xml")) {
            throw new Exception();
        }
    }
}
