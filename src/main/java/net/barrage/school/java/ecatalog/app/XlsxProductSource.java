package net.barrage.school.java.ecatalog.app;

import lombok.RequiredArgsConstructor;
import net.barrage.school.java.ecatalog.config.ProductSourceProperties;
import net.barrage.school.java.ecatalog.model.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class XlsxProductSource implements ProductSource {
    private final ProductSourceProperties.SourceProperty property;

    private static final Logger log = LoggerFactory.getLogger(XlsxProductSource.class);

    @RequiredArgsConstructor
    @Component
    public static class Factory implements ProductSource.Factory {
        @Override
        public Set<String> getSupportedFormats() {
            return Set.of("excel");
        }

        @Override
        public ProductSource create(ProductSourceProperties.SourceProperty psp) {
            return new XlsxProductSource(psp);
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            var products = new ArrayList<Product>();
            XSSFWorkbook excelData = new XSSFWorkbook(new URL(property.getUrl()).openStream());
            XSSFSheet sheetData = excelData.getSheetAt(0);
            Iterator<Row> rowIterator = sheetData.rowIterator();
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                products.add(convert(row));
            }
            return products;
        } catch (Exception e1) {
            log.warn("Oops!", e1);
            throw new RuntimeException(e1);
        }
    }


    private Product convert(Row sourceProduct) {
        var product = new Product();
        product.setId(UUID.randomUUID());

        Iterator<Cell> cellIterator = sourceProduct.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            switch (cell.getColumnIndex()) {
                case 0 -> product.setName(cell.getStringCellValue());
                case 1 -> product.setImage(cell.getStringCellValue());
                case 2 -> product.setDescription(cell.getStringCellValue());
                case 4 -> product.setPrice(Double.parseDouble(cell.getStringCellValue()));
            }
        }

        return product;
    }
}
