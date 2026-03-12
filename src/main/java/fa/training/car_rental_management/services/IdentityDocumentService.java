package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.IdentityDocument;

import java.util.List;
import java.util.Optional;

public interface IdentityDocumentService {
    IdentityDocument createIdentityDocument(IdentityDocument document);
    Optional<IdentityDocument> getIdentityDocumentById(Integer id);
    List<IdentityDocument> getIdentityDocumentsByUserId(Integer userId);
    List<IdentityDocument> getAllIdentityDocuments();
    IdentityDocument updateIdentityDocument(IdentityDocument document);
    void deleteIdentityDocument(Integer id);
}

