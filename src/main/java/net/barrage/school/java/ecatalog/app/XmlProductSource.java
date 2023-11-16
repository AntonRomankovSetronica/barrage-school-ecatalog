package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.barrage.school.java.ecatalog.config.ProductSourceProperties;
import net.barrage.school.java.ecatalog.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class XmlProductSource implements ProductSource {
    private final ProductSourceProperties.SourceProperty property;

    private static final Logger log = LoggerFactory.getLogger(XmlProductSource.class);

    @RequiredArgsConstructor
    @Component
    public static class Factory implements ProductSource.Factory {
        @Override
        public Set<String> getSupportedFormats() {
            return Set.of("xml");
        }

        @Override
        public ProductSource create(ProductSourceProperties.SourceProperty psp) {
            return new XmlProductSource(psp);
        }
    }

    @Override
    public List<Product> getProducts() {
        var products = new ArrayList<Product>();
        try {
            var factory = DocumentBuilderFactory
                    .newDefaultInstance();
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(new URL(property.getUrl()).openStream());

            NodeList nodeIterator = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeIterator.getLength(); i++) {
                Node node = nodeIterator.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    products.add(convert(node));
                }
            }
            return products;
        } catch (Exception e1) {
            log.warn("Oops!", e1);
            throw new RuntimeException(e1);
        }
    }


    private Product convert(Node sourceProduct) {
        var product = new Product();
        product.setId(UUID.randomUUID());

        NodeList sourceProductChildNodes = sourceProduct.getChildNodes();
        for (int i = 0; i < sourceProductChildNodes.getLength(); i++) {
            Node childNode = sourceProductChildNodes.item(i);

            switch (childNode.getNodeName()) {
                case "title" -> product.setName(childNode.getTextContent());
                case "description" -> product.setDescription(childNode.getTextContent());
                case "price" -> product.setPrice(Double.parseDouble(childNode.getTextContent()));
            }
        }

        return product;
    }
}
