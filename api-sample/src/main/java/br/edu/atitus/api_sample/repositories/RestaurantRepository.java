package br.edu.atitus.api_sample.repositories;

import br.edu.atitus.api_sample.entities.RestaurantEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);

    List<RestaurantEntity> findByUser(UserEntity user);


    Optional<RestaurantEntity> findByIdAndUser(UUID id, UserEntity user);
}