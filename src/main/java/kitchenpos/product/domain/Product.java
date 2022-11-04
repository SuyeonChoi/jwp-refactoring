package kitchenpos.product.domain;

import java.math.BigDecimal;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import kitchenpos.common.vo.Price;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Embedded
    private Price price;

    protected Product() {
    }

    public Product(final String name, final Price price) {
        this(null, name, price);
    }

    public Product(final Long id, final String name, final Price price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.getPrice();
    }
}
