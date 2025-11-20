package com.pds.warehouseservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private Long productCode; // corresponds to productCode used by OrderProcessing

    @Setter
    private String name;


    /**
     * Required by Hibernate for entity creation and proxy generation.
     */
    public Product() {
        // Default constructor
    }


    public Product(Long productCode, String name) {
        this.productCode = productCode;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (o != null && getClass() == o.getClass() && id != null && id.equals(((Product) o).id));
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}