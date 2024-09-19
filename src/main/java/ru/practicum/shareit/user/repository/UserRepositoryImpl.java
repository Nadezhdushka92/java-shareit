package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DublicateException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmails = new HashSet<>();
    private long userId = 0;

    @Override
    public List<User> findAll() {
            return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(++userId); //(long) users.size() + 1
        if (!uniqueEmails.add(user.getEmail())) {
            --userId;
            throw new DublicateException("Email уже используется");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public User update(User user) {
        if (!user.getEmail().equals(users.get(user.getId()).getEmail())) {
            if (uniqueEmails.add(user.getEmail())) {
                uniqueEmails.remove(users.get(user.getId()).getEmail());
            } else {
                throw new DublicateException("Пользователь с данным e-mail уже существует");
            }
        }

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long id) {
        log.debug("Удаление пользователя с id : {}", id);
        uniqueEmails.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
