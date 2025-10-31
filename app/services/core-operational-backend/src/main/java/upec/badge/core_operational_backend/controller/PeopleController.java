package upec.badge.core_operational_backend.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upec.badge.core_operational_backend.model.RegisteredPerson;
import upec.badge.core_operational_backend.repository.RegisteredPersonRepository;
import upec.badge.core_operational_backend.service.EventProducer;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final RegisteredPersonRepository repository;
    private final EventProducer producer;

    public PeopleController(RegisteredPersonRepository repository, EventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    //@GetMapping
    //public List<RegisteredPerson> all() {
     //   return repository.findAll();
    //}


    @GetMapping("/{badgeId}")
    @Cacheable(value = "people", key = "#badgeId")
    public RegisteredPerson findByBadge(@PathVariable String badgeId) {
        RegisteredPerson person = repository.findByBadgeId(badgeId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        boolean granted = person.isActive();
        producer.publishBadgeEvent(badgeId, granted);
        return person;
    }

}
