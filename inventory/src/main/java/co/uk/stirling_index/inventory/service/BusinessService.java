package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.Business;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    public boolean isBusinessValid(Business business) throws IllegalArgumentException {
        if (business == null || business.getBusiness_id().toString().isEmpty() || business.getName() == null || business.getEmail() == null) {
            throw new IllegalArgumentException("Business is null, or ID, name or email is invalid" + business);
        }

        return businessRepository.existsByUUID(business.getBusiness_id());
    }

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
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
