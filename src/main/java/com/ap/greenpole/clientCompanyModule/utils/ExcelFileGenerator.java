package com.ap.greenpole.clientCompanyModule.utils;


import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Lewis.Aguh on 28/08/2020.
 */

public class ExcelFileGenerator<T> implements IFileGenerator<T>{

	private static Logger log = LoggerFactory.getLogger(ExcelFileGenerator.class);
    private String filename;
    private FileOutputStream fs;
    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private int currentRowNumber;
    private T target;
    
 
    public ExcelFileGenerator(String filename, T target){
        this.filename = filename;
        this.target = target;
        this.currentRowNumber = 0;
    }


    @Override
	public void open() {
		try {
			this.wb = new XSSFWorkbook();
			this.sheet = this.wb.createSheet("Query Export");
		} catch (Exception ex) {
			log.error("Error occurred {}", ex);
		}
	}
    
    @Override
	public void createDirectory(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			boolean flag = file.mkdirs();
			if (flag) {
				log.debug("Directory is created!");
			} else {
				log.error("Directory creation failed");
			}
		} else {
			log.debug("Directory already exists.");
		}
	}
	
	private void createHeaders() throws IllegalAccessException, InvocationTargetException, IllegalArgumentException {
		
		CellStyle headerCellStyle = this.wb.createCellStyle();
		headerCellStyle.setBorderTop((short) 6); // double lines border
		headerCellStyle.setBorderBottom((short) 1); // single line border
		XSSFFont font = this.wb.createFont();
		font.setFontHeightInPoints((short) 13);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		headerCellStyle.setFont(font);
		
		XSSFRow title = this.sheet.createRow(this.currentRowNumber++);
		title.createCell(0).setCellValue("Export");
		title.getCell(0).setCellStyle(headerCellStyle);

		XSSFRow blank = this.sheet.createRow(this.currentRowNumber++);
		blank.createCell(0).setCellValue(" ");

		// create header
		XSSFRow header = this.sheet.createRow(this.currentRowNumber++);
		List<Field> fieldsList = Arrays.asList(this.target.getClass().getDeclaredFields());
		
		int counter = 0;
		for(final Field property: fieldsList) {
			if(!(Collection.class.isAssignableFrom(property.getType()))) {
				header.createCell(counter).setCellValue(Utils.splitCamelCase(property.getName()).toUpperCase());
				header.getCell(counter).setCellStyle(headerCellStyle);
				
				counter++;
			}
		}

		int x = 0;
	}
	
	private void createActiveRows(List<T> dataList) {
		
		CellStyle cellStyle = this.wb.createCellStyle();
		CreationHelper createHelper = this.wb.getCreationHelper();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MMM/yyyy h:mm:ss"));
		
		for(Object obj: dataList) {
			
			XSSFRow row = this.sheet.createRow(this.currentRowNumber++);
			
			final BeanWrapper src = new BeanWrapperImpl(obj);
			List<Field> fieldsList = Arrays.asList(obj.getClass().getDeclaredFields());
			
			int counter = 0;
			for(final Field property: fieldsList) {
				if(!(Collection.class.isAssignableFrom(property.getType()))) {
					Object providedObject = src.getPropertyValue(property.getName());
					if(providedObject != null) {
						row.createCell(counter).setCellValue(providedObject.toString());
						row.getCell(counter).setCellStyle(cellStyle);
					}
					counter++;
				}
			}
			
		}
	}
	
	private List<String> getActiveFieldNames() {
		List<Field> fieldsList = Arrays.asList(this.target.getClass().getDeclaredFields());
		List<String> result = new ArrayList<>();
		for (final Field property : fieldsList) {
			if (!(Collection.class.isAssignableFrom(property.getType()))) {
				result.add(property.getName());
			}
		}
		return result;
	}

	@Override
	public void close() throws Exception {}


	@Override
	public void write(List<T> dataList) {
		try {
			createHeaders();
			createActiveRows(dataList);
			resizeColumns(getActiveFieldNames());
		} catch (IllegalAccessException | InvocationTargetException
				| IllegalArgumentException ex) {
			log.error("Error occurred {}", ex);
		}
	}

	private void resizeColumns(List<String> activeFields) {
		int numCol = activeFields.size();
		try {
			for (int i = 0; i < numCol; i++) {
				this.sheet.autoSizeColumn(i);
			}
			this.fs = new FileOutputStream(this.filename);
			if (null != this.fs) {
				this.wb.write(this.fs);
				this.fs.close();
			}

		} catch (Exception ex) {
			log.error("{}", ex);
		}
	}
}