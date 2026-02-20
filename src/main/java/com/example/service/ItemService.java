package com.example.service;

import com.example.model.Item;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory service for managing Items.
 * In a real application, this would use a database.
 */
@ApplicationScoped
public class ItemService {

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        // Add some sample data
        Item item1 = new Item("Laptop", "High-performance laptop for developers", 1299.99);
        Item item2 = new Item("Keyboard", "Mechanical keyboard with RGB", 149.99);
        Item item3 = new Item("Monitor", "27-inch 4K monitor", 449.99);
        
        items.put(item1.getId(), item1);
        items.put(item2.getId(), item2);
        items.put(item3.getId(), item3);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public Optional<Item> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item create(Item item) {
        if (item.getId() == null || item.getId().isBlank()) {
            item = new Item(item.getName(), item.getDescription(), item.getPrice());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> update(String id, Item updatedItem) {
        return findById(id).map(existing -> {
            existing.setName(updatedItem.getName());
            existing.setDescription(updatedItem.getDescription());
            existing.setPrice(updatedItem.getPrice());
            existing.setUpdatedAt(Instant.now());
            return existing;
        });
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    public long count() {
        return items.size();
    }
}
