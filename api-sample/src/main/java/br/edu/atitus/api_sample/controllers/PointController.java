package br.edu.atitus.api_sample.controllers;

import java.util.UUID;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.atitus.api_sample.entities.PointEntity;
import br.edu.atitus.api_sample.services.PointService;
import br.edu.atitus.api_sample.dtos.PointDTO;

@RestController
@RequestMapping("/ws/point")
public class PointController {

    public final PointService pointService;

    public PointController(PointService pointService) {
        super();
    	this.pointService = pointService;
    }

    // GET /ws/point
    @GetMapping
    public ResponseEntity<List<PointEntity>> listAll() {
        return ResponseEntity.ok(pointService.findAll());
    }

    // POST /ws/point
    @PostMapping
    public ResponseEntity<PointEntity> create(@RequestBody PointDTO dto) throws Exception {
        PointEntity entity = new PointEntity();
        BeanUtils.copyProperties(dto, entity);

        PointEntity saved = pointService.save(entity);
        return ResponseEntity.status(201).body(saved);
    }

    // DELETE /ws/point/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) throws Exception {
        pointService.deleteById(id);
        return ResponseEntity.ok("Ponto removido com sucesso.");
    }
    
    
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handlerException(Exception ex) {
		String message = ex.getMessage().replaceAll("\r\n", "");
		return ResponseEntity.badRequest().body(message);
		}
}
