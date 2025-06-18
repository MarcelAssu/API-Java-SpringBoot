package br.edu.atitus.api_sample.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.atitus.api_sample.entities.PointEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.PointRepository;

@Service
public class PointService {

	private final PointRepository repository;

	public PointService(PointRepository repository) {
		super();
		this.repository = repository;
	}

	@Transactional
	public PointEntity save(PointEntity point) throws Exception {
		if (point == null)
			throw new Exception("O ponto não pode ser nulo.");

		if (point.getDescription() == null || point.getDescription().isEmpty())
			throw new Exception("Descrição obrigatória.");
		point.setDescription(point.getDescription().trim());

		if (point.getLatitude() < -90 || point.getLatitude() > 90)
			throw new Exception("Latitude inválida.");
		if (point.getLongitude() < -180 || point.getLongitude() > 180)
			throw new Exception("Longitude inválida.");

		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		point.setUser(userAuth);
		return repository.save(point);
	}
	
	public void deleteById(UUID id) throws Exception{
		
		var point = repository.findById(id) 
				.orElseThrow(() -> new Exception("Não existe ponto cadastrado com esse ID"));
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (!point.getUser().getId().equals(userAuth.getId()))
			throw new Exception("Voce nao tem permissão para pagar este registro");
		
		repository.deleteById(id);
	}
	
	public List<PointEntity> findAll(){
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return repository.findByUser(userAuth);
	}
}