package br.edu.atitus.api_sample.services;

import br.edu.atitus.api_sample.entities.RestaurantEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.entities.UserType;
import br.edu.atitus.api_sample.repositories.RestaurantRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RestaurantEntity save(RestaurantEntity restaurant) throws Exception {
        UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        
        if (userAuth.getType() != UserType.RestaurantOwner && userAuth.getType() != UserType.Admin) {
            throw new Exception("Apenas Donos de Restaurante ou Administradores podem cadastrar/atualizar restaurantes.");
        }

        if (restaurant == null) {
            throw new Exception("O restaurante não pode ser nulo.");
        }
        if (restaurant.getName() == null || restaurant.getName().isEmpty()) {
            throw new Exception("Nome do restaurante é obrigatório.");
        }
        restaurant.setName(restaurant.getName().trim());

       
        if (restaurant.getId() != null) {
            Optional<RestaurantEntity> existingRestaurantOpt = repository.findById(restaurant.getId());
            if (existingRestaurantOpt.isEmpty()) {
                throw new Exception("Restaurante com ID " + restaurant.getId() + " não encontrado para atualização.");
            }
            RestaurantEntity existingRestaurant = existingRestaurantOpt.get();
            if (!existingRestaurant.getUser().getId().equals(userAuth.getId()) && userAuth.getType() != UserType.Admin) {
                throw new Exception("Você não tem permissão para alterar este restaurante.");
            }
            
            restaurant.setUser(existingRestaurant.getUser());
        } else {
            
            restaurant.setUser(userAuth);
        }

        return repository.save(restaurant);
    }

    public List<RestaurantEntity> findAll() {
        return repository.findAll(); 
    }

    public Optional<RestaurantEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) throws Exception {
        UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<RestaurantEntity> restaurantOpt = repository.findById(id);

        if (restaurantOpt.isEmpty()) {
            throw new Exception("Não existe restaurante cadastrado com esse ID.");
        }

        RestaurantEntity restaurant = restaurantOpt.get();

        
        if (!restaurant.getUser().getId().equals(userAuth.getId()) && userAuth.getType() != UserType.Admin) {
            throw new Exception("Você não tem permissão para deletar este restaurante.");
        }

        repository.deleteById(id);
    }
}