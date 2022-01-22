package ru.snsin.apisec.items;

import java.util.Collection;
import java.util.List;

public interface ItemsService {

    ItemDto createItem(ItemDto item);

    List<ItemDto> getItems(Collection<Long> itemIds);
}
