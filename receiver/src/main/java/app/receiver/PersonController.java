package app.receiver;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/people")
public class PersonController {

    private final PersonService personService;

    @GetMapping("/latest")
    public ResponseEntity<List<PersonDto>> getLastReceived() {
        List<PersonDto> lastReceived = personService.getLastReceived();
        return ResponseEntity.ok(lastReceived);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PersonDto>> getFilteredPeople(
            @RequestParam(name = "sex", required = false) @Pattern(regexp = "(male|female)", message = "Invalid sex value") String sex,
            @RequestParam(name = "after", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Past(message = "Date must be in the past") LocalDate after,
            @RequestParam(name = "before", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Past(message = "Date must be in the past") LocalDate before) {
        log.info("sex: {}, bornAfter: {}, bornBefore: {}", sex, after, before);
        List<PersonDto> filteredUsers = personService.getFilteredPeople(sex, after, before);
        return ResponseEntity.ok(filteredUsers);
    }

}
