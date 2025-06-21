package br.edu.atitus.api_sample.dtos;


public record RestaurantDTO(
    String name,
    String description,
    String phone,
    String address
) {}