package com.program.flow_manager.domain.model;

import com.program.flow_manager.exception.UnsupportedFileTypeException;

import java.util.Locale;

public enum SupportedFileType {
    TXT,
    PNG,
    JPG,
    JPEG,
    ZIP;

    public static SupportedFileType fromFilename(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == fileName.length() - 1) {
            throw new UnsupportedFileTypeException("File type cannot be inferred from filename: " + fileName);
        }
        return from(fileName.substring(extensionIndex + 1));
    }

    public static SupportedFileType from(String value) {
        try {
            return SupportedFileType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + value, ex);
        }
    }

    public SupportedFileType normalized() {
        return this == JPEG ? JPG : this;
    }
}