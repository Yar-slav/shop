package com.gridu.store.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_items")
@Builder
public class ShopItemEntity {

    @Id
    private Long id;

    private Long available;

    @MapsId
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(name = "fk_shop_items_products"))
    private ProductEntity product;
}
