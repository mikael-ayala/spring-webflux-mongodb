package com.devsuperior.workshopmongo.services;

import com.devsuperior.workshopmongo.entities.User;
import com.devsuperior.workshopmongo.services.exceptioons.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsuperior.workshopmongo.dto.UserDTO;
import com.devsuperior.workshopmongo.repositories.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public Flux<UserDTO> findAll() {
		return repository.findAll().map(UserDTO::new);
	}

	public Mono<UserDTO> findById(String id) {
		return repository.findById(id)
			.map(existingUser -> new UserDTO(existingUser))
			.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
	}

	public Mono<UserDTO> insert(UserDTO userDTO) {
		User entity = new User();
		copyDtoToEntity(userDTO, entity);

		Mono<UserDTO> result = repository.save(entity).map(user -> new UserDTO(user));

		return result;
	}

	public Mono<UserDTO> update(String id, UserDTO userDTO) {
		return repository.findById(id)
				.flatMap(existingUser -> {
					existingUser.setName(userDTO.getName());
					existingUser.setEmail(userDTO.getEmail());
					return repository.save(existingUser);
				})
				.map(user -> new UserDTO(user))
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")));
	}

	public Mono<Void> delete(String id) {
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("Recurso não encontrado")))
				.flatMap(existingUser -> repository.delete(existingUser));
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
	}
}
