package kitchenpos.application;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Optional;
import kitchenpos.menugroup.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MenuGroupService 클래스의")
class MenuGroupServiceTest extends ServiceTest {

    @Test
    @DisplayName("create 메서드는 메뉴 분류를 생성한다.")
    void create() {
        // given
        MenuGroup menuGroup = new MenuGroup("반마리치킨");

        // when
        MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);

        // then
        Optional<MenuGroup> actual = menuGroupRepository.findById(savedMenuGroup.getId());
        assertThat(actual).isPresent();
    }

    @Test
    @DisplayName("list 메서드는 모든 메뉴 분류를 조회한다.")
    void list() {
        // given
        saveMenuGroup("반마리치킨");
        saveMenuGroup("세마리치킨");

        // when
        List<MenuGroup> menuGroups = menuGroupService.list();

        // then
        assertThat(menuGroups).hasSize(2);
    }
}
