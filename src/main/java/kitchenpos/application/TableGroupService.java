package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTables;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.TableGroupRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableGroupService {
    private final OrderTableDao orderTableDao;
    private final TableGroupDao tableGroupDao;

    public TableGroupService(final OrderTableDao orderTableDao, final TableGroupDao tableGroupDao) {
        this.orderTableDao = orderTableDao;
        this.tableGroupDao = tableGroupDao;
    }

    @Transactional
    public TableGroup create(final TableGroupRequest request) {
        final OrderTables orderTables = findOrderTables(request.getOrderTables());
        orderTables.group();
        final TableGroup tableGroup = new TableGroup(orderTables);
        return tableGroupDao.save(tableGroup);
    }

    private OrderTables findOrderTables(final OrderTables orderTables) {
        final List<Long> orderTableIds = orderTables.getOrderTables()
                .stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());
        final List<OrderTable> savedOrderTables = orderTableDao.findAllByIdIn(orderTableIds);
        validateOrderTables(orderTables, savedOrderTables);
        return new OrderTables(savedOrderTables);
    }

    private void validateOrderTables(final OrderTables orderTables, final List<OrderTable> savedOrderTables) {
        if (orderTables.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final OrderTables orderTables = new OrderTables(orderTableDao.findAllByTableGroupId(tableGroupId));
        orderTables.ungroup();
        orderTableDao.saveAll(orderTables);
    }
}
