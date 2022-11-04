package kitchenpos.application;

import static kitchenpos.fixture.TableFixture.createOrderTableRequest;
import static kitchenpos.fixture.TableFixture.createTableGroupRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.product.domain.Product;
import kitchenpos.table.domain.TableGroup;
import kitchenpos.table.dto.TableGroupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("TableGroupService 클래스의")
class TableGroupServiceTest extends ServiceTest {

    @Nested
    @DisplayName("create 메서드는")
    class Create {

        @Test
        @DisplayName("orderTable 목록을 그룹화한다.")
        void success() {
            // given
            OrderTable orderTable1 = saveOrderTable(2, true);
            OrderTable orderTable2 = saveOrderTable(4, true);
            TableGroupRequest request = new TableGroupRequest(List.of(orderTable1, orderTable2));

            // when
            TableGroup savedTableGroup = tableGroupService.create(request);

            // then
            Optional<TableGroup> actual = tableGroupRepository.findById(savedTableGroup.getId());
            assertThat(actual).isPresent();
        }

        @Test
        @DisplayName("그룹화할 orderTable이 존재하지 않는 경우 예외를 던진다.")
        void orderTable_NotExist_ExceptionThrown() {
            // given
            OrderTable orderTable1 = saveOrderTable(2, true);
            OrderTable orderTable2 = createOrderTableRequest(4, true).toEntity();
            TableGroupRequest request = createTableGroupRequest(orderTable1, orderTable2);

            // when & then
            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("그룹화할 orderTable이 빈 테이블이 아닌 경우 예외를 던진다.")
        void orderTable_NotEmpty_ExceptionThrown() {
            // given
            OrderTable orderTable1 = saveOrderTable(2, true);
            OrderTable orderTable2 = saveOrderTable(4, false);
            TableGroupRequest request = createTableGroupRequest(orderTable1, orderTable2);

            // when & then
            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("그룹화할 orderTable이 이미 그룹에 속한 경우 예외를 던진다.")
        void orderTable_alreadyGrouped_ExceptionThrown() {
            // given
            OrderTable orderTable1 = saveOrderTable(0, true);
            OrderTable orderTable2 = saveOrderTable(0, true);
            OrderTable orderTable3 = saveOrderTable(0, true);
            OrderTable orderTable4 = saveOrderTable(0, true);
            saveTableGroup(orderTable1, orderTable2, orderTable3, orderTable4);
            TableGroupRequest request = createTableGroupRequest(orderTable1, orderTable2);

            // when & then
            assertThatThrownBy(() -> tableGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ungroup 메서드는")
    class Ungroup {

        @Test
        @DisplayName("그룹을 해제한다.")
        void success() {
            // given
            OrderTable orderTable1 = saveOrderTable(2, true);
            OrderTable orderTable2 = saveOrderTable(4, true);
            TableGroup savedTableGroup = saveTableGroup(orderTable1, orderTable2);

            // when
            tableGroupService.ungroup(savedTableGroup.getId());

            // then
            Optional<OrderTable> actualOrderTable1 = orderTableRepository.findById(orderTable1.getId());
            Optional<OrderTable> actualOrderTable2 = orderTableRepository.findById(orderTable2.getId());
            assertThat(actualOrderTable1).isPresent();
            assertThat(actualOrderTable2).isPresent();
            assertAll(
                    () -> assertThat(actualOrderTable1.get().getTableGroup()).isNull(),
                    () -> assertThat(actualOrderTable1.get().isEmpty()).isFalse(),
                    () -> assertThat(actualOrderTable2.get().getTableGroup()).isNull(),
                    () -> assertThat(actualOrderTable2.get().isEmpty()).isFalse()
            );
        }

        @ParameterizedTest
        @EnumSource(
                value = OrderStatus.class,
                names = {"COMPLETION"},
                mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("orderTable에 해당하는 order의 orderStatus가 COMPLETION이 아닌 경우 예외를 던진다.")
        void orderStatus_NotCompletion_ExceptionThrown(OrderStatus orderStatus) {
            // given
            OrderTable orderTable1 = saveOrderTable(2, true);
            OrderTable orderTable2 = saveOrderTable(4, true);
            TableGroup savedTableGroup = saveTableGroup(orderTable1, orderTable2);

            MenuGroup menuGroup = saveMenuGroup("반마리치킨");
            Product product = saveProduct("크림치킨", BigDecimal.valueOf(15000.00));
            Menu menu1 = saveMenu("크림치킨", menuGroup, product);
            Menu menu2 = saveMenu("크림어니언치킨", menuGroup, product);
            Order savedOrder = saveOrder(orderTable1, menu1, menu2);
            entityManager.flush();
            updateOrder(savedOrder, orderStatus.name());

            // when & then
            assertThatThrownBy(() -> tableGroupService.ungroup(savedTableGroup.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
