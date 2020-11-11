package com.atacankullabci.immovin.service;

import com.atacankullabci.immovin.common.enums.EnumTransformationType;
import com.atacankullabci.immovin.exception.InvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.TransformerException;

@Service
public class FileValidationService {

    public void validateLibraryFile(MultipartFile libraryFile) throws Exception {
        fileExtensionValidation(libraryFile.getOriginalFilename());
        fileContentValidation(libraryFile.getBytes());
    }

    public void fileContentValidation(byte[] file) throws InvalidFileException {
        try {
            LibraryTransformer transformer = new LibraryTransformer();
            transformer.transform(file, EnumTransformationType.LIBRARY);
        } catch (TransformerException exception) {
            throw new InvalidFileException("File content is corrupted");
        }
    }

    public void fileExtensionValidation(String fileName) throws InvalidFileException {
        String fileExtension = fileName.split("\\.")[fileName.split("\\.").length - 1];
        if (!fileExtension.equals("xml")) {
            throw new InvalidFileException("File extension is unacceptable");
        }
    }
}
