package at.softwarecraftsmen.persistenceshowcase.persistence;

import at.softwarecraftsmen.persistenceshowcase.entity.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotographerRepository extends JpaRepository<Photographer, Long> {

}