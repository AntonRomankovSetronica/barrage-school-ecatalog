package net.barrage.school.java.ecatalog.model;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class Product {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private double price;
}
