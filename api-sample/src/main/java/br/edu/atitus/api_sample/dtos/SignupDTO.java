package br.edu.atitus.api_sample.dtos;

import br.edu.atitus.api_sample.entities.UserType;

public record SignupDTO(String name, String email, String password, UserType userType) {

}