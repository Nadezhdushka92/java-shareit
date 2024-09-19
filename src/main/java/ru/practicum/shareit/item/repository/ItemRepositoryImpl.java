package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepository userRepository;
    private final Map<Long, List<Item>> userItems = new HashMap<>();
    private long itemId = 0;

//    @Autowired
//    public ItemRepositoryImpl(@Lazy UserRepository userRepository, @Lazy ItemRepository itemRepository) {
//        this.userRepository = userRepository;
//        this.itemRepository = itemRepository;
//    }

    @Override
    public List<Item> findItemsByUserId(long id){
        return userItems.getOrDefault(id, new ArrayList<>());
    }

    @Override
    public Item findItemById(long itemId) {
        log.debug("Поиск item с id: {} ", itemId);
        Item item = null;
        for (long userId : userItems.keySet()) {
            item = userItems.get(userId).stream().filter(items -> items.getId() == itemId).findFirst().orElse(null);
        }
        return item;
    }

    @Override
    public Item save(long userId, Item item){
        item.setId(++itemId); //item.size()+1
        item.setOwner((userRepository.getById(userId)));
        userItems.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        log.debug("Добавление item: {}", item);
        int index = findItemIndexInList(itemId, userId);
        return userItems.get(userId).get(index);
    }

    @Override
    public Item update(Long userId, Item item) {
        log.debug("Обновление item : {}", item);
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new NotFoundException("Владелец некорректный");
        }

        int index = findItemIndexInList(itemId, userId);
        userItems.get(userId).set(index, item);
        return userItems.get(userId).get(index);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        List<Item> availableItems = new ArrayList<>();
        for (long userId : userItems.keySet()) {
            availableItems.addAll(userItems.get(userId).stream()
                    .filter(item -> item.getAvailable().equals(true) && item.getName().toLowerCase().contains(text) ||
                    item.getDescription().toLowerCase().contains(text))
                    .toList());
        }
        return availableItems;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        List<Item> items = userItems.get(userId);
        if (items != null) {
            items.removeIf(item -> item.getId() == itemId);
            userItems.put(userId, items);
        }
    }

    private int findItemIndexInList(long itemId, long userId) {
        return IntStream.range(0, userItems.get(userId).size())
                .filter(i -> userItems.get(userId).get(i).getId() == itemId)
                .findFirst()
                .orElse(-1);
    }

}