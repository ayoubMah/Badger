package upec.badge.core_operational_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upec.badge.core_operational_backend.model.RegisteredPerson;
import upec.badge.core_operational_backend.repository.RegisteredPersonRepository;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final RegisteredPersonRepository repository;

    public PeopleController(RegisteredPersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RegisteredPerson> all() {
        return repository.findAll();
    }
}
