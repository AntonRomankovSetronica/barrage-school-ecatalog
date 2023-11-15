package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.barrage.school.java.ecatalog.config.ProductSourceProperties;
import net.barrage.school.java.ecatalog.model.Product;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class ExcelProductSource implements ProductSource {
    private final ProductSourceProperties.SourceProperty property;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(ExcelProductSource.class);

    @RequiredArgsConstructor
    @Component
    public static class Factory implements ProductSource.Factory {
        private final ObjectMapper objectMapper;

        @Override
        public Set<String> getSupportedFormats() {
            return Set.of("excel");
        }

        @Override
        public ProductSource create(ProductSourceProperties.SourceProperty psp) {
            return new ExcelProductSource(psp, objectMapper);
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            URL url = new URL(property.getUrl());
            InputStream inputStream = url.openStream();
            Workbook workbook = new XSSFWorkbook(inputStream);

                Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

                Iterator<Row> iterator = sheet.iterator();
                List<Product> productList = new ArrayList<>();

                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    if (currentRow.getRowNum() == 0) {
                        // Skip header row
                        continue;
                    }

                    Product product = new Product();
                    product.setName(currentRow.getCell(0).getStringCellValue());
                    product.setImage(currentRow.getCell(1).getStringCellValue());
                    product.setId(UUID.randomUUID());
                    product.setDescription(null);
                    product.setPrice(Double.parseDouble(currentRow.getCell(4).getStringCellValue()));

                    // Add the product to the list
                    productList.add(product);
                }

                workbook.close();
                return productList;

            } catch (Exception e1) {
            log.warn("Oops!", e1);
            throw new RuntimeException(e1);
        }
    }


    private Product convert(SourceProduct sourceProduct) {
        var product = new Product();
        product.setName(sourceProduct.getName());
        product.setDescription(null);
        product.setImage(Optional.ofNullable(sourceProduct.photo_url)
                .flatMap(media -> media.stream().findFirst())
                .orElse(null));
        product.setPrice(sourceProduct.getPrice());
        return product;
    }

    static class SourceProductList extends ArrayList<SourceProduct> {
    }

    @Data
    static class SourceProduct {
        private String name;
        private String quantity;
        private Integer id;
        private List<String> photo_url;
        private double price;
    }
}
