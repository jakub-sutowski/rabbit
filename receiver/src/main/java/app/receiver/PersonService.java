package app.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PersonService {

    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    @Value("${min_last_received}")
    private int MIN_LAST_RECEIVED;

    @RabbitListener(queues = "formQueue")
    @Transactional
    public void receiveMessage(FormRequest request) {
        Person person = Person.builder()
                .id(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .sex(request.getSex())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .city(request.getCity())
                .receivedDate(LocalDateTime.now())
                .build();

        log.info("New person saved");
        personRepository.save(person);
    }

    public List<PersonDto> getLastReceived() {
        LocalDateTime lastReceivedTime = LocalDateTime.now().minusMinutes(MIN_LAST_RECEIVED);
        List<Person> personList = personRepository.findByReceivedDateAfter(lastReceivedTime);
        return personList.stream().map(p -> modelMapper.map(p, PersonDto.class)).toList();
    }

    public List<PersonDto> getFilteredPeople(String sex, LocalDate after, LocalDate before) {
        Specification<Person> spec = Specification.where(null);

        if (sex != null) {
            String capitalizedSex = sex.substring(0, 1).toUpperCase() + sex.substring(1).toLowerCase();
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sex"), capitalizedSex));
        }

        if (after != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dateOfBirth"), after));
        }

        if (before != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dateOfBirth"), before));
        }

        List<Person> personList = personRepository.findAll(spec);
        return personList.stream().map(p -> modelMapper.map(p, PersonDto.class)).toList();
    }
}
