package it.epicode.segnoNome.modules.repositories;

import it.epicode.segnoNome.modules.entities.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
}
