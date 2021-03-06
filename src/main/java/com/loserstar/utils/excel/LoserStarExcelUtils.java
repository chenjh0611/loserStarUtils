/**
 * author: loserStar
 * date: 2018年5月29日下午9:48:47
 * email:362527240@qq.com
 * github:https://github.com/xinxin321198
 * remarks:
 */
package com.loserstar.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.loserstar.utils.ObjectMapConvert.LoserStarObjMapConvertUtil;
import com.loserstar.utils.json.LoserStarJsonUtil;

/**
 * author: loserStar 
 * date: 2018年5月29日下午9:48:47 
 * remarks: excel操作相关
 */
public class LoserStarExcelUtils {
	
	/**
	 * @param file excel文件对象
	 * @param hideRowIndex 隐藏行的index，此行的值当做key
	 * @param startRowIndex 数据开始行的index
	 * @return
	 * @throws IOException
	 */
	public static String readExceltoJson(File file, int hideRowIndex, int startRowIndex)
			throws IOException {
		Map<String, List> excelMap = new HashMap<String, List>();
		POIFSFileSystem fs = null;
		CellStyle cellStyle = null;
		Workbook workbook = null;
		if (file.getName().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(new FileInputStream(file));
		} else if (file.getName().endsWith("xls")) {
			fs = new POIFSFileSystem(new FileInputStream(file));
			workbook = new HSSFWorkbook(fs);
		} else {
			new Exception("上传文件必须后缀必须为xls或xlsx！");
		}

		int sheetsCounts = workbook.getNumberOfSheets();

		for (int i = 0; i < sheetsCounts; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			String[] cellNames;
			Row fisrtRow = sheet.getRow(hideRowIndex);

			if (null == fisrtRow) {
				continue;
			}

			int curCellNum = fisrtRow.getLastCellNum();
			cellNames = new String[curCellNum];
			for (int m = 0; m < curCellNum; m++) {
				Cell cell = fisrtRow.getCell(m);
				// 设置该列的样式是字符串
				cell.setCellStyle(cellStyle);
				cell.setCellType(CellType.STRING);
				// 取得该列的字符串值
				cellNames[m] = cell.getStringCellValue();
			}

			int rowNum = sheet.getLastRowNum();
			for (int j = startRowIndex; j <= rowNum; j++) {
				// 一行数据对于一个Map
				Map<String, String> rowMap = new HashMap<String, String>();
				// 取得某一行
				Row row = sheet.getRow(j);
				int cellNum = row.getLastCellNum();
				// 遍历每一列
				for (int k = 0; k < cellNum; k++) {
					Cell cell = row.getCell(k);
					cell.setCellStyle(cellStyle);
					// 保存该单元格的数据到该行中
					cell.setCellType(CellType.STRING);
					rowMap.put(cellNames[k], cell.getStringCellValue());
				}
				// 保存该行的数据到该表的List中
				list.add(rowMap);
			}
			// 将该sheet表的表名为key，List转为json后的字符串为Value进行存储
			excelMap.put(sheet.getSheetName(), list);
		}
		return LoserStarJsonUtil.toJsonDeep(excelMap);
	}
	
	public static void main(String[] args) {
		try {
			File file = new File("C:\\excelTest.xls");
			String json = readExceltoJson(file, 1, 2);
//			System.out.println(json);
			Map<String, Object> map = LoserStarJsonUtil.toModel(json, Map.class);
			List<Object> list = LoserStarObjMapConvertUtil.ConvertToList(map.get("部门绩效指标"));
			for (Object object : list) {
				Map<String, Object> objMap = LoserStarObjMapConvertUtil.ConvertToMap(object);
				System.out.println(LoserStarJsonUtil.toJsonDeep(objMap));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}