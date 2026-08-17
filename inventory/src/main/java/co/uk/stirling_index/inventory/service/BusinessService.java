package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.exceptions.business.BusinessNotFoundException;
import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    public boolean isBusinessValid(Business business) throws IllegalArgumentException {
        if ((business == null) || business.getName() == null || business.getEmail() == null) {
            throw new IllegalArgumentException("Business is null, or ID, name or email is invalid" + business);
        }

        return businessRepository.existsById(business.getId());
    }

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public Business getBusinessById(UUID id) {
        return businessRepository.findById(id).orElseThrow(
                () -> new BusinessNotFoundException(id)
        );
    }

    public void addBusiness(Business business) {
        if (!isBusinessValid(business)) return;

        businessRepository.save(business);
    }

    public void updateBusiness(Business business) {
        businessRepository.save(business);
    }

    public void deleteBusiness(Business business) {
        businessRepository.delete(business);
    }
}
