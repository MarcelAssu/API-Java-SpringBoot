package br.edu.atitus.api_sample.controllers;

import br.edu.atitus.api_sample.dtos.RestaurantDTO;
import br.edu.atitus.api_sample.entities.RestaurantEntity;
import br.edu.atitus.api_sample.services.RestaurantService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ws/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantEntity> create(@RequestBody RestaurantDTO dto) throws Exception {
        RestaurantEntity entity = new RestaurantEntity();
        BeanUtils.copyProperties(dto, entity);
        RestaurantEntity saved = restaurantService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantEntity>> findAll() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantEntity> findById(@PathVariable UUID id) throws Exception {
        return restaurantService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new Exception("Restaurante com ID " + id + " não encontrado para este usuário."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantEntity> update(@PathVariable UUID id, @RequestBody RestaurantDTO dto) throws Exception {
        RestaurantEntity entity = new RestaurantEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id); 
        RestaurantEntity updated = restaurantService.save(entity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws Exception {
        restaurantService.deleteById(id);
        return ResponseEntity.noContent().build(); 
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}