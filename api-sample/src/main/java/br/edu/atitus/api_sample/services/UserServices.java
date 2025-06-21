package br.edu.atitus.api_sample.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.UserRepository;

@Service
public class UserServices implements UserDetailsService{

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;


	public UserServices(UserRepository repository, PasswordEncoder passwordEncoder) {
		super();
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserEntity save(UserEntity user) throws Exception {
		if (user == null)
			throw new Exception("Objeto não pode ser nulo");

		if (user.getName() == null || user.getName().isEmpty())
			throw new Exception("Nome inválido");
		user.setName(user.getName().trim());

		if (user.getEmail() == null || user.getEmail().isEmpty())
			throw new Exception("E-mail inválido");
		user.setEmail(user.getEmail().trim().toLowerCase());

		
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})+$";
		if (!user.getEmail().matches(emailRegex)) {
		    throw new Exception("Formato de e-mail inválido. Deve conter @ e ao menos dois domínios (ex: email@dominio.com.br)");
		}

		if (user.getPassword() == null
				|| user.getPassword().isEmpty()
				|| user.getPassword().length() < 8)
			throw new Exception("Senha inválida. Deve ter no mínimo 8 caracteres.");

		
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";
		if (!user.getPassword().matches(passwordRegex)) {
		    throw new Exception("A senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número.");
		}

		if (user.getType() == null)
			throw new Exception("Tipo de usuário inválido");


		if(repository.existsByEmail(user.getEmail()))
			throw new Exception("Já existe usuário cadastrado com este e-mail");

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);

		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
	}

	public UserEntity signin(String email, String password) throws Exception {
		UserEntity user = (UserEntity) loadUserByUsername(email);

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new Exception("Senha inválida");
		}
		return user;
	}
}