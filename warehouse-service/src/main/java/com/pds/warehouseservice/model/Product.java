package com.pds.warehouseservice.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productCode; // corresponds to productCode used by OrderProcessing

    private String name;

    public Product() {}
    public Product(String productCode, String name) {
        this.productCode = productCode;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (o != null && getClass() == o.getClass() && id != null && id.equals(((Product) o).id));
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
