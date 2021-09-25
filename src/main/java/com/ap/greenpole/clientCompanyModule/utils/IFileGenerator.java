package com.ap.greenpole.clientCompanyModule.utils;

import java.util.List;

/**
 * Created by Lewis.Aguh on 28/08/2020.
 */
public interface IFileGenerator<T> extends AutoCloseable{

    void open();
    void write(List<T> list);
    void createDirectory(String filename);
}
