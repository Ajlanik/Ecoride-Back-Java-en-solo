package mapper;

import dto.CarDTO;
import entity.Car;

public class CarMapper {

    public static CarDTO toDTO(Car car) {
        if (car == null) {
            return null;
        }
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setColor(car.getColor());
        dto.setEngine(car.getEngine());
        dto.setNumberOfSeat(car.getNumberOfSeat());
        dto.setPicture(car.getPicture()); // Utilisation du bon getter
        dto.setIsFavorite(car.getIsFavorite());
        dto.setPurchaseDate(car.getPurchaseDate());
        dto.setInsuranceDate(car.getInsuranceDate());
        return dto;
    }

    public static Car toEntity(CarDTO dto) {
        if (dto == null) {
            return null;
        }
        Car car = new Car();
        car.setId(dto.getId());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setLicensePlate(dto.getLicensePlate());
        car.setColor(dto.getColor());
        car.setEngine(dto.getEngine());
        car.setNumberOfSeat(dto.getNumberOfSeat());
        car.setPicture(dto.getPicture());
        car.setIsFavorite(dto.getIsFavorite());
        car.setPurchaseDate(dto.getPurchaseDate());
        car.setInsuranceDate(dto.getInsuranceDate());
        return car;
    }
}