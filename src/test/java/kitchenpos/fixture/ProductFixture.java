package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product createProduct(String name, BigDecimal price) {
        return new Product(name, price);
    }
}
