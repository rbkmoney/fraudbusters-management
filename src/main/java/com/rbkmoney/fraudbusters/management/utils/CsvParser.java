package com.rbkmoney.fraudbusters.management.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface CsvParser<T> {

    boolean hasCSVFormat(MultipartFile file);

    List<T> parse(InputStream is);

}
