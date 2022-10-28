package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenuGroup;
import static kitchenpos.fixture.MenuFixture.createMenuProduct;
import static kitchenpos.fixture.MenuFixture.createMenuRequest;
import static kitchenpos.fixture.OrderFixture.createOrderRequest;
import static kitchenpos.fixture.OrderFixture.updatedOrderStatusRequest;
import static kitchenpos.fixture.ProductFixture.createProductRequest;
import static kitchenpos.fixture.TableFixture.createOrderTableRequest;
import static kitchenpos.fixture.TableFixture.createTableGroupRequest;

import java.math.BigDecimal;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.MenuRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
abstract class ServiceTest {

    @Autowired
    protected MenuGroupService menuGroupService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected TableService tableService;

    @Autowired
    protected TableGroupService tableGroupService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected MenuGroupDao menuGroupDao;

    @Autowired
    protected ProductDao productDao;

    @Autowired
    protected MenuDao menuDao;

    @Autowired
    protected OrderTableDao orderTableDao;

    @Autowired
    protected TableGroupDao tableGroupDao;

    @Autowired
    protected OrderDao orderDao;

    protected MenuGroup saveMenuGroup(String name) {
        return menuGroupService.create(createMenuGroup(name));
    }

    protected Product saveProduct(String name, BigDecimal price) {
        return productService.create(createProductRequest(name, price));
    }

    protected Menu saveMenu(String menuName, MenuGroup menuGroup, Product product) {
        MenuProduct menuProduct = createMenuProduct(product.getId(), 1, product.getPrice());
        MenuRequest request = createMenuRequest(menuName,
                product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())), menuGroup.getId(),
                menuProduct);
        return menuService.create(request);
    }

    protected OrderTable saveOrderTable(int numberOfGuests, boolean empty) {
        return tableService.create(createOrderTableRequest(numberOfGuests, empty));
    }

    protected TableGroup saveTableGroup(OrderTable... orderTables) {
        return tableGroupService.create(createTableGroupRequest(orderTables));
    }

    protected Order saveOrder(OrderTable orderTable, Menu... menus) {
        return orderService.create(createOrderRequest(orderTable, menus));
    }

    protected Order updateOrder(Order order, String status) {
        return orderService.changeOrderStatus(order.getId(), updatedOrderStatusRequest(status));
    }
}
