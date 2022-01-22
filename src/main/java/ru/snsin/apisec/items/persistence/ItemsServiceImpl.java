package ru.snsin.apisec.items.persistence;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.snsin.apisec.items.ItemDto;
import ru.snsin.apisec.items.ItemsService;

import javax.validation.Validator;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemsServiceImpl implements ItemsService {

    private final Validator validator;
    private final ItemsRepository itemsRepository;

    @Override
    public ItemDto createItem(ItemDto item) {
        if (item == null || !validator.validate(item).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var itemEntity = fromDto(item);
        var savedItem = itemsRepository.save(itemEntity);
        return fromEntity(savedItem);
    }

    @Override
    public List<ItemDto> getItems(Collection<Long> itemIds) {
        var items = itemsRepository.findAllByIdIn(itemIds);
        return items.stream()
                .map(this::fromEntity).toList();
    }

    private Item fromDto(ItemDto item) {
        var itemEntity = new Item();
        itemEntity.setTitle(item.getTitle());
        itemEntity.setPrice(item.getPrice());
        itemEntity.setOwner(item.getOwner());
        return itemEntity;
    }

    private ItemDto fromEntity(Item savedItem) {
        var itemDto = new ItemDto();
        itemDto.setId(savedItem.getId());
        itemDto.setTitle(savedItem.getTitle());
        itemDto.setPrice(savedItem.getPrice());
        itemDto.setOwner(savedItem.getOwner());
        return itemDto;
    }
}
