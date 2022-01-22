package ru.snsin.apisec.items.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByIdIn(Collection<Long> longs);
}
