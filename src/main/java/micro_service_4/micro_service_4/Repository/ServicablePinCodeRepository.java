package micro_service_4.micro_service_4.Repository;

import micro_service_4.micro_service_4.Modules.ServiceablePinCode;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ServicablePinCodeRepository extends CrudRepository<ServiceablePinCode, Integer> {
}
