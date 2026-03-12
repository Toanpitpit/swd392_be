package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.IdentityDocument;
import fa.training.car_rental_management.repository.IdentityDocumentRepository;
import fa.training.car_rental_management.services.IdentityDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class IdentityDocumentServiceImpl implements IdentityDocumentService {

    @Autowired
    private IdentityDocumentRepository identityDocumentRepository;

    @Override
    public IdentityDocument createIdentityDocument(IdentityDocument document) {
        log.info("Creating identity document for user: {}", document.getUserId());
        return identityDocumentRepository.save(document);
    }

    @Override
    public Optional<IdentityDocument> getIdentityDocumentById(Integer id) {
        log.info("Fetching identity document with id: {}", id);
        return identityDocumentRepository.findById(id);
    }

    @Override
    public List<IdentityDocument> getIdentityDocumentsByUserId(Integer userId) {
        log.info("Fetching identity documents for user: {}", userId);
        return identityDocumentRepository.findByUserId(userId);
    }

    @Override
    public List<IdentityDocument> getAllIdentityDocuments() {
        log.info("Fetching all identity documents");
        return identityDocumentRepository.findAll();
    }

    @Override
    public IdentityDocument updateIdentityDocument(IdentityDocument document) {
        log.info("Updating identity document with id: {}", document.getId());
        return identityDocumentRepository.save(document);
    }

    @Override
    public void deleteIdentityDocument(Integer id) {
        log.info("Deleting identity document with id: {}", id);
        identityDocumentRepository.deleteById(id);
    }
}

